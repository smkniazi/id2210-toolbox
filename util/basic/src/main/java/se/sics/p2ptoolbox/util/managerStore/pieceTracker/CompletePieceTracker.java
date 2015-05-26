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

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class CompletePieceTracker implements PieceTracker {

    private final int nrPieces;

    public CompletePieceTracker(int nrPieces) {
        this.nrPieces = nrPieces;
    }

    @Override
    public boolean isComplete(int from) {
        return true;
    }
    
    @Override
    public int contiguous(int from) {
        return nrPieces;
    }
    
    @Override
    public boolean hasPiece(int piecePos) {
        if(piecePos < nrPieces) {
        return true;
    }
        return false;
    }

    @Override
    public Set<Integer> nextPiecesNeeded(int n, int startPos, Set<Integer> except) {
        return new TreeSet<Integer>();
    }

    @Override
    public Integer nextPieceNeeded(int startPos, Set<Integer> except) {
        return null;
    }

    @Override
    public void addPiece(int piecePos) {
        //already have, do nothing
    }
}
