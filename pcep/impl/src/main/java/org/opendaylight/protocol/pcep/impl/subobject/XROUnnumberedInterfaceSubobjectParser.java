/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.impl.subobject;

import static org.opendaylight.protocol.util.ByteBufWriteUtil.writeUnsignedByte;
import static org.opendaylight.protocol.util.ByteBufWriteUtil.writeUnsignedInt;

import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedBytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.XROSubobjectParser;
import org.opendaylight.protocol.pcep.spi.XROSubobjectSerializer;
import org.opendaylight.protocol.pcep.spi.XROSubobjectUtil;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.exclude.route.object.xro.Subobject;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.exclude.route.object.xro.SubobjectBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.ExcludeRouteSubobjects.Attribute;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.UnnumberedSubobject;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.basic.explicit.route.subobjects.subobject.type.UnnumberedCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.basic.explicit.route.subobjects.subobject.type.UnnumberedCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.basic.explicit.route.subobjects.subobject.type.unnumbered._case.UnnumberedBuilder;

/**
 * Parser for {@link UnnumberedCase}
 */
public class XROUnnumberedInterfaceSubobjectParser implements XROSubobjectParser, XROSubobjectSerializer {

    public static final int TYPE = 4;

    private static final int RESERVED = 1;

    private static final int CONTENT_LENGTH = 10;

    @Override
    public Subobject parseSubobject(final ByteBuf buffer, final boolean mandatory) throws PCEPDeserializerException {
        Preconditions.checkArgument(buffer != null && buffer.isReadable(), "Array of bytes is mandatory. Can't be null or empty.");
        if (buffer.readableBytes() != CONTENT_LENGTH) {
            throw new PCEPDeserializerException("Wrong length of array of bytes. Passed: " + buffer.readableBytes() + "; Expected: "
                    + CONTENT_LENGTH + ".");
        }
        buffer.readerIndex(buffer.readerIndex() + RESERVED);
        final SubobjectBuilder builder = new SubobjectBuilder();
        builder.setMandatory(mandatory);
        builder.setAttribute(Attribute.forValue(UnsignedBytes.toInt(buffer.readByte())));
        final UnnumberedBuilder ubuilder = new UnnumberedBuilder();
        ubuilder.setRouterId(buffer.readUnsignedInt());
        ubuilder.setInterfaceId(buffer.readUnsignedInt());
        builder.setSubobjectType(new UnnumberedCaseBuilder().setUnnumbered(ubuilder.build()).build());
        return builder.build();
    }

    @Override
    public void serializeSubobject(final Subobject subobject, final ByteBuf buffer) {
        Preconditions.checkArgument(subobject.getSubobjectType() instanceof UnnumberedCase, "Unknown subobject instance. Passed %s. Needed UnnumberedCase.", subobject.getSubobjectType().getClass());
        final UnnumberedSubobject specObj = ((UnnumberedCase) subobject.getSubobjectType()).getUnnumbered();
        final ByteBuf body = Unpooled.buffer(CONTENT_LENGTH);
        body.writeZero(RESERVED);
        writeUnsignedByte(subobject.getAttribute() != null ? (short) subobject.getAttribute().getIntValue() : null, body);
        Preconditions.checkArgument(specObj.getRouterId() != null, "RouterId is mandatory.");
        writeUnsignedInt(specObj.getRouterId(), body);
        Preconditions.checkArgument(specObj.getInterfaceId() != null, "InterfaceId is mandatory.");
        writeUnsignedInt(specObj.getInterfaceId(), body);
        XROSubobjectUtil.formatSubobject(TYPE, subobject.isMandatory(), body, buffer);
    }
}
