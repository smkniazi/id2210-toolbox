///*
// * Copyright (C) 2009 Swedish Institute of Computer Science (SICS) Copyright (C)
// * 2009 Royal Institute of Technology (KTH)
// *
// * GVoD is free software; you can redistribute it and/or
// * modify it under the terms of the GNU General Public License
// * as published by the Free Software Foundation; either version 2
// * of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
// */
//
//package se.sics.p2ptoolbox.util.network.vod;
//
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.util.Set;
//import se.sics.gvod.net.VodAddress;
//import se.sics.kompics.network.Address;
//import se.sics.p2ptoolbox.util.network.NatType;
//import se.sics.p2ptoolbox.util.network.NatedAddress;
//import se.sics.p2ptoolbox.util.network.impl.BasicAddress;
//
///**
// * @author Alex Ormenisan <aaor@sics.se>
// */
//public class VodContainerAddress implements NatedAddress {
//    private final InetSocketAddress isa;
//    private final VodAddress baseAddress;
//    
//    public VodContainerAddress(VodAddress baseAddress) {
//        this.isa = new InetSocketAddress(baseAddress.getIp(), baseAddress.getPort());
//        this.baseAddress = baseAddress;
//    }
//    
//    @Override
//    public InetAddress getIp() {
//        return baseAddress.getIp();
//    }
//
//    @Override
//    public int getPort() {
//        return baseAddress.getPort();
//    }
//    
//    @Override
//    public InetSocketAddress asSocket() {
//        return isa;
//    }
//
//    @Override
//    public boolean sameHostAs(Address other) {
//        return this.isa.equals(other.asSocket());
//    }
//    
//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 47 * hash + (this.baseAddress != null ? this.baseAddress.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final VodContainerAddress other = (VodContainerAddress) obj;
//        if (this.baseAddress != other.baseAddress && (this.baseAddress == null || !this.baseAddress.equals(other.baseAddress))) {
//            return false;
//        }
//        return true;
//    }
//    
//    //**************NAT********************************************************
//    @Override
//    public NatType getNatType() {
//        switch(baseAddress.getNatType()) {
//            case OPEN: return NatType.OPEN;
//            case NAT: return NatType.NAT;
//        }
//        return null; //should never happen
//    }
//
//    
//    public VodAddress getVodAddress() {
//        return baseAddress;
//    }
//
//    @Override
//    public boolean isOpen() {
//        return baseAddress.isOpen();
//    }
//
//    @Override
//    public Set<NatedAddress> getParents() {
//        throw new UnsupportedOperationException();
//    }
//
//    //****************Identifiable**********************************************
//    @Override
//    public Integer getId() {
//        return baseAddress.getId();
//    }
//}
