/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.bgp.parser.impl.message.update;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.opendaylight.protocol.bgp.parser.spi.AttributeParser;
import org.opendaylight.protocol.bgp.parser.spi.AttributeSerializer;
import org.opendaylight.protocol.bgp.parser.spi.AttributeUtil;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.PathAttributes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.LocalPref;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.LocalPrefBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.update.PathAttributesBuilder;
import org.opendaylight.yangtools.yang.binding.DataObject;

public final class LocalPreferenceAttributeParser implements AttributeParser,AttributeSerializer {

    public static final int TYPE = 5;
    public static final int LOCAL_PREFS_LENGTH = 4;

    @Override
    public void parseAttribute(final ByteBuf buffer, final PathAttributesBuilder builder) {
        builder.setLocalPref(new LocalPrefBuilder().setPref(buffer.readUnsignedInt()).build());
    }

    @Override
    public void serializeAttribute(final DataObject attribute, final ByteBuf byteAggregator) {
        final LocalPref lp = ((PathAttributes) attribute).getLocalPref();
        if (lp == null) {
            return;
        }
        AttributeUtil.formatAttribute(AttributeUtil.TRANSITIVE, TYPE, Unpooled.copyInt(lp.getPref().intValue()), byteAggregator);
    }
}
