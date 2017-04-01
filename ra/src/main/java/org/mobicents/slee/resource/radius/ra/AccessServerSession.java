/*

  * JUNO Software SRL

  * Copyright 2016-2017, JUNO Software SRL and individual contributors

  * by the @authors tag.

  *

  * This program is free software: you can redistribute it and/or modify

  * under the terms of the GNU Affero General Public License as

  * published by the Free Software Foundation; either version 3 of

  * the License, or (at your option) any later version.

  *

  * This program is distributed in the hope that it will be useful,

  * but WITHOUT ANY WARRANTY; without even the implied warranty of

  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

  * GNU Affero General Public License for more details.

  *

  * You should have received a copy of the GNU Affero General Public License

  * along with this program.  If not, see <http://www.gnu.org/licenses/>

  *

  * This file incorporates work covered by the following copyright and

  * permission notice:

  *

  *   JBoss, Home of Professional Open Source

  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors

  *   by the @authors tag. See the copyright.txt in the distribution for a

  *   full listing of individual contributors.

  *

  *   This is free software; you can redistribute it and/or modify it

  *   under the terms of the GNU Lesser General Public License as

  *   published by the Free Software Foundation; either version 2.1 of

  *   the License, or (at your option) any later version.

  *

  *   This software is distributed in the hope that it will be useful,

  *   but WITHOUT ANY WARRANTY; without even the implied warranty of

  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU

  *   Lesser General Public License for more details.

  *

  *   You should have received a copy of the GNU Lesser General Public

  *   License along with this software; if not, write to the Free

  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA

  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.

  */

package org.mobicents.slee.resource.radius.ra;

import org.mobicents.resources.radius.RadiusAccessAck;
import org.mobicents.resources.radius.RadiusAccessNack;
import org.mobicents.resources.radius.RadiusAccessRequest;
import org.mobicents.slee.resource.radius.ra.local.RadiusActivityHandle;
import org.mobicents.slee.resource.radius.ratype.RadiusAccessSession;
import org.tinyradius.packet.RadiusPacket;

import javax.slee.resource.ActivityHandle;

/**
 * @author <a href="mailto:m@juno-software.com"> Mihnea Teodorescu </a>
 */

public class AccessServerSession implements RadiusAccessSession {

    private RadiusAccessRequest radiusAccessRequest;
    private RadiusResourceAdaptor ra;
    private Status status;
    private ActivityHandle activityHandle;

    public AccessServerSession(RadiusAccessRequest radiusAccessRequest, RadiusResourceAdaptor ra, RadiusActivityHandle activityHandle) {
        this.ra = ra;
        this.radiusAccessRequest = radiusAccessRequest;
        this.status = Status.CREATED;
        this.activityHandle = activityHandle;
    }

    public RadiusAccessRequest getRadiusAccessRequest() {
        return radiusAccessRequest;
    }

    public RadiusAccessAck createAck() {
        RadiusPacket ack = new RadiusPacket(RadiusPacket.ACCESS_ACCEPT, radiusAccessRequest.getAccessRequest().getPacketIdentifier());
        return new RadiusAccessAck(ack);
    }

    public RadiusAccessAck createNack() {
        RadiusPacket ack = new RadiusPacket(RadiusPacket.ACCESS_REJECT, radiusAccessRequest.getAccessRequest().getPacketIdentifier());
        return new RadiusAccessAck(ack);
    }

    public void accept(RadiusAccessAck ack) {
        this.status = Status.ACCEPTED;
        ra.accessAck(this, ack);
    }

    public void reject(RadiusAccessNack nack) {
        ra.accessNack(this, nack);
        this.status = Status.REJECTED;
    }

    public Status getStatus() {
        return status;
    }

    public ActivityHandle getActivityHandle() {
        return activityHandle;
    }
}
