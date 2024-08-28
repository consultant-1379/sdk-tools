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
 * SdkPbaNotFoundException is a runtime exception thrown new a PBA is not found in a specified location.
 */
public class SdkPbaNotFoundException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4440905811956080892L;

    /**
     * Instantiates a new sdk pba not found exception.
     */
    public SdkPbaNotFoundException() {
        super();
    }

    /**
     * Instantiates a new sdk pba not found exception.
     *
     * @param throwable
     *            the throwable
     */
    public SdkPbaNotFoundException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Instantiates a new sdk pba not found exception.
     *
     * @param message
     *            the message
     */
    public SdkPbaNotFoundException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new sdk pba not found exception.
     *
     * @param message
     *            the message
     * @param throwable
     *            the throwable
     */
    public SdkPbaNotFoundException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
