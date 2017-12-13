/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.bgpcep.config.loader.impl;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileWatcherImpl implements FileWatcher, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(FileWatcherImpl.class);
    private static final String INTERRUPTED = "InterruptedException";
    private static final String BGPCEP_CONFIG_FOLDER = "bgpcep";
    private static final String DEFAULT_APP_CONFIG_FILE_PATH = "etc" + File.separator + "opendaylight"
            + File.separator + BGPCEP_CONFIG_FOLDER + File.separator;
    private static final Path PATH = Paths.get(DEFAULT_APP_CONFIG_FILE_PATH);
    private final WatchService watchService;

    public FileWatcherImpl() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        final File file = new File(DEFAULT_APP_CONFIG_FILE_PATH);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                LOG.warn("Failed to create config directory {}", DEFAULT_APP_CONFIG_FILE_PATH);
                return;
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                FileWatcherImpl.this.watchService.close();
            } catch (final IOException e) {
                LOG.warn(INTERRUPTED, e);
            }
        }));
        PATH.register(this.watchService, OVERFLOW, ENTRY_CREATE);
        LOG.info("File Watcher service initiated");
    }

    @Override
    public String getPathFile() {
        return DEFAULT_APP_CONFIG_FILE_PATH;
    }

    @Override
    public synchronized WatchService getWatchService() {
        return this.watchService;
    }

    @Override
    public synchronized void close() throws Exception {
        if (this.watchService != null) {
            this.watchService.close();
        }
    }
}
