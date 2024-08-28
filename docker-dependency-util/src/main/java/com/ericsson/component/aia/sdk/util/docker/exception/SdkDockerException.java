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
 * SdkDockerException is thrown when an exception occurs during a docker operation.
 */
public class SdkDockerException extends RuntimeException {

    private static final long serialVersionUID = 4440905811956080892L;

    /**
     * Instantiates a new sdk docker exception.
     */
    public SdkDockerException() {
        super();
    }

    /**
     * Instantiates a new sdk docker exception.
     *
     * @param throwable
     *            the throwable
     */
    public SdkDockerException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Instantiates a new sdk docker exception.
     *
     * @param message
     *            the message
     */
    public SdkDockerException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new sdk docker exception.
     *
     * @param throwable
     *            the throwable
     * @param message
     *            the message
     */
    public SdkDockerException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
