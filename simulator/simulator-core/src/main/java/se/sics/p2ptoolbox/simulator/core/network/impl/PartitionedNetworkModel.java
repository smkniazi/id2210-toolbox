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

import se.sics.kompics.network.Address;
import se.sics.kompics.network.Msg;
import se.sics.p2ptoolbox.simulator.core.network.NetworkModel;
import se.sics.p2ptoolbox.simulator.core.network.PartitionMapper;
import se.sics.p2ptoolbox.util.identifiable.IntegerIdentifiable;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class PartitionedNetworkModel implements NetworkModel {

    private final NetworkModel netModel;
    private final PartitionMapper mapper;

    public PartitionedNetworkModel(NetworkModel netModel, PartitionMapper mapper) {
        this.netModel = netModel;
        this.mapper = mapper;
    }

    @Override
    public long getLatencyMs(Msg message) {
        Address src = message.getHeader().getSource();
        Address dst = message.getHeader().getDestination();
        if (src instanceof IntegerIdentifiable && dst instanceof IntegerIdentifiable) {
            int srcPartition = mapper.getPartition(((IntegerIdentifiable) src).getId());
            int destPartition = mapper.getPartition(((IntegerIdentifiable) dst).getId());
            if (srcPartition == destPartition) {
                return netModel.getLatencyMs(message);
            }
            return -1;
        } else {
            throw new RuntimeException("Incomplete - src/dst is not Identifiable - or at least of no known Identifiable");
        }
    }
}
