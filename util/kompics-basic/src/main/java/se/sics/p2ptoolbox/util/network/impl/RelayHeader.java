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

import se.sics.kompics.network.Header;
import se.sics.kompics.network.Transport;
import se.sics.p2ptoolbox.util.network.NatedAddress;
import se.sics.p2ptoolbox.util.network.NatedHeader;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class RelayHeader<A extends NatedAddress> implements NatedHeader<A> {
    private final Header<A> baseH;
    private final A relay;
    
    public RelayHeader(Header<A> baseH, A relay) {
        this.baseH = baseH;
        this.relay = relay;
    }
    
    @Override
    public A getSource() {
        return relay;
    }

    @Override
    public A getDestination() {
        return baseH.getDestination();
    }

    @Override
    public Transport getProtocol() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public A getActualSource() {
        return baseH.getSource();
    }
    
    public Header<A> getActualHeader() {
        return baseH;
    }
}
