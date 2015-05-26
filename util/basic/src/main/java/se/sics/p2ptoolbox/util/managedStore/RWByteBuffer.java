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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class RWByteBuffer implements Storage {
    private final ByteBuf buf;
    private final int length;
    
    public RWByteBuffer(int bufLength) {
        this.buf = Unpooled.wrappedBuffer(new byte[bufLength]);
        this.length = bufLength;
    }

    @Override
    public byte[] read(long readPos, long readLength) {
        if(readPos > Integer.MAX_VALUE || readLength > Integer.MAX_VALUE) {
            System.exit(1);
        }
        if(readPos > length) {
            return new byte[0];
        }
        if(readPos + readLength > length) {
            readLength = length - readPos;
        }
        return read((int)readPos, (int)readLength);
    }
    
    private byte[] read(int readPos, int readLength) {
        byte[] result = new byte[readLength];
        buf.readerIndex(readPos);
        buf.readBytes(result);
        return result;
    }

    @Override
    public int write(long writePos, byte[] bytes) {
        if(writePos > Integer.MAX_VALUE) {
            System.exit(1);
        }
        if(writePos > length) {
            return 0;
        }
        return write((int)writePos, bytes);
    }
    
    private int write(int writePos, byte[] bytes) {
        int auxWriterIndex = buf.writerIndex();
        buf.writerIndex(writePos);
        int rest = length - writePos;
        int writeBytes = (bytes.length < rest ? bytes.length : rest);
        buf.writeBytes(bytes, 0, writeBytes);
        int auxWriterIndex2 = buf.writerIndex();
        if(auxWriterIndex2 < auxWriterIndex) {
            buf.writerIndex(auxWriterIndex);
        }
        return writeBytes;
    }

    @Override
    public long length() {
        return length;
    }
}
