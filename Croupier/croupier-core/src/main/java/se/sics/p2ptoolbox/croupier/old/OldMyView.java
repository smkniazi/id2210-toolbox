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
//package se.sics.p2ptoolbox.croupier.old;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.ListIterator;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//import org.javatuples.Triplet;
//import se.sics.gvod.net.VodAddress;
//
///**
// * @author Alex Ormenisan <aaor@sics.se>
// */
//public class OldMyView {
//    private final Random rand;
//    private final int viewSize;
//
//    private final Comparator<CroupierPV> comparatorByAge = new Comparator<CroupierPV>() {
//        @Override
//        public int compare(CroupierPV o1, CroupierPV o2) {
//            if (o1.getAge() > o2.getAge()) {
//                return 1;
//            } else if (o1.getAge() < o2.getAge()) {
//                return -1;
//            } else {
//                return 0;
//            }
//        }
//    };
//
//    private final ArrayList<CroupierPV> entries;
//    private final Map<VodAddress, CroupierPV> srcToEntry;
//
//    public OldMyView(Random rand, int viewSize) {
//        this.rand = rand;
//        this.viewSize = viewSize;
//        this.entries = new ArrayList<CroupierPV>();
//        this.srcToEntry = new HashMap<VodAddress, CroupierPV>();
//    }
//
//    public void viewSelect(Triplet<Integer, Integer, Integer> chs, Set<CroupierPV> cpvSet) {
//        append(cpvSet);
//        removeOld(Math.min(chs.getValue0(), chs.getValue1()));
//        removeHead(Math.min(chs.getValue0(), chs.getValue2()));
//        removeRandom(viewSize - entries.size());
//    }
//
//    private void append(Set<CroupierPV> cpvSet) {
//        for (CroupierPV cpv : cpvSet) {
//            CroupierPV oldCPV = srcToEntry.get(cpv.src);
//            if (oldCPV == null) {
//                addEntry(cpv);
//            } else if (cpv.getAge() < oldCPV.getAge()) {
//                removeEntry(oldCPV);
//                addEntry(cpv);
//            } else {
//                //do nothing, oldCPV is newer
//            }
//        }
//    }
//    private void removeOld(int n) {
//        if (n == 0) {
//            return;
//        }
//        Set<CroupierPV> toRemove = new HashSet<CroupierPV>();
//        
//        List<CroupierPV> auxEntries = new ArrayList<CroupierPV>(entries);
//        Collections.sort(auxEntries, Collections.reverseOrder(comparatorByAge));
//        
//        Iterator<CroupierPV> it = auxEntries.iterator();
//        for(int i = 0 ; i < n && it.hasNext(); i++) {
//            toRemove.add(it.next());
//        }
//        
//        for(CroupierPV cpv: toRemove) {
//            removeEntry(cpv);
//        }
//    }
//    private void removeHead(int n) {
//        if(n == 0) {
//            return;
//        }
//        Set<CroupierPV> toRemove = new HashSet<CroupierPV>();
//        
//        Iterator<CroupierPV> it = entries.iterator();
//        for(int i = 0 ; i < n && it.hasNext(); i++) {
//            toRemove.add(it.next());
//        }
//        
//        for(CroupierPV cpv: toRemove) {
//            removeEntry(cpv);
//        }
//    }
//    private void removeRandom(int n) {
//        if(n == 0) {
//            return;
//        }
//        
//        for(int i = 0; i < n; i++) {
//            removeEntry(entries.get(rand.nextInt(entries.size())));
//        }
//    }
//    
//    private void addEntry(CroupierPV cpv) {
//        entries.add(cpv);
//        srcToEntry.put(cpv.src, cpv);
//    }
//    private void removeEntry(CroupierPV cpv) {
//        entries.remove(cpv);
//        srcToEntry.remove(cpv.src);
//    }
//}
