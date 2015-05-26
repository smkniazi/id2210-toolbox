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

package se.sics.p2ptoolbox.util.filters;

import se.sics.kompics.ChannelFilter;
import se.sics.kompics.network.Address;
import se.sics.kompics.network.Msg;
import se.sics.p2ptoolbox.util.identifiable.IntegerIdentifiable;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class IntegerFilter extends ChannelFilter<Msg, Integer> {

    public IntegerFilter(int id) {
        super(Msg.class, id, true);
    }


    @Override
    public Integer getValue(Msg message) {
        Address dst = message.getHeader().getDestination();
        if(dst instanceof IntegerIdentifiable) {
            return ((IntegerIdentifiable)dst).getId();
        } else {
            throw new RuntimeException("dst is not of known Identifiable");
        }
    }
}