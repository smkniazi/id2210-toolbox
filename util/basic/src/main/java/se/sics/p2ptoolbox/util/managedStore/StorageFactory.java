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

import java.io.File;
import java.io.IOException;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class StorageFactory {
    public static Storage getExistingFile(String pathname) throws IOException {
        File file = new File(pathname);
        return new RMemMapFile(file);
    }
    
    public static Storage getEmptyFile(String pathname, long length) throws IOException {
        File file = new File(pathname);
        if (!file.createNewFile()) {
            throw new IOException("Could not create file " + pathname);
        }
        return new RWMemMapFile(file, length);
    }
    
    public static Storage getEmptyBlock(int bufferLength) {
        return new RWByteBuffer(bufferLength);
    }
}
