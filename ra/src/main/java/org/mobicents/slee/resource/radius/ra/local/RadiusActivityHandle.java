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

package org.mobicents.slee.resource.radius.ra.local;

import org.mobicents.resources.radius.RadiusAccessRequest;
import org.mobicents.resources.radius.RadiusAccountingRequest;

import javax.slee.resource.ActivityHandle;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author <a href="mailto:m@juno-software.com"> Mihnea Teodorescu </a>
 */

public class RadiusActivityHandle implements ActivityHandle {


    final int packetIdentifier;
    final InetSocketAddress remoteAddress;
    final String accountingSessionId;

    public RadiusActivityHandle(RadiusAccessRequest radiusAccessRequest) {
        packetIdentifier = radiusAccessRequest.getAccessRequest().getPacketIdentifier();
        remoteAddress = radiusAccessRequest.getRequestContext().getRemoteAddress();
        accountingSessionId = null;
    }

    public RadiusActivityHandle(RadiusAccountingRequest request) {
        packetIdentifier = 0;
        remoteAddress = request.getContext().getRemoteAddress();
        accountingSessionId = request.getRequest().getAttribute("Acct-Session-Id").getAttributeValue();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RadiusActivityHandle that = (RadiusActivityHandle) o;

        if (packetIdentifier != that.packetIdentifier) return false;
        if (accountingSessionId != null ? !accountingSessionId.equals(that.accountingSessionId) : that.accountingSessionId != null)
            return false;
        if (remoteAddress != null ? !remoteAddress.equals(that.remoteAddress) : that.remoteAddress != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = packetIdentifier;
        result = 31 * result + (remoteAddress != null ? remoteAddress.hashCode() : 0);
        result = 31 * result + (accountingSessionId != null ? accountingSessionId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (accountingSessionId == null) return "AH  {, " + remoteAddress + " / I-" + packetIdentifier + '}';
        else return "AH  {" + remoteAddress + " / S-" + accountingSessionId + '}';
    }
}
