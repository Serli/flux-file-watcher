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
package com.codenvy.flux.watcher.core.utils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.codenvy.flux.watcher.core.spi.Resource.ResourceType;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Kevin Pollet
 */
public final class ResourceHelper {
    private static final MessageDigest messageDigest;

    static {
        try {

            messageDigest = MessageDigest.getInstance("SHA-1");

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param type
     * @param content
     * @return
     */
    public static String hash(ResourceType type, byte[] content) {
        checkNotNull(type);
        if (content == null) {
            content = new byte[0];
        }

        switch (type) {
            case FILE: {
                final byte[] digest = messageDigest.digest(content);
                return DatatypeConverter.printHexBinary(digest);
            }

            default:
                return "0";
        }
    }

    /**
     * Disable instantiation.
     */
    private ResourceHelper() {
    }
}
