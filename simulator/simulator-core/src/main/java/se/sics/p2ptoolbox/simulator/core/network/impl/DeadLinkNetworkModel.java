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

package se.sics.p2ptoolbox.simulator.core.network.impl;

import java.util.Set;
import org.javatuples.Pair;
import se.sics.kompics.network.Address;
import se.sics.kompics.network.Msg;
import se.sics.p2ptoolbox.simulator.core.network.NetworkModel;
import se.sics.p2ptoolbox.util.identifiable.IntegerIdentifiable;

/**
 *
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class DeadLinkNetworkModel implements NetworkModel {
    private int id;
    private final NetworkModel baseNM;
    private final Set<Pair<Integer, Integer>> deadLinks;
    
    public DeadLinkNetworkModel(int id, NetworkModel baseNM, Set<Pair<Integer, Integer>> deadLinks) {
        this.id = id;
        this.baseNM = baseNM;
        this.deadLinks = deadLinks;
    }

    @Override
    public long getLatencyMs(Msg message) {
        Address src = message.getHeader().getSource();
        Address dst = message.getHeader().getDestination();
        if(!(src instanceof IntegerIdentifiable) || !(dst instanceof IntegerIdentifiable)) {
            throw new RuntimeException("used addresses are not identifiable - cannot used DeadLinkNetworkModel with these addresses");
        }
        Pair<Integer, Integer> link = Pair.with(((IntegerIdentifiable)src).getId(), ((IntegerIdentifiable)dst).getId());
        if(deadLinks.contains(link)) {
            return -1;
        } else {
            return baseNM.getLatencyMs(message);
        }
    }
 
    @Override
    public String toString() {
        return "DeadLink NetworkModel<" + id + ">";
    }
}
