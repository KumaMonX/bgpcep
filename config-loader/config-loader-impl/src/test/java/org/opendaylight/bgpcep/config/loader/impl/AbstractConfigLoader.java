/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.bgpcep.config.loader.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.GuardedBy;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendaylight.bgpcep.config.loader.spi.ConfigFileProcessor;
import org.opendaylight.controller.md.sal.binding.impl.BindingToNormalizedNodeCodec;
import org.opendaylight.controller.md.sal.binding.test.AbstractConcurrentDataBrokerTest;
import org.opendaylight.controller.md.sal.binding.test.AbstractDataBrokerTestCustomizer;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;

public abstract class AbstractConfigLoader extends AbstractConcurrentDataBrokerTest {
    @GuardedBy("this")
    private final List<WatchEvent<?>> eventList = new ArrayList<>();
    protected ConfigLoaderImpl configLoader;
    @Mock
    protected WatchService watchService;
    @Mock
    protected ConfigFileProcessor processor;
    @Mock
    private WatchKey watchKey;
    @Mock
    private WatchEvent<?> watchEvent;
    @Mock
    private FileWatcher fileWatcher;
    private BindingToNormalizedNodeCodec mappingService;

    public AbstractConfigLoader() {
        super(true);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doAnswer(invocation -> true).when(this.watchKey).reset();
        doReturn(this.eventList).when(this.watchKey).pollEvents();
        doReturn(this.watchKey).when(this.watchService).take();
        doReturn("watchKey").when(this.watchKey).toString();
        doReturn("watchService").when(this.watchService).toString();
        doReturn("watchEvent").when(this.watchEvent).toString();
        doReturn(getResourceFolder()).when(this.fileWatcher).getPathFile();
        doReturn(this.watchService).when(this.fileWatcher).getWatchService();
        doAnswer(invocation -> {
            clearEvent();
            return null;
        }).when(this.processor).loadConfiguration(any());
        final SchemaContext schemaContext = getSchemaContext();
        this.configLoader = new ConfigLoaderImpl(schemaContext, this.mappingService, this.fileWatcher);
        this.configLoader.init();
    }

    @Override
    protected AbstractDataBrokerTestCustomizer createDataBrokerTestCustomizer() {
        final AbstractDataBrokerTestCustomizer customizer = super.createDataBrokerTestCustomizer();
        this.mappingService = customizer.getBindingToNormalized();
        return customizer;
    }

    private synchronized void clearEvent() {
        this.eventList.clear();
    }

    protected String getResourceFolder() {
        return ClassLoader.getSystemClassLoader().getResource("initial").getPath();
    }

    protected synchronized void triggerEvent(final String filename) {
        doReturn(filename).when(this.watchEvent).context();
        this.eventList.add(this.watchEvent);
    }

    @After
    public final void tearDown() throws Exception {
        this.configLoader.close();
    }
}
