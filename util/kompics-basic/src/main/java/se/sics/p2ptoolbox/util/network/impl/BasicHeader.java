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

import se.sics.kompics.network.Address;
import se.sics.kompics.network.Header;
import se.sics.kompics.network.Transport;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class BasicHeader<A extends Address> implements Header<A> {
    private final A src;
    private final A dst;
    private final Transport protocol;
    
    public BasicHeader(A src, A dst, Transport protocol) {
        this.src = src;
        this.dst = dst;
        this.protocol = protocol;
    }
    
    @Override
    public A getSource() {
        return src;
    }

    @Override
    public A getDestination() {
        return dst;
    }

    @Override
    public Transport getProtocol() {
        return protocol;
    }
    
}
