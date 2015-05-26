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
package se.sics.p2ptoolbox.croupier.example.system;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.gvod.address.Address;
import se.sics.gvod.net.NatNetworkControl;
import se.sics.gvod.net.NettyInit;
import se.sics.gvod.net.NettyNetwork;
import se.sics.gvod.net.Transport;
import se.sics.gvod.net.VodAddress;
import se.sics.gvod.net.VodNetwork;
import se.sics.gvod.net.events.PortBindRequest;
import se.sics.gvod.net.events.PortBindResponse;
import se.sics.gvod.timer.Timer;
import se.sics.gvod.timer.java.JavaTimer;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Kompics;
import se.sics.kompics.Start;
import se.sics.kompics.nat.utils.getip.ResolveIp;
import se.sics.kompics.nat.utils.getip.ResolveIpPort;
import se.sics.kompics.nat.utils.getip.events.GetIpRequest;
import se.sics.kompics.nat.utils.getip.events.GetIpResponse;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class Launcher extends ComponentDefinition {

    private static final Logger log = LoggerFactory.getLogger(Launcher.class);

    private static final int seed = 1234;
    private static final int port = 23432;
    private static int id;
    private static VodAddress bootstrapNode;

    public static void setArgs(int idArg, String bootstrapIp, int bootstrapId) {
        id = idArg;
        try {
            bootstrapNode = new VodAddress(new Address(InetAddress.getByName(bootstrapIp), port, bootstrapId), -1);
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Component timer;
    private Component resolveIp;
    private Component network;
    private Component manager;

    private Address selfAddress;

    public Launcher() {
        log.info("init");
        subscribe(handleStart, control);

        ExampleNetFrameDecoder.init();

        timer = create(JavaTimer.class, Init.NONE);
        resolveIp = create(ResolveIp.class, Init.NONE);

        connect(resolveIp.getNegative(Timer.class), timer.getPositive(Timer.class));
        subscribe(handleGetIpResponse, resolveIp.getPositive(ResolveIpPort.class));
    }

    public Handler<Start> handleStart = new Handler<Start>() {

        @Override
        public void handle(Start event) {
            log.info("phase 1 - getting ip");
            trigger(new GetIpRequest(false), resolveIp.getPositive(ResolveIpPort.class));
        }
    };

    private void phase2(InetAddress selfIp) {
        log.info("phase 2 - ip:{} - binding port:{}", selfIp, port);
        selfAddress = new Address(selfIp, port, id);

        network = create(NettyNetwork.class, new NettyInit(seed, true, ExampleNetFrameDecoder.class));
        connect(network.getNegative(Timer.class), timer.getPositive(Timer.class));

        subscribe(handlePsPortBindResponse, network.getPositive(NatNetworkControl.class));
        trigger(Start.event, network.getControl());
    }

    private void phase3(Address selfAddress) {
        log.info("phase 3 - starting with Address: {}", selfAddress);
        Component hostMngr = create(HostManagerComp.class, new HostManagerComp.HostManagerInit(seed, new VodAddress(selfAddress, -1), bootstrapNode));
        connect(hostMngr.getNegative(VodNetwork.class), network.getPositive(VodNetwork.class));
        connect(hostMngr.getNegative(Timer.class), timer.getPositive(Timer.class));

        trigger(Start.event, hostMngr.control());
    }

    public Handler<GetIpResponse> handleGetIpResponse = new Handler<GetIpResponse>() {
        @Override
        public void handle(GetIpResponse resp) {
            phase2(resp.getIpAddress());
            BootstrapPortBind.Request pb1 = new BootstrapPortBind.Request(selfAddress, Transport.UDP);
            pb1.setResponse(new BootstrapPortBind.Response(pb1));
            trigger(pb1, network.getPositive(NatNetworkControl.class));
        }
    };

    public Handler<BootstrapPortBind.Response> handlePsPortBindResponse = new Handler<BootstrapPortBind.Response>() {

        @Override
        public void handle(BootstrapPortBind.Response resp) {
            if (resp.getStatus() != PortBindResponse.Status.SUCCESS) {
                log.warn("Couldn't bind to port {}. Either another instance of the program is"
                        + "already running, or that port is being used by a different program. Go"
                        + "to settings to change the port in use. Status: ", resp.getPort(), resp.getStatus());
                Kompics.shutdown();
                System.exit(-1);
            } else {
                phase3(resp.boundAddress);
            }
        }
    };

    private static class BootstrapPortBind {

        private static class Request extends PortBindRequest {

            public final Address boundAddress;

            public Request(Address address, Transport transport) {
                super(address, transport);
                this.boundAddress = address;
            }
        }

        private static class Response extends PortBindResponse {

            public final Address boundAddress;

            public Response(Request req) {
                super(req);
                this.boundAddress = req.boundAddress;
            }
        }
    }
}