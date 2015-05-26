///*
// * Copyright (C) 2009 Swedish Institute of Computer Science (SICS) Copyright (C)
// * Copyright (C) 2009 Royal Institute of Technology (KTH)
// *
// * Croupier is free software; you can redistribute it and/or
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
//package se.sics.p2ptoolbox.util.network.vod;
//
//import java.util.HashSet;
//import java.util.Set;
//import se.sics.gvod.net.VodAddress;
//import se.sics.p2ptoolbox.util.network.NatedAddress;
//
///**
// * @author Alex Ormenisan <aaor@sics.se>
// */
//public class VoDHelper {
//
//    public static NatedAddress getNatedAddress(VodAddress adr) {
//        return new VodContainerAddress(adr);
//    }
//
//    public static VodAddress getVodAddress(NatedAddress adr) {
//        if (adr instanceof VodContainerAddress) {
//            return ((VodContainerAddress) adr).getVodAddress();
//        } else {
//            throw new ClassCastException("Expected VodAddressContainer");
//        }
//    }
//
//    public static Set<NatedAddress> getNatAdrSetFromVodAdr(Set<VodAddress> adrSet) {
//        Set<NatedAddress> result = new HashSet<NatedAddress>();
//        for (VodAddress adr : adrSet) {
//            result.add(new VodContainerAddress(adr));
//        }
//        return result;
//    }
//    
//    public static Set<VodAddress> getVodAddressSet(Set<NatedAddress> adrSet) {
//        Set<VodAddress> result = new HashSet<VodAddress>();
//        for (NatedAddress adr : adrSet) {
//            if (adr instanceof VodContainerAddress) {
//                result.add(((VodContainerAddress) adr).getVodAddress());
//            } else {
//                throw new ClassCastException("Expected VodAddressContainer");
//            }
//        }
//        return result;
//    }
//}
