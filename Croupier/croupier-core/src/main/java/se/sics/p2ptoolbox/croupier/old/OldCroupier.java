///*
// * Copyright (C) 2009 Swedish Institute of Computer Science (SICS) Copyright (C)
// * 2009 Royal Institute of Technology (KTH)
// *
// * GVoD is free software; you can redistribute it and/or
// * modify it under the terms of the GNU General Public License
// * as published by the Free Software Foundation; either version 2
// * of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
// */
//package se.sics.p2ptoolbox.croupier.old;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import se.sics.gvod.net.VodAddress;
//import se.sics.gvod.net.VodNetwork;
//import se.sics.gvod.timer.CancelPeriodicTimeout;
//import se.sics.gvod.timer.CancelTimeout;
//import se.sics.gvod.timer.SchedulePeriodicTimeout;
//import se.sics.gvod.timer.ScheduleTimeout;
//import se.sics.gvod.timer.TimeoutId;
//import se.sics.gvod.timer.Timer;
//import se.sics.kompics.ComponentDefinition;
//import se.sics.kompics.Handler;
//import se.sics.kompics.Init;
//import se.sics.kompics.Negative;
//import se.sics.kompics.Positive;
//import se.sics.p2ptoolbox.croupier.api.CroupierControlPort;
//import se.sics.p2ptoolbox.croupier.api.CroupierPort;
//import se.sics.p2ptoolbox.croupier.api.msg.CroupierDisconnected;
//import se.sics.p2ptoolbox.croupier.api.msg.CroupierJoin;
//import se.sics.p2ptoolbox.croupier.api.msg.CroupierSample;
//import se.sics.p2ptoolbox.croupier.api.msg.CroupierUpdate;
//import se.sics.p2ptoolbox.croupier.api.util.PeerView;
//import se.sics.p2ptoolbox.croupier.api.util.ViewFilter;
//import se.sics.p2ptoolbox.croupier.core.CroupierConfig;
//import se.sics.p2ptoolbox.croupier.core.CroupierConfig;
//import se.sics.p2ptoolbox.croupier.core.msg.Shuffle;
//import se.sics.p2ptoolbox.croupier.core.msg.ShuffleCycle;
//import se.sics.p2ptoolbox.croupier.core.msg.ShuffleNet;
//import se.sics.p2ptoolbox.croupier.core.msg.ShuffleTimeout;
//import se.sics.p2ptoolbox.croupier.core.net.CroupierNetworkSettings;
//import se.sics.p2ptoolbox.croupier.core.util.CroupierPV;
//import se.sics.p2ptoolbox.serialization.SerializationContext;
//
///**
// * @author Alex Ormenisan <aaor@sics.se>
// */
//public class OldCroupier extends ComponentDefinition {
//
//    private static final Logger logger = LoggerFactory.getLogger(OldCroupier.class);
//
//    Negative<CroupierControlPort> croupierControlPort = provides(CroupierControlPort.class);
//    Negative<CroupierPort> croupierPort = provides(CroupierPort.class);
//    Positive<VodNetwork> network = requires(VodNetwork.class);
//    Positive<Timer> timer = requires(Timer.class);
//
//    private final CroupierConfig config;
//    private final int overlayId;
//    private final VodAddress selfAddress;
//    private final String croupierLogPrefix;
//
//    private final List<VodAddress> bootstrapNodes;
//
//    private PeerView selfView;
//    private ViewFilter.Base prefferentialFilter;
//    private OldCroupierView prefferedView;
//    private OldCroupierView rawView;
//
//    private TimeoutId shuffleCycleTid;
//    private TimeoutId shuffleTimeoutTid;
//
//    public OldCroupier(CroupierInit init) {
//        //Croupier networking initialization
//        CroupierNetworkSettings.setContext(init.context);
//        if(!CroupierNetworkSettings.checkPreCond()) {
//            throw new RuntimeException("Croupier Network Settings are not correct");
//        }
//        CroupierNetworkSettings.registerSerializers();
//        
//        this.config = init.config;
//        this.overlayId = init.overlayId;
//        this.selfAddress = init.selfAddress;
//        this.croupierLogPrefix = "CROUPIER<" + selfAddress.getId() + ", " + overlayId + ">";
//
//        this.prefferentialFilter = null;
//        this.selfView = null;
//        this.shuffleCycleTid = null;
//
//        logger.info("{} creating", croupierLogPrefix);
//
//        this.prefferedView = new OldCroupierView(selfAddress, config.rand, config.viewSize);
//        this.rawView = new OldCroupierView(selfAddress, config.rand, config.viewSize);
//        this.bootstrapNodes = new ArrayList<VodAddress>();
//
//        subscribe(handleJoin, croupierControlPort);
//        subscribe(handleUpdate, croupierPort);
//        subscribe(handleShuffleRequest, network);
//        subscribe(handleShuffleResponse, network);
//        subscribe(handleCycle, timer);
//        subscribe(handleTimeout, timer);
//    }
//
//    Handler<CroupierJoin> handleJoin = new Handler<CroupierJoin>() {
//        @Override
//        public void handle(CroupierJoin join) {
//            logger.info("{} {}", croupierLogPrefix, join);
//            logger.debug("{} joining using nodes:{}", croupierLogPrefix, join.peers);
//
//            bootstrapNodes.addAll(join.peers);
//            if (bootstrapNodes.contains(selfAddress)) {
//                logger.warn("{} trying to bootstrap with myself", new Object[]{croupierLogPrefix, overlayId});
//                bootstrapNodes.remove(selfAddress);
//            }
//
//            tryInitiateShuffle();
//        }
//    };
//
//    Handler<CroupierUpdate> handleUpdate = new Handler<CroupierUpdate>() {
//        @Override
//        public void handle(CroupierUpdate update) {
//            logger.debug("{} updating selfView:{} and filter:{}", new Object[]{croupierLogPrefix, overlayId, update.selfView, update.filter});
//
//            selfView = (update.selfView == null ? selfView : update.selfView);
//            if (update.filter != null) {
//                prefferentialFilter = update.filter;
//                prefferedView = new OldCroupierView(selfAddress, config.rand, config.viewSize);
//            }
//
//            if (shuffleCycleTid == null) {
//                tryInitiateShuffle();
//            }
//        }
//    };
//
//    private void tryInitiateShuffle() {
//        if (selfView == null) {
//            logger.info("{} no self view - not shuffling", new Object[]{croupierLogPrefix, overlayId});
//            return;
//        }
//        if (bootstrapNodes.isEmpty()) {
//            logger.info("{} no insiders - not shuffling", new Object[]{croupierLogPrefix, overlayId});
//            return;
//        }
//        logger.info("{} initiating periodic shuffle", new Object[]{croupierLogPrefix, overlayId});
//        scheduleShuffleCycle();
//    }
//
//    private void scheduleShuffleTimeout(VodAddress dest) {
//        ScheduleTimeout spt = new ScheduleTimeout(config.shufflePeriod);
//        ShuffleTimeout sc = new ShuffleTimeout(spt, overlayId, dest);
//        spt.setTimeoutEvent(sc);
//        shuffleTimeoutTid = sc.getTimeoutId();
//
//        logger.debug("{} scheduling {}", new Object[]{croupierLogPrefix, sc});
//        trigger(spt, timer);
//    }
//
//    private void cancelShuffleTimeout() {
//        logger.debug("{} canceling shuffle timeout", new Object[]{croupierLogPrefix});
//        CancelTimeout cpt = new CancelTimeout(shuffleCycleTid);
//        trigger(cpt, timer);
//        shuffleTimeoutTid = null;
//    }
//    
//    private void scheduleShuffleCycle() {
//        SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(config.shufflePeriod, config.shufflePeriod);
//        ShuffleCycle sc = new ShuffleCycle(spt, overlayId);
//        spt.setTimeoutEvent(sc);
//        shuffleCycleTid = sc.getTimeoutId();
//
//        logger.debug("{} scheduling {} with period:{}", new Object[]{croupierLogPrefix, sc, config.shufflePeriod});
//        trigger(spt, timer);
//    }
//
//    private void cancelShuffleCycle() {
//        logger.debug("{} canceling shuffle cycle", new Object[]{croupierLogPrefix});
//        CancelPeriodicTimeout cpt = new CancelPeriodicTimeout(shuffleCycleTid);
//        trigger(cpt, timer);
//        shuffleCycleTid = null;
//    }
//
//    Handler<ShuffleCycle> handleCycle = new Handler<ShuffleCycle>() {
//        @Override
//        public void handle(ShuffleCycle timeout) {
//            logger.debug("{} {}", croupierLogPrefix, timeout);
//            logger.debug("{} preffered view size:{}, raw view size:{}", new Object[]{croupierLogPrefix, prefferedView.size(), rawView.size()});
//
//            prefferedView.incrementDescriptorAges();
//            rawView.incrementDescriptorAges();
//
//            if (!prefferedView.isEmpty() || !rawView.isEmpty()) {
//                trigger(new CroupierSample(UUID.randomUUID(), overlayId, prefferedView.getAllPVCopy(), rawView.getAllPVCopy()), croupierPort);
//            }
//
//            VodAddress peer = selectPeerToShuffleWith();
//            if (peer == null) {
//                logger.warn("{} disconnected", croupierLogPrefix);
//                trigger(new CroupierDisconnected(UUID.randomUUID(), overlayId), croupierControlPort);
//                cancelShuffleCycle();
//                return;
//            }
//
//            Set<PeerView> shuffleSet = new HashSet<PeerView>();
//            shuffleSet.addAll(prefferedView.selectCroupierPVToSend(config.shuffleLength, peer));
//            shuffleSet.addAll(rawView.selectCroupierPVToSend(config.shuffleLength, peer));
//            Shuffle content = new Shuffle(shuffleSet, new PeerView(selfView, selfAddress));
//            ShuffleNet.Request req = new ShuffleNet.Request(selfAddress, peer, UUID.randomUUID(), overlayId, content);
//            logger.debug("{} sending {}", croupierLogPrefix, req);
//            trigger(req, network);
//            scheduleShuffleTimeout(peer);
//        }
//    };
//
//    Handler<ShuffleTimeout> handleTimeout = new Handler<ShuffleTimeout>() {
//        @Override
//        public void handle(ShuffleTimeout timeout) {
//            logger.debug("{} shuffle to {} timeout", new Object[]{croupierLogPrefix, timeout.dest});
//            prefferedView.timedOut(timeout.dest);
//            rawView.timedOut(timeout.dest);
//        }
//    };
//
//    private VodAddress selectPeerToShuffleWith() {
//        if (!bootstrapNodes.isEmpty()) {
//            return bootstrapNodes.remove(0);
//        }
//
//        OldCroupierView selectView = null;
//        if (prefferedView.isEmpty() && rawView.isEmpty()) {
//            return null;
//        } else if (prefferedView.isEmpty()) {
//            selectView = rawView;
//        } else if (rawView.isEmpty()) {
//            selectView = prefferedView;
//        } else {
//            if (config.rand.nextDouble() < config.rawToPrefferedShuffleRatio) {
//                selectView = rawView;
//            } else {
//                selectView = prefferedView;
//            }
//        }
//
//        return selectView.selectPeerToShuffleWith(config.policy, true, 0.75d);
//    }
//
//    private boolean isPreffered(PeerView peer) {
//        if (prefferentialFilter instanceof ViewFilter.Simple) {
//            ViewFilter.Simple simpleFilter = (ViewFilter.Simple) prefferentialFilter;
//            return simpleFilter.apply(peer);
//        } else if (prefferentialFilter instanceof ViewFilter.CompareToSelf) {
//            ViewFilter.CompareToSelf compareToSelf = (ViewFilter.CompareToSelf) prefferentialFilter;
//            return compareToSelf.apply(selfView, peer);
//        }
//        throw new RuntimeException("unknown filter type");
//    }
//
//    Handler<ShuffleNet.Request> handleShuffleRequest = new Handler<ShuffleNet.Request>() {
//        @Override
//        public void handle(ShuffleNet.Request req) {
//            logger.debug("{} received {}", croupierLogPrefix, req);
//
//            Set<PeerView> shuffleSet = new HashSet<PeerView>();
//            shuffleSet.addAll(prefferedView.selectCroupierPVToSend(config.shuffleLength, req.getVodSource()));
//            shuffleSet.addAll(rawView.selectCroupierPVToSend(config.shuffleLength, req.getVodSource()));
//            Shuffle content = new Shuffle(shuffleSet, new PeerView(selfView, selfAddress));
//            ShuffleNet.Response resp = new ShuffleNet.Response(selfAddress, req.getVodSource(), req.id, overlayId, content);
//            logger.debug("{} sending {}", croupierLogPrefix, resp);
//            trigger(resp, network);
//
//            req.content.shuffleSet.add(req.content.self);
//            filterShuffleSet(req.getVodSource(), req.content.shuffleSet);
//        }
//    };
//
//    Handler<ShuffleNet.Response> handleShuffleResponse = new Handler<ShuffleNet.Response>() {
//        @Override
//        public void handle(ShuffleNet.Response resp) {
//            logger.debug("{} received {}", croupierLogPrefix, resp);
//            resp.content.shuffleSet.add(resp.content.self);
//            filterShuffleSet(resp.getVodSource(), resp.content.shuffleSet);
//        }
//    };
//
//    private void filterShuffleSet(VodAddress src, Set<PeerView> shuffleNodes) {
//        if (prefferentialFilter instanceof ViewFilter.NoFilter) {
//            rawView.selectToKeep(src, shuffleNodes);
//        } else {
//            Set<PeerView> prefferedSet = new HashSet<PeerView>();
//            for (PeerView cpv : shuffleNodes) {
//                if (isPreffered(cpv.pv)) {
//                    prefferedSet.add(cpv);
//                }
//            }
//            prefferedView.selectToKeep(src, prefferedSet);
//            rawView.selectToKeep(src, shuffleNodes);
//        }
//    }
//
//    public static class CroupierInit extends Init<OldCroupier> {
//
//        public final CroupierConfig config;
//        public final SerializationContext context;
//        public final int overlayId;
//        public final VodAddress selfAddress;
//
//        public CroupierInit(CroupierConfig config, SerializationContext context, int overlayId, VodAddress selfAddress) {
//            this.config = config;
//            this.context = context;
//            this.overlayId = overlayId;
//            this.selfAddress = selfAddress;
//        }
//    }
//}
