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
package se.sics.p2ptoolbox.simulator.example.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import se.sics.p2ptoolbox.util.network.impl.BasicAddress;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class MyComponent extends ComponentDefinition {

    private static final Logger log = LoggerFactory.getLogger(MyComponent.class);

    private Positive<Network> network = requires(Network.class);
    private Positive<Timer> timer = requires(Timer.class);

    private BasicAddress self;
    private BasicAddress statusServer;

    public MyComponent(MyInit init) {
        log.debug("initiating test node:{}", init.self);

        this.self = init.self;
        this.statusServer = init.statusServer;

        subscribe(handleStart, control);
        subscribe(handleNetPing, network);
        subscribe(handleNetPong, network);
    }

    private Handler<Start> handleStart = new Handler<Start>() {

        @Override
        public void handle(Start event) {
            log.debug("starting test node:{}", self);
        }

    };

    private Handler<MyNetMsg.NetPing> handleNetPing = new Handler<MyNetMsg.NetPing>() {

        @Override
        public void handle(MyNetMsg.NetPing ping) {
            log.debug("{} received net ping from {}", self, ping.getHeader().getSource());
            trigger(new MyNetMsg.NetPong(self, ping.getHeader().getSource(), ping.getContent().id), network);
            
            log.info("sending status msgs");
//            trigger(new MyNetMsg.NetStatus1(self, statusServer), network);
//            trigger(new MyNetMsg.NetStatus2(self, statusServer), network);
        }
    };

    private Handler<MyNetMsg.NetPong> handleNetPong = new Handler<MyNetMsg.NetPong>() {

        @Override
        public void handle(MyNetMsg.NetPong event) {
            log.debug("{} received net pong from {}", self, event.getHeader().getSource());
        }
    };

//    private void scheduleStatusTimeout() {
//        SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(1000, 1000);
//        StatusTimeout timeout = new StatusTimeout(spt);
//        spt.setTimeoutEvent(timeout);
//
//        log.debug("scheduling timeout {}", timeout);
//        trigger(spt, timer);
//    }

    public static class MyInit extends Init<MyComponent> {

        public final BasicAddress self;
        public final BasicAddress statusServer;

        public MyInit(BasicAddress self, BasicAddress statusServer) {
            this.self = self;
            this.statusServer = statusServer;
        }
    }
    
    public static class StatusTimeout extends Timeout {
        public StatusTimeout(SchedulePeriodicTimeout spt) {
            super(spt);
        }
    }
}
