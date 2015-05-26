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

package se.sics.p2ptoolbox.croupier.example.core;

import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.p2ptoolbox.croupier.api.CroupierPort;
import se.sics.p2ptoolbox.croupier.api.msg.CroupierSample;
import se.sics.p2ptoolbox.croupier.api.msg.CroupierUpdate;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class ExampleComponentB extends ComponentDefinition {
    private static final Logger log = LoggerFactory.getLogger(ExampleComponentB.class);

    private Positive croupier = requires(CroupierPort.class);
   
    private final Random rand;
    private int counter = 0;
    
     public ExampleComponentB(ExampleInitB init) {
        this.rand = init.rand;
        subscribe(handleStart, control);
        subscribe(handleCroupierSample, croupier);
    }

    Handler<Start> handleStart = new Handler<Start>() {

        @Override
        public void handle(Start event) {
            log.info("ExampleComponentB starting, sending first croupier update");
            trigger(new CroupierUpdate(UUID.randomUUID(), new PeerViewB(counter)), croupier);
        }

    };

    Handler<CroupierSample> handleCroupierSample = new Handler<CroupierSample>() {

        @Override
        public void handle(CroupierSample sample) {
            log.info("ExampleComponentB croupier public sample {} \n croupier private sample{}", sample.publicSample, sample.privateSample);
            counter++;
            if(rand.nextDouble() > 0.7) {
                counter++;
                trigger(new CroupierUpdate(UUID.randomUUID(), new PeerViewB(counter)), croupier);
            }
        }
    };
    
    public static class ExampleInitB extends Init<ExampleComponentB> {
        public final Random rand;
        
        public ExampleInitB(long seed) {
            this.rand = new Random(seed + 2);
        }
    }
}
