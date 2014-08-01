/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.flux.watcher.server.inject;

import com.codenvy.flux.watcher.server.FluxSyncEventService;
import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;

/**
 * Bindings for the flux sync service
 *
 * @author Stéphane Tournié
 */
@DynaModule
public class FluxSyncModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FluxSyncEventService.class);
        //bind(RepositoryProvider.class).to(VFSRepository.class).in(Singleton.class);
    }
}