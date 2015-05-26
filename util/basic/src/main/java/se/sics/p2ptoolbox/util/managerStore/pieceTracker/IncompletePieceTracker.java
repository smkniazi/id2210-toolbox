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
package se.sics.p2ptoolbox.util.managerStore.pieceTracker;

import java.util.BitSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class IncompletePieceTracker implements PieceTracker {

    private final BitSet pieces;
    private final int nrPieces;

    public IncompletePieceTracker(int nrPieces) {
        this.pieces = new BitSet(nrPieces + 1);
        this.nrPieces = nrPieces;
    }

    @Override
    public boolean isComplete(int from) {
        return pieces.nextClearBit(from) == nrPieces;
    }
    
    /**
     * 
     * @param from
     * @return the first piece that is not downloaded, including from.
     */
    @Override
    public int contiguous(int from) {
        int nextClear = pieces.nextClearBit(from);
        return nextClear;
    }

    @Override
    public boolean hasPiece(int piecePos) {
        return pieces.get(piecePos);
    }

    @Override
    public Integer nextPieceNeeded(int startPos, Set<Integer> except) {
        int nextPos = startPos;
        while (nextPos < nrPieces) {
            nextPos = pieces.nextClearBit(nextPos);
            if (!except.contains(nextPos)) {
                return nextPos;
            }
            nextPos++;
        }
        return null;
    }

    @Override
    public Set<Integer> nextPiecesNeeded(int n, int startPos, Set<Integer> except) {
        Set<Integer> result = new TreeSet<Integer>();
        int nextPos = startPos;
        while (result.size() < n) {
            nextPos = pieces.nextClearBit(nextPos);
            if (nextPos < nrPieces) {
                if (!except.contains(nextPos)) {
                    result.add(nextPos);
                }
            } else {
                break;
            }
            nextPos++;
        }
        return result;
    }

    @Override
    public void addPiece(int piecePos) {
        pieces.set(piecePos);
    }
}
