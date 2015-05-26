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

import java.util.Random;
import se.sics.p2ptoolbox.simulator.dsl.distribution.Distribution;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class IntegerUniformDistribution extends Distribution<Integer>{
    private final Random rand;
    private final int min;
    private final int max;
    
    /**
     * @param min closed interval - included
     * @param max open interval - excluded
     */
    public IntegerUniformDistribution(int min, int max, Random rand) {
        super(Type.UNIFORM, Integer.class);
        this.rand = rand;
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer draw() {
        return min + rand.nextInt(max-min);
    }
}
