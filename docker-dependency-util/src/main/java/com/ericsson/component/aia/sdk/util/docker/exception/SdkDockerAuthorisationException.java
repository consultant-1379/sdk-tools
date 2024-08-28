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
 * SdkMalformedDockerImageException is a runtime exception thrown when a docker operation cannot be completed because of bad credentials
 */
public class SdkDockerAuthorisationException extends SdkDockerException {

    private static final long serialVersionUID = 4440905811956080892L;

    /**
     * Instantiates a new sdk docker image not found exception.
     */
    public SdkDockerAuthorisationException() {
        super();
    }

    /**
     * Instantiates a new sdk docker image not found exception.
     *
     * @param throwable
     *            the throwable
     */
    public SdkDockerAuthorisationException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Instantiates a new sdk docker image not found exception.
     *
     * @param message
     *            the message
     */
    public SdkDockerAuthorisationException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new sdk docker authorisation exception.
     *
     * @param message
     *            the message
     * @param throwable
     *            the throwable
     */
    public SdkDockerAuthorisationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
