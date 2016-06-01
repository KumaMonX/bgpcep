/*
 * Copyright (c) 2016 AT&T Services, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.concepts;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.google.common.base.Charsets;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class KeyMapping extends HashMap<InetAddress, byte[]> {
    private static final long serialVersionUID = 1L;

    private KeyMapping() {
        super();
    }

    private KeyMapping(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    private KeyMapping(final int initialCapacity) {
        super(initialCapacity);
    }

    private KeyMapping(final Map<? extends InetAddress, ? extends byte[]> m) {
        super(m);
    }

    public static KeyMapping getKeyMapping(@Nonnull final InetAddress inetAddress, @Nullable final String password){
        if (!isNullOrEmpty(password)) {
            final KeyMapping keyMapping = new KeyMapping();
            keyMapping.put(inetAddress, password.getBytes(Charsets.US_ASCII));
            return keyMapping;
        }
        return null;
    }

    public static KeyMapping getKeyMapping(){
        final KeyMapping keyMapping = new KeyMapping();
        return keyMapping;
    }
}
