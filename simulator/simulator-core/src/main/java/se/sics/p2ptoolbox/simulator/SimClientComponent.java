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
package se.sics.p2ptoolbox.simulator;

import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.Stop;
import se.sics.kompics.network.Msg;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.p2ptoolbox.simulator.cmd.NetworkOpCmd;
import se.sics.p2ptoolbox.simulator.cmd.OperationCmd;
import se.sics.p2ptoolbox.simulator.cmd.impl.SimulationResult;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class SimClientComponent extends ComponentDefinition {

    private static final Logger log = LoggerFactory.getLogger(SimClientComponent.class);

    private Positive<Network> network = requires(Network.class);
    private Positive<Timer> timer = requires(Timer.class);
    private Positive<ExperimentPort> experimentPort = requires(ExperimentPort.class);

    private final SimulationContextImpl simulationContext;

    private LinkedList<NetworkOpCmd> activeOps;
    private SimulationResult simResult;

    public SimClientComponent(SimClientInit init) {
        log.info("initiating...");
        this.simulationContext = init.simulationContext;
        this.activeOps = new LinkedList<NetworkOpCmd>();
        this.simResult = null;

        subscribe(handleStart, control);
        subscribe(handleStop, control);

        subscribe(handleNetworkOp, experimentPort);
        subscribe(handleNetworkOpResp, network);
        subscribe(handleSimulationResult, experimentPort);
    }

    //**********CONTROL HANDLERS************************************************
    private Handler<Start> handleStart = new Handler<Start>() {

        @Override
        public void handle(Start event) {
            log.info("starting...");
        }

    };

    private Handler<Stop> handleStop = new Handler<Stop>() {

        @Override
        public void handle(Stop event) {
            log.info("stopping...");

            if (simulationContext.canContinue() && (!activeOps.isEmpty())) {
                log.error("simulation ongoing...");
                throw new RuntimeException("simulation ongoing - tried to terminate experiment experiment too soon");
            }
            deliverResults();
        }

    };

    //**************************************************************************
    private Handler<NetworkOpCmd> handleNetworkOp = new Handler<NetworkOpCmd>() {

        @Override
        public void handle(NetworkOpCmd op) {
            log.info("received network op:{}", op);

            if (simulationContext.canContinue()) {
                activeOps.add(op);
                startOp(op);
            } else {
                log.warn("operation dropped - simulation error");
            }
        }
    };

    private void startOp(NetworkOpCmd op) {
        log.info("starting op:{}", op);
        op.beforeCmd(simulationContext);
        Msg msg = op.getNetworkMsg(simulationContext.getSimulatorAddress());
        log.debug("sending network msg:{}", msg);
        trigger(msg, network);
    }

    private void finishOp(NetworkOpCmd op, Msg resp) {
        try {
            op.validate(simulationContext, resp);
        } catch (OperationCmd.ValidationException ex) {
            simulationContext.fail(ex);
            return;
        }
        op.afterValidation(simulationContext);
        log.info("op:{} successfull", op);
    }

    private Handler<Msg> handleNetworkOpResp = new Handler<Msg>() {

        @Override
        public void handle(Msg resp) {
            log.info("received network op:{} response", resp);

            NetworkOpCmd activeOp = null;
            for (NetworkOpCmd op : activeOps) {
                if (op.myResponse(resp)) {
                    activeOp = op;
                    finishOp(activeOp, resp);
                    break;
                }
            }
            activeOps.remove(activeOp);
        }
    };

    private Handler<SimulationResult> handleSimulationResult = new Handler<SimulationResult>() {

        @Override
        public void handle(SimulationResult event) {
            log.info("received simulation result request");
            simResult = event;
        }

    };

    private void deliverResults() {
        log.info("delivering simulation results...");
        if (simResult == null) {
            log.error("no simulation result delivery");
            throw new RuntimeException("no SimulationResult event in scenario");
        }
        simResult.setSimulationResult(simulationContext.getSimulationResult());
    }

    //**************************************************************************
    public static class SimClientInit extends Init<SimClientComponent> {

        public final SimulationContextImpl simulationContext;

        public SimClientInit(SimulationContextImpl simulationContext) {
            this.simulationContext = simulationContext;
        }
    }
}
