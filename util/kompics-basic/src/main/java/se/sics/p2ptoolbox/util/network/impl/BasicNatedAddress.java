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

package se.sics.p2ptoolbox.util.network.impl;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import se.sics.kompics.network.Address;
import se.sics.p2ptoolbox.util.network.NatType;
import se.sics.p2ptoolbox.util.network.NatedAddress;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class BasicNatedAddress implements NatedAddress {
    private final BasicAddress base;
    private final NatType natType;
    private final Set<NatedAddress> parents;
    
    public BasicNatedAddress(BasicAddress base, NatType natType, Set<NatedAddress> parents) {
        this.base = base;
        this.natType = natType;
        this.parents = parents;
    }
    
    public BasicNatedAddress(BasicAddress base) {
        this(base, NatType.OPEN, new HashSet<NatedAddress>());
    }

    @Override
    public InetAddress getIp() {
        return base.getIp();
    }

    @Override
    public int getPort() {
        return base.getPort();
    }

    @Override
    public InetSocketAddress asSocket() {
        return base.asSocket();
    }

    @Override
    public boolean sameHostAs(Address other) {
        return base.sameHostAs(other);
    }
    
    @Override
    public String toString() {
        return base.toString() + (isOpen() ? " OPEN " : " NATED " );
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.base != null ? this.base.hashCode() : 0);
        hash = 31 * hash + (this.natType != null ? this.natType.hashCode() : 0);
        hash = 31 * hash + (this.parents != null ? this.parents.hashCode() : 0);
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
        final BasicNatedAddress other = (BasicNatedAddress) obj;
        if (this.base != other.base && (this.base == null || !this.base.equals(other.base))) {
            return false;
        }
        if (this.natType != other.natType) {
            return false;
        }
        if (this.parents != other.parents && (this.parents == null || !this.parents.equals(other.parents))) {
            return false;
        }
        return true;
    }
    
    @Override
    public Address getBaseAdr() {
        return base;
    }
    //********************NAT***************************************************
    @Override
    public boolean isOpen() {
        return natType.equals(NatType.OPEN);
    }

    @Override
    public NatType getNatType() {
        return natType;
    }

    @Override
    public Set<NatedAddress> getParents() {
        return parents;
    }

    //********************Identifiable******************************************
    @Override
    public Integer getId() {
        return base.getId();
    }
}