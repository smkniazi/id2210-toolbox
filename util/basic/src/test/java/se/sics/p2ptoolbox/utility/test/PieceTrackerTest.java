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
package se.sics.p2ptoolbox.utility.test;

import java.util.HashSet;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;
import se.sics.p2ptoolbox.util.managerStore.pieceTracker.IncompletePieceTracker;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class PieceTrackerTest {

    @Test
    public void test() {
        IncompletePieceTracker pt = new IncompletePieceTracker(10);
        TreeSet<Integer> expected;

        Assert.assertFalse(pt.isComplete(0));

        expected = new TreeSet<Integer>();
        expected.add(0);
        expected.add(1);
        expected.add(2);
        Assert.assertEquals(expected, pt.nextPiecesNeeded(3, 0, new HashSet<Integer>()));
        Assert.assertEquals(0, pt.contiguous(0));

        pt.addPiece(0);
        pt.addPiece(1);
        pt.addPiece(3);
        pt.addPiece(4);
        pt.addPiece(6);
        Assert.assertFalse(pt.isComplete(0));
        Assert.assertEquals(2, pt.contiguous(0));

        expected = new TreeSet<Integer>();
        expected.add(2);
        expected.add(5);
        expected.add(7);
        Assert.assertEquals(expected, pt.nextPiecesNeeded(3, 0, new HashSet<Integer>()));

        expected = new TreeSet<Integer>();
        expected.add(8);
        expected.add(9);
        Assert.assertEquals(expected, pt.nextPiecesNeeded(3, 8, new HashSet<Integer>()));

        pt.addPiece(2);
        pt.addPiece(5);
        Assert.assertEquals(7, pt.contiguous(0));
        pt.addPiece(7);
        pt.addPiece(8);
        pt.addPiece(9);
        Assert.assertEquals(10, pt.contiguous(0));
        Assert.assertTrue(pt.isComplete(0));
    }
}
