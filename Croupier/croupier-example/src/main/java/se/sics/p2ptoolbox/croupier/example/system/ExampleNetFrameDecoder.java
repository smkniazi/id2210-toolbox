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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;
import se.sics.gvod.common.msgs.MessageDecodingException;
import se.sics.gvod.net.BaseMsgFrameDecoder;
import se.sics.gvod.net.VodAddress;
import se.sics.gvod.net.msgs.RewriteableMsg;
import se.sics.p2ptoolbox.croupier.api.util.PeerView;
import se.sics.p2ptoolbox.croupier.core.CroupierNetworkSettings;
import se.sics.p2ptoolbox.croupier.example.core.PeerViewA;
import se.sics.p2ptoolbox.croupier.example.core.PeerViewASerializer;
import se.sics.p2ptoolbox.croupier.example.core.PeerViewB;
import se.sics.p2ptoolbox.croupier.example.core.PeerViewBSerializer;
import se.sics.p2ptoolbox.serialization.SerializationContext;
import se.sics.p2ptoolbox.serialization.SerializationContextImpl;
import se.sics.p2ptoolbox.serialization.msg.HeaderField;
import se.sics.p2ptoolbox.serialization.msg.NetMsg;
import se.sics.p2ptoolbox.serialization.msg.OverlayHeaderField;
import se.sics.p2ptoolbox.serialization.serializer.OverlayHeaderFieldSerializer;
import se.sics.p2ptoolbox.serialization.serializer.SerializerAdapter;
import se.sics.p2ptoolbox.serialization.serializer.UUIDSerializer;
import se.sics.p2ptoolbox.serialization.serializer.VodAddressSerializer;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class ExampleNetFrameDecoder extends BaseMsgFrameDecoder {

    public static final byte CROUPIER_REQUEST = (byte) 0x90;
    public static final byte CROUPIER_RESPONSE = (byte) 0x91;

    //other aliases
    public static final byte HEADER_FIELD_CODE = (byte) 0x01;
    public static final byte PEER_VIEW_CODE = (byte) 0x02;

    public static final String HEADER_FIELD_ALIAS = "MY_EXAMPLE_HEADER_FIELD";
    public static final String PEER_VIEW_ALIAS = "MY_EXAMPLE_PEER_VIEW";

    private static final SerializationContext context = new SerializationContextImpl();

    public static void init() {
        NetMsg.setContext(context);
        SerializerAdapter.setContext(context);
        CroupierNetworkSettings.oneTimeSetup(context, CONNECT_REQUEST, CONNECT_REQUEST);

        try {
            //check CroupierNetworkSettings.OtherSerializers for Croupier required serializers
            context.registerAlias(HeaderField.class, HEADER_FIELD_ALIAS, HEADER_FIELD_CODE);
            context.registerSerializer(OverlayHeaderField.class, new OverlayHeaderFieldSerializer());
            context.multiplexAlias(HEADER_FIELD_ALIAS, OverlayHeaderField.class, (byte) 0x01);
            
            context.registerSerializer(UUID.class, new UUIDSerializer());
            context.registerSerializer(VodAddress.class, new VodAddressSerializer());

            context.registerAlias(PeerView.class, PEER_VIEW_ALIAS, PEER_VIEW_CODE);
            context.registerSerializer(PeerViewA.class, new PeerViewASerializer());
            context.multiplexAlias(PEER_VIEW_ALIAS, PeerViewA.class, (byte) 0x01);
            context.registerSerializer(PeerViewB.class, new PeerViewBSerializer());
            context.multiplexAlias(PEER_VIEW_ALIAS, PeerViewB.class, (byte) 0x02);
        } catch (SerializationContext.DuplicateException ex) {
            throw new RuntimeException(ex);
        } catch (SerializationContext.MissingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public ExampleNetFrameDecoder() {
        super();
    }

    @Override
    protected RewriteableMsg decodeMsg(ChannelHandlerContext ctx, ByteBuf buffer) throws MessageDecodingException {
        // See if msg is part of parent project, if yes then return it.
        // Otherwise decode the msg here.
        RewriteableMsg msg = super.decodeMsg(ctx, buffer);
        if (msg != null) {
            return msg;
        }

        switch (opKod) {
            case CROUPIER_REQUEST:
                SerializerAdapter.Request requestS = new SerializerAdapter.Request();
                return requestS.decodeMsg(buffer);
            case CROUPIER_RESPONSE:
                SerializerAdapter.Response responseS = new SerializerAdapter.Response();
                return responseS.decodeMsg(buffer);
            default:
                return null;
        }
    }
}
