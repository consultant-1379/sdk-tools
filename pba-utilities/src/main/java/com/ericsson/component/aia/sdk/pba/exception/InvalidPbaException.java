/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.pba.exception;

/**
 * This exception is thrown if the pba is invalid or if the pba file could not be loaded & parsed.
 *
 * @author echchik
 *
 */
public class InvalidPbaException extends RuntimeException {

    private static final long serialVersionUID = -8654711410983591567L;

    /**
     *
     * @param message
     *            describing exception condition.
     */
    public InvalidPbaException(final String message) {
        super(message);
    }

    /**
     *
     * @param message
     *            describing exception condition.
     * @param cause
     *            of exception condition
     */
    public InvalidPbaException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
