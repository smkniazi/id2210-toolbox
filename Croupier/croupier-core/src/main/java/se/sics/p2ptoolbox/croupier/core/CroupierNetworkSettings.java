package se.sics.p2ptoolbox.croupier.core;

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
import java.util.UUID;
import se.sics.gvod.common.msgs.DirectMsgNetty;
import se.sics.gvod.net.VodAddress;
import se.sics.p2ptoolbox.croupier.api.util.CroupierPeerView;
import se.sics.p2ptoolbox.croupier.core.msg.Shuffle;
import se.sics.p2ptoolbox.croupier.core.msg.ShuffleNet;
import se.sics.p2ptoolbox.croupier.core.net.CroupierPVSerializer;
import se.sics.p2ptoolbox.croupier.core.net.ShuffleNetSerializer;
import se.sics.p2ptoolbox.croupier.core.net.ShuffleSerializer;
import se.sics.p2ptoolbox.serialization.SerializationContext;
import se.sics.p2ptoolbox.serialization.msg.NetMsg;
import se.sics.p2ptoolbox.serialization.msg.OverlayHeaderField;
import se.sics.p2ptoolbox.serialization.serializer.SerializerAdapter;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class CroupierNetworkSettings {

    private static SerializationContext context = null;
    private static final String NET_REQUEST_ALIAS = "CROUPIER_NET_REQUEST";
    private static final String NET_RESPONSE_ALIAS = "CROUPIER_NET_RESPONSE";

    public static void oneTimeSetup(SerializationContext setContext, byte croupierRequestAlias, byte croupierResponseAlias) {
        if(context != null) {
            throw new RuntimeException("croupier has already been setup - do not call this multiple times(for each croupier instance)");
        }
        context = setContext;

        registerNetworkMsg(croupierRequestAlias, croupierResponseAlias);
        registerOthers();
        
        checkSetup();
    }

    private static void registerNetworkMsg(byte croupierRequestAlias, byte croupierResponseAlias) {
        try {
            context.registerAlias(DirectMsgNetty.Request.class, NET_REQUEST_ALIAS, croupierRequestAlias);
            context.registerAlias(DirectMsgNetty.Response.class, NET_RESPONSE_ALIAS, croupierResponseAlias);

            context.registerSerializer(ShuffleNet.Request.class, new ShuffleNetSerializer.Request());
            context.registerSerializer(ShuffleNet.Response.class, new ShuffleNetSerializer.Response());

            context.multiplexAlias(NET_REQUEST_ALIAS, ShuffleNet.Request.class, (byte) 0x01);
            context.multiplexAlias(NET_RESPONSE_ALIAS, ShuffleNet.Response.class, (byte) 0x01);
        } catch (SerializationContext.DuplicateException ex) {
            throw new RuntimeException(ex);
        } catch (SerializationContext.MissingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void registerOthers() {
        try {
            context.registerSerializer(Shuffle.class, new ShuffleSerializer());
            context.registerSerializer(CroupierPeerView.class, new CroupierPVSerializer());
        } catch (SerializationContext.DuplicateException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void checkSetup() {
        if (context == null || !NetMsg.hasContext() || !SerializerAdapter.hasContext()) {
            throw new RuntimeException("serialization context not set");
        }

        try {
            for (OtherSerializers serializedClass : OtherSerializers.values()) {
                context.getSerializer(serializedClass.serializedClass);
            }
        } catch (SerializationContext.MissingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
//    public static enum MsgAliases {
//
//        CROUPIER_NET_REQUEST(DirectMsgNetty.Request.class), CROUPIER_NET_RESPONSE(DirectMsgNetty.Response.class);
//        public final Class aliasedClass;
//
//        MsgAliases(Class aliasedClass) {
//            this.aliasedClass = aliasedClass;
//        }
//    }

    public static enum OtherSerializers {
        UUID(UUID.class), VOD_ADDRESS(VodAddress.class), OVERLAY_HEADER_FIELD(OverlayHeaderField.class);
        
        public final Class serializedClass;

        OtherSerializers(Class serializedClass) {
            this.serializedClass = serializedClass;
        }
    }
}
