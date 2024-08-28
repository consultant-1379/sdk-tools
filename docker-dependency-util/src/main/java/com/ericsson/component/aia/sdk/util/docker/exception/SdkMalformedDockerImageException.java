/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.util.docker.exception;

/**
 * SdkMalformedDockerImageException is a runtime exception thrown the docker image selected for an operation is not in a usable condition.
 */
public class SdkMalformedDockerImageException extends SdkDockerException {

    private static final long serialVersionUID = 4440905811956080892L;

    /**
     * Instantiates a new malformed docker image exception.
     */
    public SdkMalformedDockerImageException() {
        super();
    }

    /**
     * Instantiates a new malformed docker image exception.
     *
     * @param throwable
     *            the throwable
     */
    public SdkMalformedDockerImageException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Instantiates a new malformed docker image exception.
     *
     * @param message
     *            the message
     */
    public SdkMalformedDockerImageException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new malformed docker image exception.
     *
     * @param throwable
     *            the throwable
     * @param message
     *            the message
     */
    public SdkMalformedDockerImageException(final Throwable throwable, final String message) {
        super(message, throwable);
    }
}
