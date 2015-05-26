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

import se.sics.p2ptoolbox.util.managerStore.pieceTracker.PieceTracker;
import se.sics.p2ptoolbox.util.managedStore.Storage;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class SimpleBlockMngr implements BlockMngr {
    private final Storage storage;
    private final PieceTracker pieceTracker;
    private final int blockSize;
    private final int pieceSize;
    private final int lastPiece;
    private final int lastPieceSize;
    
    public SimpleBlockMngr(Storage storage, PieceTracker pieceTracker, int blockSize, int pieceSize) {
        this.storage = storage;
        this.pieceTracker = pieceTracker;
        this.blockSize = blockSize;
        this.pieceSize = pieceSize;
        this.lastPiece = (blockSize % pieceSize == 0) ? blockSize / pieceSize - 1 : blockSize / pieceSize;
        this.lastPieceSize = (blockSize % pieceSize == 0) ? pieceSize : blockSize % pieceSize;
    }

    @Override
    public boolean hasPiece(int pieceNr) {
        return pieceTracker.hasPiece(pieceNr);
    }

    @Override
    public int writePiece(int pieceNr, byte[] piece) {
        pieceTracker.addPiece(pieceNr);
        long writePos = pieceNr * pieceSize;
        return storage.write(writePos, piece);
    }

    @Override
    public boolean isComplete() {
        return pieceTracker.isComplete(0);
    }

    @Override
    public byte[] getBlock() {
        return storage.read(0, blockSize);
    }

    @Override
    public int nrPieces() {
        return lastPiece + 1;
    }
}
