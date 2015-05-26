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
public class IncompleteFileMngr implements FileMngr {
    private final PieceTracker pieceTracker;
    private final Storage storage;
    private final int blockSize;
    private final int pieceSize;
    private final int lastBlock;
    private final int lastBlockSize;
    
    public IncompleteFileMngr(Storage storage, PieceTracker pieceTracker, int blockSize, int pieceSize) {
        this.pieceTracker = pieceTracker;
        this.storage = storage;
        this.blockSize = blockSize;
        this.pieceSize = pieceSize;
        this.lastBlock = (storage.length() % blockSize == 0) ? (int)(storage.length() / blockSize) - 1 : (int)(storage.length() / blockSize);
        this.lastBlockSize = (storage.length() % blockSize == 0) ? blockSize : (int)(storage.length() % blockSize);
    }
    
    @Override
    public boolean isComplete(int fromBlockNr) {
        return pieceTracker.isComplete(fromBlockNr);
    }
    
    @Override
    public int contiguous(int fromBlockNr) {
        return pieceTracker.contiguous(fromBlockNr);
    }

    @Override
    public boolean has(long readPos, int length) {
        while (length > 0) {
            int blockNr = (int) readPos / blockSize;
            if (!pieceTracker.hasPiece(blockNr)) {
                return false;
            }
            length = length - blockSize;
            readPos = readPos + blockSize;
        }
        return true;
    }

    @Override
    public byte[] read(long readPos, int length) {
        return storage.read(readPos, length);
    }

    @Override
    public int writeBlock(int blockNr, byte[] block) {
        pieceTracker.addPiece(blockNr);
        return storage.write(blockNr * blockSize, block);
    }

    @Override
    public Integer nextBlock(int blockNr, Set<Integer> exclude) {
        return pieceTracker.nextPieceNeeded(blockNr, exclude);
    }

    @Override
    public int blockSize(int blockNr) {
        if(blockNr == lastBlock) {
            return lastBlockSize;
        }
        return blockSize;
    }

    @Override
    public boolean hasPiece(int pieceNr) {
        return has(pieceNr * pieceSize, pieceSize);
    }

    @Override
    public byte[] readPiece(int pieceNr) {
        return read(pieceNr * pieceSize, pieceSize);
    }
}
