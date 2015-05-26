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

package se.sics.p2ptoolbox.simulator.dsl.distribution.extra;

import se.sics.p2ptoolbox.simulator.dsl.distribution.Distribution;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class GenIntSequentialDistribution extends Distribution<Integer> {
    private final BasicIntSequentialDistribution indexDist;
    private final Integer[] sequence;
    
    public GenIntSequentialDistribution(Integer[] sequence) {
        super(Type.OTHER, Integer.class);
        this.sequence = sequence;
        this.indexDist = new BasicIntSequentialDistribution(0);
    }

    @Override
    public Integer draw() {
        int index = indexDist.draw();
        if(index < sequence.length) {
            return sequence[index];
        } else {
            return null;
        }
    }
    
}
