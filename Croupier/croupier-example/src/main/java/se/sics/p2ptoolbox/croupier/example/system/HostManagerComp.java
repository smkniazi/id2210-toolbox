/*
 * Copyright (C) 2009 Swedish Institute of Computer Science (SICS) Copyright (C)
 * Copyright (C) 2009 Royal Institute of Technology (KTH)
 *
 * Croupier is free software; you can redistribute it and/or
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
package se.sics.p2ptoolbox.croupier.example.system;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.gvod.net.VodAddress;
import se.sics.gvod.net.VodNetwork;
import se.sics.gvod.timer.Timer;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.p2ptoolbox.croupier.api.CroupierControlPort;
import se.sics.p2ptoolbox.croupier.api.CroupierPort;
import se.sics.p2ptoolbox.croupier.api.CroupierSelectionPolicy;
import se.sics.p2ptoolbox.croupier.api.msg.CroupierDisconnected;
import se.sics.p2ptoolbox.croupier.api.msg.CroupierJoin;
import se.sics.p2ptoolbox.croupier.core.Croupier;
import se.sics.p2ptoolbox.croupier.core.CroupierConfig;
import se.sics.p2ptoolbox.croupier.example.core.ExampleComponentA;
import se.sics.p2ptoolbox.croupier.example.core.ExampleComponentB;
import se.sics.p2ptoolbox.serialization.filter.OverlayHeaderFilter;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class HostManagerComp extends ComponentDefinition {

    private static final Logger log = LoggerFactory.getLogger(HostManagerComp.class);
    
    private Positive<VodNetwork> network = requires(VodNetwork.class);
    private Positive<Timer> timer = requires(Timer.class);
    
    private Positive<CroupierControlPort> croupierControlPort1;
    private Positive<CroupierControlPort> croupierControlPort2;
    
    private VodAddress bootstrapNode;
    
    public HostManagerComp(HostManagerInit init) {
        this.bootstrapNode = init.bootstrap;
        
        CroupierConfig config1 = new CroupierConfig(4, 1000, 2, CroupierSelectionPolicy.RANDOM);
        Component croupier1 = create(Croupier.class, new Croupier.CroupierInit(config1, init.seed, 10, init.self));
        CroupierConfig config2 = new CroupierConfig(4, 2000, 2, CroupierSelectionPolicy.RANDOM);
        Component croupier2 = create(Croupier.class, new Croupier.CroupierInit(config2, init.seed, 11, init.self));
        
        Component compA = create(ExampleComponentA.class, new ExampleComponentA.ExampleInitA(init.seed));
        Component compB = create(ExampleComponentB.class, new ExampleComponentB.ExampleInitB(init.seed));
        
        connect(croupier1.getNegative(VodNetwork.class), network, new OverlayHeaderFilter(10));
        connect(croupier1.getNegative(Timer.class), timer);
        
        connect(croupier2.getNegative(VodNetwork.class), network, new OverlayHeaderFilter(11));
        connect(croupier2.getNegative(Timer.class), timer);
        
        connect(croupier1.getPositive(CroupierPort.class), compA.getNegative(CroupierPort.class));
        connect(croupier2.getPositive(CroupierPort.class), compB.getNegative(CroupierPort.class));
        
        croupierControlPort1 = croupier1.getPositive(CroupierControlPort.class);
        croupierControlPort2 = croupier2.getPositive(CroupierControlPort.class);
        subscribe(handleDisconnected, croupierControlPort1);
        subscribe(handleDisconnected, croupierControlPort2);
    
        subscribe(handleStart, control);
    }
    
    private Handler<Start> handleStart = new Handler<Start>() {

        @Override
        public void handle(Start event) {
            Set<VodAddress> bootstrapSet = new HashSet<VodAddress>();
            bootstrapSet.add(bootstrapNode);
            trigger(new CroupierJoin(UUID.randomUUID(), bootstrapSet), croupierControlPort1);
            
            bootstrapSet = new HashSet<VodAddress>();
            bootstrapSet.add(bootstrapNode);
            trigger(new CroupierJoin(UUID.randomUUID(), bootstrapSet), croupierControlPort2);
        }
        
    };
    
    private Handler<CroupierDisconnected> handleDisconnected = new Handler<CroupierDisconnected>() {

        @Override
        public void handle(CroupierDisconnected event) {
            log.info("croupier: {} disconnected", event.overlayId);
        }
        
    };
    
    public static class HostManagerInit extends Init<HostManagerComp> {

        public final long seed;
        public final VodAddress self;
        public final VodAddress bootstrap;
        
        public HostManagerInit(long seed, VodAddress self, VodAddress bootstrap) {
            this.seed = seed;
            this.self = self;
            this.bootstrap = bootstrap;
        }
    }
}
