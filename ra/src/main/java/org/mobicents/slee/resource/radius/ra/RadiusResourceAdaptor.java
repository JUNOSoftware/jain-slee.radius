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

import org.mobicents.resources.radius.*;
import org.mobicents.slee.resource.radius.ra.local.RadiusActivityHandle;
import org.mobicents.slee.resource.radius.ra.local.RadiusLocalProvider;
import org.mobicents.slee.resource.radius.ratype.RadiusAccountSession;
import org.mobicents.slee.resource.radius.ratype.RadiusActivityContextInterfaceFactory;
import org.mobicents.slee.resource.radius.ratype.RadiusSession;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;
import org.tinyradius.util.RequestContext;

import javax.slee.Address;
import javax.slee.AddressPlan;
import javax.slee.EventTypeID;
import javax.slee.UnrecognizedEventException;
import javax.slee.facilities.EventLookupFacility;
import javax.slee.facilities.Tracer;
import javax.slee.resource.*;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author <a href="mailto:m@juno-software.com"> Mihnea Teodorescu </a>
 */


public class RadiusResourceAdaptor implements ResourceAdaptor, Serializable {

    private RadiusLocalProvider provider = new RadiusLocalProvider(this);

    private ResourceAdaptorContext raContext;
    private SleeEndpoint sleeEndpoint;
    private EventLookupFacility eventLookupFacility;
    private Tracer logger = null;

    private RadiusServerWrapper server;
    private ConcurrentHashMap<ActivityHandle, RadiusSession> activities = new ConcurrentHashMap<ActivityHandle, RadiusSession>();

    private static final String DEFAULT_SECRET_KEY = "*";
    private Integer authPort = 1812;
    private Integer accPort = 1813;
    private Map<String, String> secrets = new HashMap<String, String>();

    private String secretsString = "*:all";
    private RadiusActivityContextInterfaceFactory acif = null;

    public void setResourceAdaptorContext(ResourceAdaptorContext resourceAdaptorContext) {
        this.raContext = resourceAdaptorContext;
        this.logger = raContext.getTracer("RadiusResourceAdaptor");
        this.sleeEndpoint = raContext.getSleeEndpoint();
        this.eventLookupFacility = raContext.getEventLookupFacility();
        this.server = new RadiusServerWrapper(this);
    }

    public void unsetResourceAdaptorContext() {
        this.raContext = null;
        this.sleeEndpoint = null;
        this.eventLookupFacility = null;
    }

    public void raConfigure(ConfigProperties p) {
        logger.info("Ra-Configure request received....." + p);
        setAuthPort(Integer.parseInt(p.getProperty("authPort").getValue().toString()));
        setAccPort(Integer.parseInt(p.getProperty("accPort").getValue().toString()));
        setSecrets(p.getProperty("secrets").getValue().toString());
    }

    public void raUnconfigure() {
        logger.info("org.mobicents.slee.resource.radius.ra.RadiusResourceAdaptor.raUnconfigure");
    }

    public void raActive() {
        server.setAcctPort(getAccPort());
        server.setAuthPort(getAuthPort());
        server.start(true, true);
        logger.info("org.mobicents.slee.resource.radius.ra.RadiusResourceAdaptor.raActive");
    }

    public void raStopping() {
        logger.info("org.mobicents.slee.resource.radius.ra.RadiusResourceAdaptor.raStopping");
    }

    public void raInactive() {
        logger.info("org.mobicents.slee.resource.radius.ra.RadiusResourceAdaptor.raInactive");
        server.stop();
    }

    public void raVerifyConfiguration(ConfigProperties configProperties) throws InvalidConfigurationException {
        //throw new InvalidConfigurationException("No name set jcc peer.");
        logger.info("Verify ra configurations");
    }

    public void raConfigurationUpdate(ConfigProperties configProperties) {
        logger.info("Update ra configurations");
        this.raConfigure(configProperties);
    }

    public Object getResourceAdaptorInterface(String s) {
        return this.provider;
    }

    public Marshaler getMarshaler() {
        return null;
    }

    public void serviceActive(ReceivableService receivableService) {
        logger.info("org.mobicents.slee.resource.radius.ra.RadiusResourceAdaptor.serviceActive");
    }

    public void serviceStopping(ReceivableService receivableService) {
        logger.info("org.mobicents.slee.resource.radius.ra.RadiusResourceAdaptor.serviceStopping");
    }

    public void serviceInactive(ReceivableService receivableService) {
        logger.info("org.mobicents.slee.resource.radius.ra.RadiusResourceAdaptor.serviceInactive");
    }

    public void queryLiveness(ActivityHandle activityHandle) {
        logger.info("org.mobicents.slee.resource.radius.ra.RadiusResourceAdaptor.queryLiveness");
    }

    public Object getActivity(ActivityHandle activityHandle) {
        return activities.get(activityHandle);
    }

    public ActivityHandle getActivityHandle(Object o) {
        return ((RadiusSession)o).getActivityHandle();
    }

    public void administrativeRemove(ActivityHandle activityHandle) {
        logger.info("org.mobicents.slee.resource.radius.ra.RadiusResourceAdaptor.administrativeRemove");
    }

    public void eventProcessingSuccessful(ActivityHandle activityHandle, FireableEventType fireableEventType, Object o, Address address, ReceivableService receivableService, int i) {
        logger.info("org.mobicents.slee.resource.radius.ra.RadiusResourceAdaptor.eventProcessingSuccessful");
    }

    public void eventProcessingFailed(ActivityHandle activityHandle, FireableEventType fireableEventType, Object o, Address address, ReceivableService receivableService, int i, FailureReason failureReason) {
        logger.info("org.mobicents.slee.resource.radius.ra.RadiusResourceAdaptor.eventProcessingFailed");
    }

    public void eventUnreferenced(ActivityHandle activityHandle, FireableEventType fireableEventType, Object o, Address address, ReceivableService receivableService, int i) {
        logger.info("org.mobicents.slee.resource.radius.ra.RadiusResourceAdaptor.eventUnreferenced");
    }

    public void activityEnded(ActivityHandle activityHandle) {
        logger.info("Activity ended " + activityHandle + " " + (activities.remove(activityHandle) ));
    }

    public void activityUnreferenced(ActivityHandle activityHandle) {
        logger.info("org.mobicents.slee.resource.radius.ra.RadiusResourceAdaptor.activityUnreferenced");
    }

    public RadiusPacket accessRequestReceived(final AccessRequest accessRequest, RequestContext rq) throws RadiusException {

        RadiusAccessRequest radiusAccessRequest = new RadiusAccessRequest(accessRequest, rq);
        RadiusActivityHandle activityHandle = new RadiusActivityHandle(radiusAccessRequest);

        logger.info("Access Request Received " + activityHandle);
        try {
            AccessServerSession session = new AccessServerSession(radiusAccessRequest, this, activityHandle);

            String eventName = "org.mobicents.resources.radius.ACCESS_REQUEST";

            activities.put(activityHandle, session);
            sleeEndpoint.startActivity(activityHandle, session, ActivityFlags.REQUEST_ENDED_CALLBACK);

            fireEvent(radiusAccessRequest, activityHandle, eventName);

        } catch (Exception e) {
            logger.warning("Error while processing access request " + activityHandle);
        }

        return null;
    }

    private void fireEvent(Object request, RadiusActivityHandle activityHandle, String eventName) throws UnrecognizedEventException, FireEventException {

        FireableEventType eventType = eventLookupFacility.getFireableEventType(new EventTypeID(
                eventName,
                "org.mobicents.resources.radius",
                "1.0"));

        Address address = new Address(AddressPlan.IP, activityHandle.toString());

        logger.info("Fire event " + eventName + " "  + activityHandle);

        sleeEndpoint.fireEvent(activityHandle, eventType, request, address, null);
    }

    public RadiusPacket accountingRequestReceived(
            final AccountingRequest accountingRequest,
            final RequestContext rq) throws RadiusException {

        RadiusAccountingRequest request = new RadiusAccountingRequest(accountingRequest, rq);
        RadiusActivityHandle handle = new RadiusActivityHandle(request);
        int id = request.getRequest().getPacketIdentifier();
        boolean isStart   = accountingRequest.getAcctStatusType() == AccountingRequest.ACCT_STATUS_TYPE_START;
        boolean isInterim = accountingRequest.getAcctStatusType() == AccountingRequest.ACCT_STATUS_TYPE_INTERIM_UPDATE;
        boolean isStop   = accountingRequest.getAcctStatusType() == AccountingRequest.ACCT_STATUS_TYPE_STOP;
        boolean isActivityPresent = activities.containsKey(handle);

        logger.info("Accounting request received : " + handle + " : " + id);

        try {

            //validate request

            if (RadiusPacket.ACCOUNTING_REQUEST != accountingRequest.getPacketType()) {
                logger.warning("Unsupported accounting packet received with type "
                        + accountingRequest.getPacketType() + " : " + handle + " : " + id);
                return null;
            }

            if (isAccSessionIdNotPresent(request)) {
                logger.warning("Session not found in request. Discard the request " + handle + " : " + id);
                return null;
            }

            if (!(isStart || isInterim || isStop)) {
                logger.warning("Unsupported accounting status type : " + accountingRequest.getAcctStatusType());
                return null;
            }

            if (isStart && isActivityPresent) {
                logger.warning("Session already started for " + handle + ". Discard the request : " + id);
                return null;
            }

            if ( (isInterim || isStop) && !isActivityPresent) {
                logger.warning("Interim Update/Stop request received, but can not find the activity : "
                        + handle + " : " + id + ":" + request.getRequest().getAcctStatusType());
                return null;
            }

            //process request
            if (isStart)        doAcceptAccountStartRequest(rq, request, handle);
            else if (isInterim) doAcceptInterimAccountRequest(request, handle);
            else if (isStop)    doAcceptAccountStopRequest(request, handle);

        } catch (Exception e) {
            logger.warning("Error while handling account request " + handle, e);
        }

        return null;
    }

    private boolean isAccSessionIdNotPresent(RadiusAccountingRequest request) {
        return request.getRequest().getAttribute("Acct-Session-Id") == null;
    }

    private void doAcceptAccountStartRequest(RequestContext rq, RadiusAccountingRequest request, RadiusActivityHandle handle) throws StartActivityException, UnrecognizedEventException, FireEventException {
        RadiusAccountSession session = new AccountingServerSession(request, rq, handle, this);
        String eventName = "org.mobicents.resources.radius.ACCOUNT_START_REQUEST";

        activities.put(handle, session);
        sleeEndpoint.startActivity(handle, session, ActivityFlags.REQUEST_ENDED_CALLBACK);

        fireEvent(request, handle, eventName);
    }

    private void doAcceptInterimAccountRequest(RadiusAccountingRequest request, RadiusActivityHandle handle) throws StartActivityException, UnrecognizedEventException, FireEventException {

        AccountingServerSession radiusSession = (AccountingServerSession) activities.get(handle);
        radiusSession.setLastRequest(request);
        String eventName = "org.mobicents.resources.radius.ACCOUNT_INTERIM_REQUEST";
        fireEvent(request, handle, eventName);
    }

    private void doAcceptAccountStopRequest(RadiusAccountingRequest request, RadiusActivityHandle handle) throws StartActivityException, UnrecognizedEventException, FireEventException {
        //mark session going to end
        AccountingServerSession radiusSession = (AccountingServerSession) activities.get(handle);
        if (radiusSession.isStopping()) {
            logger.warning("Session already ending. Discard request " + handle);
            return;
        }
        radiusSession.setLastRequest(request);
        radiusSession.setAsStopping();

        String eventName = "org.mobicents.resources.radius.ACCOUNT_STOP_REQUEST";
        fireEvent(request, handle, eventName);
    }

    public String getSharedSecret(InetSocketAddress client) {
        logger.info("lookup secret for " + client.getAddress().getHostAddress());
        String secret = secrets.get(client.getAddress().getHostAddress());
        if (secret == null) {
            secret = secrets.get(DEFAULT_SECRET_KEY);
        }
        return secret;
    }

    public void accessAck(AccessServerSession session, RadiusAccessAck ack) {
        doSendResponse(session, ack);
    }

    public void accessNack(AccessServerSession accessServerSession, RadiusAccessNack nack) {
        doSendResponse(accessServerSession, nack);
    }

    private void doSendResponse(AccessServerSession session, RadiusResponse ack) {

        try {
            server.doSendResponse(session, ack, logger);
            logger.info("Responded to packet : " + ack.getPacket().getPacketIdentifier());

        } finally {

            sleeEndpoint.endActivity(session.getActivityHandle());
            logger.info("End activity " + session.getActivityHandle());

        }
    }

    public void sendResponse(AccountingServerSession session, RadiusAccountingResponse ack) {
        try {

            server.doSendResponse(session, ack, logger);
            logger.info("Responded to packet : " + session.getActivityHandle() + " : " + ack.getPacket().getPacketIdentifier());

        } finally {

            if (session.isStopping()) {
                sleeEndpoint.endActivity(session.getActivityHandle());
                logger.info("End activity " + session.getActivityHandle());
            }

        }
    }

    public Integer getAuthPort() {
        return authPort;
    }

    public void setAuthPort(Integer authPort) {
        this.authPort = authPort;
    }

    public Integer getAccPort() {
        return accPort;
    }

    public void setAccPort(Integer accPort) {
        this.accPort = accPort;
    }

    public String getSecrets() {
        return secretsString;
    }

    public void setSecrets(String secretsString) {
        this.secretsString = secretsString;
        String[] split = secretsString.split(",");
        Map<String, String> refinedSecrets = new HashMap<String, String>();
        for (String s : split) {
            String[] secret = s.split(":");
            refinedSecrets.put(secret[0], secret[1]);
        }

        this.secrets = refinedSecrets;
    }
}

