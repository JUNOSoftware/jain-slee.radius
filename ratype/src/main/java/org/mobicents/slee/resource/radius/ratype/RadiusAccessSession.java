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

package org.mobicents.slee.resource.radius.ratype;

import org.mobicents.resources.radius.RadiusAccessAck;
import org.mobicents.resources.radius.RadiusAccessNack;


/**
 * @author <a href="mailto:m@juno-software.com"> Mihnea Teodorescu </a>
 */


public interface RadiusAccessSession extends RadiusSession {

    enum Status {CREATED, ACCEPTED, REJECTED}

    public RadiusAccessAck createAck();

    public RadiusAccessAck createNack();

    public void accept(RadiusAccessAck ack);

    public void reject(RadiusAccessNack nack);

    public Status getStatus();
}
