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

import io.netty.buffer.ByteBuf;
import se.sics.p2ptoolbox.serialization.SerializationContext;
import se.sics.p2ptoolbox.serialization.Serializer;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class PeerViewBSerializer implements Serializer<PeerViewB> {

    public ByteBuf encode(SerializationContext context, ByteBuf buf, PeerViewB obj) throws SerializerException, SerializationContext.MissingException {
        buf.writeInt(obj.counter);
        return buf;
    }

    public PeerViewB decode(SerializationContext context, ByteBuf buf) throws SerializerException, SerializationContext.MissingException {
        int counter = buf.readInt();
        return new PeerViewB(counter);
    }

    public int getSize(SerializationContext context, PeerViewB obj) throws SerializerException, SerializationContext.MissingException {
        return Integer.SIZE / 8;
    }
}
