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
package com.codenvy.flux.watcher.core.internal;

import com.codenvy.flux.watcher.core.FluxMessage;
import com.codenvy.flux.watcher.core.FluxMessageHandler;
import com.codenvy.flux.watcher.core.FluxMessageTypes;
import com.codenvy.flux.watcher.core.Repository;
import com.codenvy.flux.watcher.core.Resource;
import com.codenvy.flux.watcher.core.spi.Project;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Singleton;

import static com.codenvy.flux.watcher.core.FluxMessage.Fields.HASH;
import static com.codenvy.flux.watcher.core.FluxMessage.Fields.PROJECT;
import static com.codenvy.flux.watcher.core.FluxMessage.Fields.RESOURCE;
import static com.codenvy.flux.watcher.core.FluxMessage.Fields.TIMESTAMP;
import static com.codenvy.flux.watcher.core.FluxMessageType.GET_RESOURCE_REQUEST;
import static com.codenvy.flux.watcher.core.FluxMessageType.RESOURCE_CHANGED;

/**
 * Handler replying to a {@link com.codenvy.flux.watcher.core.FluxMessageType#RESOURCE_CHANGED}.
 *
 * @author Kevin Pollet
 */
@Singleton
@FluxMessageTypes(RESOURCE_CHANGED)
public final class ResourceChangedHandler implements FluxMessageHandler {
    @Override
    public void onMessage(FluxMessage message, Repository repository) throws JSONException {
        final JSONObject request = message.content();
        final String projectName = request.getString(PROJECT.value());
        final String resourcePath = request.getString(RESOURCE.value());
        final long resourceTimestamp = request.getLong(TIMESTAMP.value());
        final String resourceHash = request.getString(HASH.value());

        final Project project = repository.getProject(projectName);
        if (project != null) {
            final Resource localResource = project.getResource(resourcePath);

            if (localResource != null
                && !localResource.hash().equals(resourceHash)
                && localResource.timestamp() < resourceTimestamp) {

                final JSONObject content = new JSONObject()
                        .put(PROJECT.value(), projectName)
                        .put(RESOURCE.value(), resourcePath)
                        .put(TIMESTAMP.value(), resourceTimestamp)
                        .put(HASH.value(), resourceHash);

                message.source()
                       .sendMessage(new FluxMessage(GET_RESOURCE_REQUEST, content));
            }
        }
    }
}
