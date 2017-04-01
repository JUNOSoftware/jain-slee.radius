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

import org.mobicents.resources.radius.RadiusResponse;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;
import org.tinyradius.util.RadiusServer;
import org.tinyradius.util.RequestContext;

import javax.slee.facilities.Tracer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;


/**
 * @author <a href="mailto:m@juno-software.com"> Mihnea Teodorescu </a>
 */


public class RadiusServerWrapper extends RadiusServer {

    private RadiusResourceAdaptor ra;

    public RadiusServerWrapper(RadiusResourceAdaptor ra) {
        this.ra = ra;
    }

    @Override
    public String getSharedSecret(InetSocketAddress client) {
        return ra.getSharedSecret(client);
    }

    @Override
    public String getUserPassword(String userName) {
        throw new IllegalStateException("This is not supported");
    }

    @Override
    public RadiusPacket accessRequestReceived(final AccessRequest accessRequest, final RequestContext rc) throws RadiusException {
        ra.accessRequestReceived(accessRequest, rc);
        return null;
    }


    @Override
    public RadiusPacket accountingRequestReceived(final AccountingRequest accountingRequest, final RequestContext rc) throws RadiusException {
        ra.accountingRequestReceived(accountingRequest, rc);
        return null;

    }

    protected void doSendResponse(AccessServerSession session, RadiusResponse ack, Tracer logger) {

        RequestContext rc = session.getRadiusAccessRequest().getRequestContext();
        int id = ack.getPacket().getPacketIdentifier();

        copyProxyState(session.getRadiusAccessRequest().getAccessRequest(), ack.getPacket());
        DatagramPacket packetOut = null;
        InetSocketAddress remoteAddress = rc.getRemoteAddress();
        try {
            packetOut = makeDatagramPacket(
                    ack.getPacket(),
                    rc.getSharedSecret(),
                    remoteAddress.getAddress(),
                    remoteAddress.getPort(),
                    session.getRadiusAccessRequest().getAccessRequest());
            logger.fine("Sending response : " + remoteAddress + " / " + id);
            rc.getSocket().send(packetOut);
        } catch (IOException e) {
            logger.warning("Error while sending ack for : " + remoteAddress + "/" + id, e);
        }
        logger.info("Responded to packet : " + id);
    }

    protected void doSendResponse(AccountingServerSession session, RadiusResponse ack, Tracer logger) {

        RequestContext rc = session.getLastRequest().getContext();
        int id = ack.getPacket().getPacketIdentifier();

        copyProxyState(session.getLastRequest().getRequest(), ack.getPacket());
        DatagramPacket packetOut = null;
        InetSocketAddress remoteAddress = rc.getRemoteAddress();
        try {
            packetOut = makeDatagramPacket(
                    ack.getPacket(),
                    rc.getSharedSecret(),
                    remoteAddress.getAddress(),
                    remoteAddress.getPort(),
                    session.getLastRequest().getRequest());
            logger.fine("Sending response : " + remoteAddress + " / " + id);
            rc.getSocket().send(packetOut);
        } catch (IOException e) {
            logger.warning("Error while sending ack for : " + remoteAddress + "/" + id, e);
        }
        logger.info("Responded to packet : " + remoteAddress + " / "+ id);
    }


}
