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

import com.google.common.collect.ImmutableCollection;
import se.sics.p2ptoolbox.croupier.api.util.CroupierPeerView;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class Shuffle {
        
        public final ImmutableCollection<CroupierPeerView> publicNodes;
        public final ImmutableCollection<CroupierPeerView> privateNodes;

        public Shuffle(ImmutableCollection<CroupierPeerView> publicNodes, ImmutableCollection<CroupierPeerView> privateNodes) {
            this.publicNodes = publicNodes;
            this.privateNodes = privateNodes;
        }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.publicNodes != null ? this.publicNodes.hashCode() : 0);
        hash = 41 * hash + (this.privateNodes != null ? this.privateNodes.hashCode() : 0);
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
        final Shuffle other = (Shuffle) obj;
        if (this.publicNodes != other.publicNodes && (this.publicNodes == null || !this.publicNodes.equals(other.publicNodes))) {
            return false;
        }
        if (this.privateNodes != other.privateNodes && (this.privateNodes == null || !this.privateNodes.equals(other.privateNodes))) {
            return false;
        }
        return true;
    }
}