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
//import se.sics.p2ptoolbox.croupier.api.CroupierSelectionPolicy;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//import org.javatuples.Pair;
//import se.sics.gvod.net.VodAddress;
//import se.sics.p2ptoolbox.croupier.api.util.PeerView;
//
///**
// * @author Alex Ormenisan <aaor@sics.se>
// */
//public class OldCroupierView {
//
//    private final VodAddress selfAddress;
//    private final Random rand;
//    private final int viewSize;
//    private final Map<VodAddress, PeerView> entries;
//
//    private final Comparator<PeerView> comparatorByAge = new Comparator<PeerView>() {
//        @Override
//        public int compare(PeerView o1, PeerView o2) {
//            if (o1.getAge() > o2.getAge()) {
//                return 1;
//            } else if (o1.getAge() < o2.getAge()) {
//                return -1;
//            } else {
//                return 0;
//            }
//        }
//    };
//
//    public OldCroupierView(VodAddress selfAddress, Random rand, int viewSize) {
//        this.selfAddress = selfAddress;
//        this.rand = rand;
//        this.viewSize = viewSize;
//        this.entries = new HashMap<VodAddress, PeerView>();
//    }
//
//    public void incrementDescriptorAges() {
//        for (PeerView cpv : entries.values()) {
//            cpv.incrementAge();
//        }
//    }
//
//    private void addYoungest(PeerView cpv) {
//        PeerView oldCPV = entries.get(cpv.src);
//        if (oldCPV != null) {
//            if (cpv.getAge() < oldCPV.getAge()) {
//                entries.put(cpv.src, cpv);
//            }
//        }
//    }
//
//    public VodAddress selectPeerToShuffleWith(CroupierSelectionPolicy policy, boolean softmax, double temperature) {
//        if (entries.isEmpty()) {
//            return null;
//        }
//
//        PeerView selectedEntry = null;
//
//        if (!softmax || policy == CroupierSelectionPolicy.RANDOM) {
//            if (policy == CroupierSelectionPolicy.SWAPPER) {
//                selectedEntry = Collections.max(entries.values(), comparatorByAge);
//            } else if (policy == CroupierSelectionPolicy.HEALER) {
//                selectedEntry = Collections.max(entries.values(), comparatorByAge);
//            } else if (policy == CroupierSelectionPolicy.RANDOM) {
//                List<PeerView> entriesAsList = new ArrayList<PeerView>(entries.values());
//                selectedEntry = entriesAsList.get(rand.nextInt(entriesAsList.size()));
//            } else {
//                throw new IllegalArgumentException("Invalid Croupier policy selected:" + policy);
//            }
//        } else {
//            List<PeerView> tempEntries = new ArrayList<PeerView>(entries.values());
//            if (policy == CroupierSelectionPolicy.SWAPPER) {
//                Collections.sort(tempEntries, comparatorByAge);
//            } else if (policy == CroupierSelectionPolicy.HEALER) {
//                Collections.sort(tempEntries, comparatorByAge);
//            } else {
//                throw new IllegalArgumentException("Invalid Croupier policy selected:" + policy);
//            }
//
//            double rnd = rand.nextDouble();
//            double total = 0.0d;
//            double[] values = new double[tempEntries.size()];
//            int j = tempEntries.size() + 1;
//            for (int i = 0; i < tempEntries.size(); i++) {
//                // get inverse of values - lowest have highest value.
//                double val = j;
//                j--;
//                values[i] = Math.exp(val / temperature);
//                total += values[i];
//            }
//
//            for (int i = 0; i < values.length; i++) {
//                if (i != 0) {
//                    values[i] += values[i - 1];
//                }
//                // normalise the probability
//                double normalisedReward = values[i] / total;
//                if (normalisedReward >= rnd) {
//                    selectedEntry = tempEntries.get(i);
//                    break;
//                }
//            }
//            if (selectedEntry == null) {
//                selectedEntry = tempEntries.get(tempEntries.size() - 1);
//            }
//        }
//
//        // TODO - by not removing a reference to the node I am shuffling with, we
//        // break the 'batched random walk' (Cyclon) behaviour. But it's more important
//        // to keep the graph connected.
//        if (entries.size() >= viewSize) {
//            entries.remove(selectedEntry.src);
//        }
//
//        return selectedEntry.src;
//    }
//
//    public Set<PeerView> selectCroupierPVToSend(int count, VodAddress dest) {
//        Set<PeerView> descriptors = new HashSet<PeerView>();
//
//        for (Pair<PeerView, OldCroupierPVHistory> entry : generateRandomSample(count)) {
//            entry.getValue1().sentTo(dest);
//            descriptors.add(entry.getValue0());
//        }
//        return descriptors;
//    }
//
//    public void selectToKeep(Pair<Integer, Integer> healerSwapper, VodAddress from, Set<PeerView> cpvSet) {
//        if (from.equals(selfAddress)) {
//            return;
//        }
//        LinkedList<PeerView> entriesSentToThisPeer = new LinkedList<PeerView>();
//        PeerView fromEntry = entries.get(from);
//
//        for (PeerView cpv : cpvSet) {
//            if (selfAddress.equals(cpv.src)) {
//                continue;
//            }
//            addYoungest(cpv);
//        }
//
//        viewSelection(healerSwapper, cpvSet);
//    }
//    
//    private void viewSelection(CroupierSelectionPolicy policy, Set<PeerView> cpvSet) {
////        switch(policy) {
////            case RANDOM :
////                Set<CroupierPV> toDelete = generateRandomSample(viewSize)
////        }
////        
////        while (entries.size() > viewSize) {
////            Set<CroupierPV> toDelete = generateRandomSample(viewSize/3);
////            toDelete.removeAll(cpvSet);
////            
////            Iterator<CroupierPV> it = toDelete.iterator();
////            while(it.hasNext()) {
////                .removeAll();
////            }
////        }
//    }
//
//    public final Set<PeerView> getAllPVCopy() {
//        Set<PeerView> pvSet = new HashSet<PeerView>();
//        for (PeerView cpv : entries.values()) {
//            pvSet.add(cpv.pv.deepCopy());
//        }
//        return pvSet;
//    }
//
//    public final Set<PeerView> getAllCroupierPV() {
//        Set<PeerView> cpvSet = new HashSet<PeerView>();
//        for (PeerView cpv : entries.values()) {
//            cpvSet.add(cpv);
//        }
//        return cpvSet;
//    }
//
//    private Set<Pair<PeerView, OldCroupierPVHistory>> generateRandomSample(int n) {
//        if (n >= d2e.size()) {
//            // return all entries
//            return new HashSet<Pair<PeerView, OldCroupierPVHistory>>(d2e.values());
//        } else {
//            Set<Pair<PeerView, OldCroupierPVHistory>> randomEntries = new HashSet<Pair<PeerView, OldCroupierPVHistory>>();
//            List<Pair<PeerView, OldCroupierPVHistory>> entries = new ArrayList<Pair<PeerView, OldCroupierPVHistory>>(d2e.values());
//
//            //TODO Alex - legacy code - is this ok?
//            // Don Knuth, The Art of Computer Programming, Algorithm S(3.4.2)
//            int t = 0, m = 0, N = d2e.size();
//            while (m < n) {
//                int x = rand.nextInt(N - t);
//                if (x < n - m) {
//                    randomEntries.add(entries.get(t));
//                    m += 1;
//                    t += 1;
//                } else {
//                    t += 1;
//                }
//            }
//            return randomEntries;
//        }
//    }
//
//    public void timedOut(VodAddress node) {
//        d2e.remove(node);
//    }
//
//    public boolean isEmpty() {
//        return d2e.isEmpty();
//    }
//
//    public int size() {
//        return d2e.size();
//    }
//}
