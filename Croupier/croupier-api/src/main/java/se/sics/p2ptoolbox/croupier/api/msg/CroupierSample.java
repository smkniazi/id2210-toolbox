/*
 * Copyright (C) 2009 Swedish Institute of Computer Science (SICS) Copyright (C)
 * Copyright (C) 2009 Royal Institute of Technology (KTH)
 *
 * Croupier is free software; you can redistribute it and/or
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
package se.sics.p2ptoolbox.croupier.api.msg;

import com.google.common.collect.ImmutableCollection;
import java.util.List;
import java.util.UUID;
import se.sics.p2ptoolbox.croupier.api.util.CroupierPeerView;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class CroupierSample extends CroupierMsg.OneWay {

    public final int overlayId;
    public final ImmutableCollection<CroupierPeerView> publicSample;
    public final ImmutableCollection<CroupierPeerView> privateSample;
    
    public CroupierSample(UUID id, int overlayId, ImmutableCollection<CroupierPeerView> publicSample, ImmutableCollection<CroupierPeerView> privateSample) {
        super(id);
        this.overlayId = overlayId;
        this.publicSample = publicSample;
        this.privateSample = privateSample;
    }

    @Override
    public String toString() {
        return "SAMPLE";
    }
}
