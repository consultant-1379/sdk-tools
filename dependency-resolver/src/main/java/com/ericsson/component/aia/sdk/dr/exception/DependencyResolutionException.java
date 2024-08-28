package com.ericsson.component.aia.sdk.dr.exception;

/**
 * DependencyResolutionServiceException Custom Service Exception.
 * @author EMOIBMO
 *
 */
public class DependencyResolutionException extends RuntimeException {

    /**
     * DependencyResolutionServiceException serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * DependencyResolutionServiceException Constructor
     * @param message s
     */
    public DependencyResolutionException(final String message) {
        super(message);
    }

}
