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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.junit.Assert;
import org.junit.Test;
import se.sics.p2ptoolbox.util.managedStore.BlockMngr;
import se.sics.p2ptoolbox.util.managedStore.FileMngr;
import se.sics.p2ptoolbox.util.managedStore.StorageMngrFactory;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class FileTest {

    //TODO Alex testEquality of MemMapFiles.. will be expensive - check temp File
    private String uploadPath;
    private String downloadPath;
    private int nrLines = 100000;
    private int fileLength;
    private int blockSize;
    private int pieceSize;
    
    @Test
    public void testSuccess() throws IOException {
        blockSize = 10*1024;
        pieceSize = 1024;
        prepareFiles();
        System.out.println(uploadPath);
        System.out.println(fileLength);
        System.out.println(downloadPath);
        
        FileMngr uploadMngr = StorageMngrFactory.getCompleteFileMngr(uploadPath, fileLength, blockSize, pieceSize);
        FileMngr downloadMngr = StorageMngrFactory.getIncompleteFileMngr(downloadPath, fileLength, blockSize, pieceSize);
        int blockNr = 0;
        while(!downloadMngr.isComplete(0)) {
//            System.out.println("block:" + blockNr);
            BlockMngr blockMngr = StorageMngrFactory.getSimpleBlockMngr(downloadMngr.blockSize(blockNr), pieceSize);
            int pieceNr = 0;
            while(!blockMngr.isComplete()) {
//                System.out.println("piece:" + pieceNr);
                blockMngr.writePiece(pieceNr, uploadMngr.read(blockNr * blockSize + pieceNr * pieceSize, pieceSize));
                pieceNr++;
            }
            downloadMngr.writeBlock(blockNr, blockMngr.getBlock());
            blockNr++;
        }
        System.out.println("blocks:" + blockNr);
        
        Assert.assertTrue(downloadMngr.isComplete(0));
        checkFiles();
    }
    
    private void prepareFiles() throws IOException {
        File downloadFile = File.createTempFile("memMapTest", "download");
        downloadPath = downloadFile.getPath();
        downloadFile.delete();

        File uploadFile = File.createTempFile("memMapTest", "upload");
        uploadPath = uploadFile.getPath();
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(uploadFile.getPath())));
        for(int i = 0; i < nrLines; i++) {
            writer.write("abc" + i + "\n");
        }
        writer.flush();
        writer.close();
        fileLength = (int)uploadFile.length();
    }
    
    private void checkFiles() throws FileNotFoundException, IOException {
        BufferedReader downloadReader = new BufferedReader(new FileReader(downloadPath));
        BufferedReader uploadReader = new BufferedReader(new FileReader(uploadPath));
        
        String downloadLine = downloadReader.readLine();
        String uploadLine = uploadReader.readLine();
        int line = 1;
        while(uploadLine != null) {
            Assert.assertEquals("line" + line, uploadLine, downloadLine);
            downloadLine = downloadReader.readLine();
            uploadLine = uploadReader.readLine();
            line++;
        }
    }
}
