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

import org.mobicents.resources.radius.RadiusAccountingRequest;
import org.mobicents.resources.radius.RadiusAccountingResponse;
import org.mobicents.slee.resource.radius.ra.local.RadiusActivityHandle;
import org.mobicents.slee.resource.radius.ratype.RadiusAccountSession;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RequestContext;

import javax.slee.resource.ActivityHandle;
import java.net.InetAddress;


/**
 * @author <a href="mailto:m@juno-software.com"> Mihnea Teodorescu </a>
 */


public class AccountingServerSession implements RadiusAccountSession {

    private ActivityHandle activityHandle;
    private InetAddress remoteAddress;
    private String accountSessionId;
    private boolean stopping;
    private RadiusAccountingRequest lastRequest;
    private RadiusResourceAdaptor ra;

    public AccountingServerSession(RadiusAccountingRequest request,
                                   RequestContext rq,
                                   RadiusActivityHandle handle,
                                   RadiusResourceAdaptor ra) {
        this.ra = ra;
        this.lastRequest = request;
        this.activityHandle = handle;
        this.remoteAddress = rq.getRemoteAddress().getAddress();
        this.accountSessionId = request.getRequest().getAttributeValue("Acct-Session-Id");
    }

    public RadiusAccountingResponse createResponse(RadiusAccountingRequest request) {
        RadiusPacket response = new RadiusPacket(RadiusPacket.ACCOUNTING_RESPONSE, request.getRequest().getPacketIdentifier());
        return new RadiusAccountingResponse(response);
    }

    public void respond(RadiusAccountingResponse response) {
        ra.sendResponse(this, response);
    }

    public RadiusAccountingRequest getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(RadiusAccountingRequest lastRequest) {
        this.lastRequest = lastRequest;
    }

    public ActivityHandle getActivityHandle() {
        return activityHandle;
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    public String getAccountSessionId() {
        return accountSessionId;
    }

    public void setAsStopping() {
        this.stopping = true;
    }

    public boolean isStopping() {
        return this.stopping;
    }
}
