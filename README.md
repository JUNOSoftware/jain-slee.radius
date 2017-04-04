# jain-slee.radius
JAIN SLEE Radius Resource Adaptor. Copyright (C) 2017 JUNO Software SRL

Introduction
======


The Resource Adaptor provides Radius API for JAIN SLEE applications. It is built using TinyRadius stack and allows the developer to build server side Radius Applications.

Events represent the Radius Authentication and Accounting messages received by the Radius stack and are fired on specific server activities.

Activities
======


Radius Resource Adaptor defines the following activities:

* RadiusAccessSession

RadiusAccessSession activity represents server side of a Radius Authentication session.
This activity type is implicitly created by the Resource Adaptor upon reception of the Radius Access-Request message.

* RadiusAccountSession

RadiusAccountSession activity represents server side of a Radius Accounting session.
This activity type is implicitly created by the Resource Adaptor upon reception of the Radius Accounting-Start message.

Both activity types define methods required to expose the necessary information to JAIN SLEE services:

* public void accept(RadiusAccessAck ack);
This method sends an Access-Accept Response message

* public void reject(RadiusAccessNack nack);
This method sends an Access-Reject Response message

* public RadiusAccountingResponse createResponse(RadiusAccountingRequest request);
This method sends an Accounting Response message


Events
======


Radius Resource Adaptor defines the Radius protocol specific events, as follows:

org.mobicents.resources.radius.ACCESS_REQUEST

org.mobicents.resources.radius.ACCOUNT_START_REQUEST

org.mobicents.resources.radius.ACCOUNT_INTERIM_REQUEST

org.mobicents.resources.radius.ACCOUNT_STOP_REQUEST

Configuration
======


The Resource Adaptor supports configuration only at Resource Adaptor Entity creation time, the following table enumerates the configuration properties:

* authPort (java.lang.Integer) - The port which receives authentication requests. E.g. 1812

* accPort (java.lang.Integer) - The port which receives account requests. E.g. 1813

* secrets (java.lang.String) - These are the secrets used to authenticate NAS. E.g. 192.168.1.1:pswd

Prerequisites
======


In order to build the Radius Resource Adaptor, the following dependences are needed:

        <dependency>
            <groupId>org.tinyradius</groupId>
            <artifactId>tinyradius</artifactId>
            <version>1.0</version>
        </dependency>

License
======


Radius Resource Adaptor is licensed under GNU Affero GPL v3.0. For commercial use or custom development, please contact JUNO Software (https://www.juno-software.com)
