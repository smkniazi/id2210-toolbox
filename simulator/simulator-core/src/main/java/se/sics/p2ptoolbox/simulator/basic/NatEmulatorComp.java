/*
 * Copyright (C) 2009 Swedish Institute of Computer Science (SICS) Copyright (C)
 * 2009 Royal Institute of Technology (KTH)
 *
 * GVoD is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.sics.p2ptoolbox.simulator.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.Stop;
import se.sics.kompics.network.Address;
import se.sics.kompics.network.Msg;
import se.sics.kompics.network.Network;
import se.sics.p2ptoolbox.util.network.NatedAddress;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class NatEmulatorComp extends ComponentDefinition {

    private static final Logger log = LoggerFactory.getLogger(NatEmulatorComp.class);
    private Negative<Network> emulatedNat = provides(Network.class);
    private Positive<Network> network = requires(Network.class);

    private final NatedAddress selfAddress;

    public NatEmulatorComp(NatEmulatorInit init) {
        this.selfAddress = init.selfAddress;
        log.info("{} {} initiating ..... ", new Object[]{selfAddress.getId(), (selfAddress.isOpen() ? "OPEN" : "NATED")});

        subscribe(handleStart, control);
        subscribe(handleStop, control);
        subscribe(handleIncomingMsg, network);
        subscribe(handleOutgoingMsg, emulatedNat);
    }

    private Handler<Start> handleStart = new Handler<Start>() {

        @Override
        public void handle(Start event) {
            log.info("{} starting...", new Object[]{selfAddress.getId()});
        }

    };
    private Handler<Stop> handleStop = new Handler<Stop>() {

        @Override
        public void handle(Stop event) {
            log.info("{} stopping...", new Object[]{selfAddress.getId()});
        }

    };

    private Handler<Msg> handleIncomingMsg = new Handler<Msg>() {

        @Override
        public void handle(Msg msg) {
            Address src = msg.getHeader().getSource();
            if (!(src instanceof NatedAddress)) {
                throw new RuntimeException("started Nat Emulator with wrong address types");
            }
            NatedAddress natedSrc = (NatedAddress) src;
            log.trace("{} received msg from:{}", new Object[]{selfAddress.getId(), msg.getHeader().getSource()});
            if (!allowMsg(natedSrc)) {
                log.info("{} dropping msg from:{}", new Object[]{selfAddress, natedSrc});
                return;
            }
            trigger(msg, emulatedNat);
        }

    };
    
    private Handler<Msg> handleOutgoingMsg = new Handler<Msg>() {

        @Override
        public void handle(Msg msg) {
            log.trace("{} sending msg to:{}", new Object[]{selfAddress.getId(), msg.getHeader().getDestination()});
            trigger(msg, network);
        }
    };
    
    private boolean allowMsg(NatedAddress src) {
        if(selfAddress.isOpen()) {
            return true;
        }
        return selfAddress.getParents().contains(src);
    }

    public static class NatEmulatorInit extends Init<NatEmulatorComp> {

        public final NatedAddress selfAddress;

        public NatEmulatorInit(NatedAddress selfAddress) {
            this.selfAddress = selfAddress;
        }
    }
}
