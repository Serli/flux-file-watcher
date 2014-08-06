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

import com.codenvy.flux.watcher.core.Message;
import com.codenvy.flux.watcher.core.MessageHandler;
import com.codenvy.flux.watcher.core.MessageTypes;
import com.codenvy.flux.watcher.core.Resource;
import com.codenvy.flux.watcher.core.spi.RepositoryProvider;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.codenvy.flux.watcher.core.Message.Fields.CONTENT;
import static com.codenvy.flux.watcher.core.Message.Fields.HASH;
import static com.codenvy.flux.watcher.core.Message.Fields.PROJECT;
import static com.codenvy.flux.watcher.core.Message.Fields.RESOURCE;
import static com.codenvy.flux.watcher.core.Message.Fields.TIMESTAMP;
import static com.codenvy.flux.watcher.core.Message.Fields.TYPE;
import static com.codenvy.flux.watcher.core.MessageType.GET_RESOURCE_RESPONSE;
import static com.codenvy.flux.watcher.core.MessageType.RESOURCE_STORED;
import static com.codenvy.flux.watcher.core.Resource.ResourceType;
import static com.codenvy.flux.watcher.core.Resource.ResourceType.FILE;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Handler replying to a {@link com.codenvy.flux.watcher.core.MessageType#GET_RESOURCE_RESPONSE}.
 *
 * @author Kevin Pollet
 */
//TODO check timestamp, hash?
@Singleton
@MessageTypes(GET_RESOURCE_RESPONSE)
public class GetResourceResponseHandler implements MessageHandler {
    private final RepositoryProvider repositoryProvider;

    /**
     * Constructs an instance of {@link com.codenvy.flux.watcher.core.internal.GetResourceResponseHandler}.
     *
     * @param repositoryProvider
     *         the {@link com.codenvy.flux.watcher.core.spi.RepositoryProvider}.
     * @throws NullPointerException
     *         if {@code repositoryProvider} parameter is {@code null}.
     */
    @Inject
    GetResourceResponseHandler(RepositoryProvider repositoryProvider) {
        this.repositoryProvider = checkNotNull(repositoryProvider);
    }

    @Override
    public void onMessage(Message message) {
        try {

            final JSONObject request = message.content();
            final String projectName = request.getString(PROJECT.value());
            final String resourcePath = request.getString(RESOURCE.value());
            final long resourceTimestamp = request.getLong(TIMESTAMP.value());
            final String resourceHash = request.getString(HASH.value());
            final String resourceContent = request.getString(CONTENT.value());

            if (repositoryProvider.hasProject(projectName)) {
                final ResourceType resourceType = ResourceType.valueOf(request.getString(TYPE.value()).toUpperCase());

                if (resourceType == FILE) {
                    final Resource resource = Resource.newFile(projectName, resourcePath, resourceTimestamp, resourceContent.getBytes());

                    if (repositoryProvider.getResource(projectName, resourcePath) == null) {
                        repositoryProvider.createResource(resource);

                    } else {
                        repositoryProvider.updateResource(resource);
                    }

                    final JSONObject content = new JSONObject()
                            .put(PROJECT.value(), projectName)
                            .put(RESOURCE.value(), resourcePath)
                            .put(TIMESTAMP.value(), resourceTimestamp)
                            .put(HASH.value(), resourceHash)
                            .put(TYPE.value(), resourceType.name().toLowerCase());

                    message.source()
                           .sendMessage(new Message(RESOURCE_STORED, content));
                }
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
