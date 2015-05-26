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

package se.sics.p2ptoolbox.croupier.core.net;

import io.netty.buffer.ByteBuf;
import org.javatuples.Pair;
import se.sics.gvod.net.VodAddress;
import se.sics.p2ptoolbox.croupier.api.util.CroupierPeerView;
import se.sics.p2ptoolbox.croupier.api.util.PeerView;
import se.sics.p2ptoolbox.serialization.SerializationContext;
import se.sics.p2ptoolbox.serialization.Serializer;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class CroupierPVSerializer implements Serializer<CroupierPeerView> {

    public ByteBuf encode(SerializationContext context, ByteBuf buf, CroupierPeerView obj) throws SerializerException, SerializationContext.MissingException {
        Pair<Byte, Byte> pvCode = context.getCode(obj.pv.getClass());
        buf.writeByte(pvCode.getValue0());
        buf.writeByte(pvCode.getValue1());
        
        Serializer serializer = context.getSerializer(obj.pv.getClass());
        serializer.encode(context, buf, obj.pv);
        context.getSerializer(VodAddress.class).encode(context, buf, obj.src);
        buf.writeInt(obj.getAge());
        return buf;
    }

    public CroupierPeerView decode(SerializationContext context, ByteBuf buf) throws SerializerException, SerializationContext.MissingException {
        Byte pvCode0 = buf.readByte();
        Byte pvCode1 = buf.readByte();
        Serializer pvS = context.getSerializer(PeerView.class, pvCode0, pvCode1);
        PeerView pv = (PeerView)pvS.decode(context, buf);
        VodAddress src = context.getSerializer(VodAddress.class).decode(context, buf);
        int age = buf.readInt();
        return new CroupierPeerView(pv, src, age);
    }

    public int getSize(SerializationContext context, CroupierPeerView obj) throws SerializerException, SerializationContext.MissingException {
        int size = 0;
        size += 2 * Byte.SIZE / 8; //pv code
        Serializer pvS = context.getSerializer(obj.pv.getClass());
        size += pvS.getSize(context, obj.pv);
        size += context.getSerializer(VodAddress.class).getSize(context, obj.src);
        size += Integer.SIZE /8; //age
        return size;
    }
}
