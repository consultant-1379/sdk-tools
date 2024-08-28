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
package com.ericsson.component.aia.sdk.git.exceptions;

/**
 * This is an unchecked exception that wraps exceptions thrown by the GIT SDK.
 *
 */
public class SdkGitException extends RuntimeException {

    private static final long serialVersionUID = -6779686668999118987L;

    /**
     * Constructor used to proxy exceptions
     *
     * @param errMsg
     *            the error message.
     */
    public SdkGitException(final String errMsg) {
        super(errMsg);
    }

    /**
     * Constructor used to proxy exceptions
     *
     * @param exception
     *            The exception which should be proxied.
     */
    public SdkGitException(final Exception exception) {
        super(exception);
    }

    /**
     * Constructor used to proxy exceptions
     *
     * @param errMsg
     *            the error message.
     * @param exp
     *            the exception.
     */
    public SdkGitException(final String errMsg, final Throwable exp) {
        super(errMsg, exp);
    }

}
