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

package se.sics.p2ptoolbox.util.managedStore;

import java.util.Set;
import se.sics.p2ptoolbox.util.managerStore.pieceTracker.PieceTracker;
import se.sics.p2ptoolbox.util.managedStore.Storage;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class SimpleHashMngr implements HashMngr {
    
    private final PieceTracker pieceTracker;
    private final Storage storage;
    public final String hashType;
    private final int hashSize;
    
    public SimpleHashMngr(PieceTracker pieceTracker, Storage storage, String hashType, int hashSize) {
        this.pieceTracker = pieceTracker;
        this.storage = storage;
        this.hashType = hashType;
        this.hashSize = hashSize;
    }
    
    @Override
    public boolean hasHash(int hashNr) {
        return pieceTracker.hasPiece(hashNr);
    }

    @Override
    public byte[] readHash(int hashNr) {
        return storage.read(hashNr * hashSize, hashSize);
    }

    @Override
    public int writeHash(int hashNr, byte[] hash) {
        if(hash.length > hashSize) {
            System.exit(1);
        }
        pieceTracker.addPiece(hashNr);
        return storage.write(hashNr*hashSize, hash);
    }

    @Override
    public boolean isComplete(int hashNr) {
        return pieceTracker.isComplete(hashNr);
    }

    @Override
    public int contiguous(int hashNr) {
        return pieceTracker.contiguous(hashNr);
    }
    
    @Override
    public Set<Integer> nextHashes(int n, int pos, Set<Integer> exclude) {
        return pieceTracker.nextPiecesNeeded(n, pos, exclude);
    }

}
