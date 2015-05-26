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
package se.sics.p2ptoolbox.simulator.run;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.p2ptoolbox.simulator.core.P2pSimulator;
import se.sics.p2ptoolbox.simulator.core.P2pSimulatorInit;
import se.sics.p2ptoolbox.simulator.core.network.impl.UniformRandomModel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.network.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.simulation.SimulatorScheduler;
import se.sics.kompics.timer.Timer;
import se.sics.p2ptoolbox.simulator.ExperimentPort;
import se.sics.p2ptoolbox.simulator.SimMngrComponent;
import se.sics.p2ptoolbox.simulator.SystemStatusHandler;
import se.sics.p2ptoolbox.simulator.dsl.SimulationScenario;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class LauncherComp extends ComponentDefinition {
    private static final Logger log = LoggerFactory.getLogger(LauncherComp.class);
    
    public static SimulatorScheduler scheduler;
    public static SimulationScenario scenario;
    public static Address simulatorClientAddress;
    public static final Set<SystemStatusHandler> systemStatusHandlers = new HashSet<SystemStatusHandler>();
    
    {
        P2pSimulator.setSimulationPortType(ExperimentPort.class);
        Component simulator = create(P2pSimulator.class, new P2pSimulatorInit(scheduler, scenario, new UniformRandomModel(1, 10)));
        Component simManager = create(SimMngrComponent.class, new SimMngrComponent.SimMngrInit(new Random(), simulatorClientAddress, systemStatusHandlers));
        connect(simManager.getNegative(Network.class), simulator.getPositive(Network.class));
        connect(simManager.getNegative(Timer.class), simulator.getPositive(Timer.class));
        connect(simManager.getNegative(ExperimentPort.class), simulator.getPositive(ExperimentPort.class));
    }
}
