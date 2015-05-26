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

package se.sics.p2ptoolbox.croupier.core.msg;

import com.google.common.base.Objects;
import java.util.Map;
import java.util.UUID;
import se.sics.gvod.net.VodAddress;
import se.sics.p2ptoolbox.serialization.msg.HeaderField;
import se.sics.p2ptoolbox.serialization.msg.NetContentMsg;
import se.sics.p2ptoolbox.serialization.msg.OverlayHeaderField;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class ShuffleNet {
    public static class Request extends NetContentMsg.Request<Shuffle> {

        public Request(VodAddress vodSrc, VodAddress vodDest, UUID id, int overlayId, Shuffle content) {
            super(vodSrc, vodDest, id, content);
            header.put("overlay", new OverlayHeaderField(overlayId));
        }
        
        public Request(VodAddress vodSrc, VodAddress vodDest, UUID id, Map<String, HeaderField> header, Shuffle content) {
            super(vodSrc, vodDest, id, header, content);
        }
        
        public Response getResponse(Shuffle content) {
            return new Response(vodDest, vodSrc, id, header, content);
        }
        
        @Override 
        public String toString() {
            return "SHUFFLE_NET_REQUEST" + " src " + vodSrc.getPeerAddress() + " dest " + vodDest.getPeerAddress();
        }

        @Override
        public Request copy() {
        
            return new Request(vodSrc, vodDest, id, header, (Shuffle) content);
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + Objects.hashCode(this.content);
            hash = 37 * hash + Objects.hashCode(this.header);
            hash = 37 * hash + Objects.hashCode(this.vodSrc);
            hash = 37 * hash + Objects.hashCode(this.vodDest);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Request other = (Request) obj;
            if (!Objects.equal(this.content, other.content)) {
                return false;
            }
            if (!Objects.equal(this.header, other.header)) {
                return false;
            }
            if (!Objects.equal(this.vodSrc, other.vodSrc)) {
                return false;
            }
            if (!Objects.equal(this.vodDest, other.vodDest)) {
                return false;
            }
            return true;
        }
    }

    public static class Response extends NetContentMsg.Response<Shuffle> {

        public Response(VodAddress vodSrc, VodAddress vodDest, UUID id, int overlayId, Shuffle content) {
            super(vodSrc, vodDest, id, content);
            header.put("overlay", new OverlayHeaderField(overlayId));
        }
        
        public Response(VodAddress vodSrc, VodAddress vodDest, UUID id, Map<String, HeaderField> header, Shuffle content) {
            super(vodSrc, vodDest, id, header, content);
        }
        
        @Override
        public String toString() {
            return "SHUFFLE_NET_RESPONSE" + " src " + vodSrc.getPeerAddress().toString() + " dest " + vodDest.getPeerAddress().toString();
        }

        @Override
        public Response copy() {
            return new Response(vodSrc, vodDest, id, header, (Shuffle) content);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + Objects.hashCode(this.content);
            hash = 37 * hash + Objects.hashCode(this.header);
            hash = 37 * hash + Objects.hashCode(this.vodSrc);
            hash = 37 * hash + Objects.hashCode(this.vodDest);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Response other = (Response) obj;
            if (!Objects.equal(this.content, other.content)) {
                return false;
            }
            if (!Objects.equal(this.header, other.header)) {
                return false;
            }
            if (!Objects.equal(this.vodSrc, other.vodSrc)) {
                return false;
            }
            if (!Objects.equal(this.vodDest, other.vodDest)) {
                return false;
            }
            return true;
        }
    }
}
