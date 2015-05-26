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
import se.sics.p2ptoolbox.util.managerStore.pieceTracker.CompletePieceTracker;
import se.sics.p2ptoolbox.util.managerStore.pieceTracker.IncompletePieceTracker;
import java.io.IOException;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class StorageMngrFactory {

    public static PieceTracker getCompletePT(int nrPieces) {
        return new CompletePieceTracker(nrPieces);
}
    
    public static PieceTracker getSimplePT(int nrPieces) {
         return new IncompletePieceTracker(nrPieces);
    }
    
    private static int nrPieces(long blockSize, int pieceSize) {
        return (blockSize % pieceSize == 0) ? (int)(blockSize / pieceSize) : (int)(blockSize / pieceSize + 1);
    }
    
    public static FileMngr getCompleteFileMngr(String pathName, long fileLength, int blockSize, int pieceSize) throws IOException {
        return new CompleteFileMngr(StorageFactory.getExistingFile(pathName), blockSize, pieceSize);
    }
    
    public static FileMngr getIncompleteFileMngr(String pathName, long fileLength, int blockSize, int pieceSize) throws IOException {
        return new IncompleteFileMngr(StorageFactory.getEmptyFile(pathName, fileLength), getSimplePT(nrPieces(fileLength, blockSize)), blockSize, pieceSize);
    }
    
    public static BlockMngr getSimpleBlockMngr(int blockSize, int pieceSize) {
        return new SimpleBlockMngr(StorageFactory.getEmptyBlock(blockSize), getSimplePT(nrPieces(blockSize, pieceSize)), blockSize, pieceSize);
    }
    
    public static HashMngr getCompleteHashMngr(String pathName, String hashType, long hashFileSize, int hashSize) throws IOException {
        return new SimpleHashMngr(getCompletePT(nrPieces(hashFileSize, hashSize)), StorageFactory.getExistingFile(pathName), hashType, hashSize);
    }
    
    public static HashMngr getIncompleteHashMngr(String pathName, String hashType, long hashFileSize, int hashSize) throws IOException {
        return new SimpleHashMngr(getSimplePT(nrPieces(hashFileSize, hashSize)), StorageFactory.getEmptyFile(pathName, hashFileSize), hashType, hashSize);
    }
}
