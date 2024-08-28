package com.ericsson.component.aia.sdk.pba.translator.metadata;

/**
 * Enum encapsulates the policy that signifies if the application container needs to be restarted on failure or not.
 */
public enum JobPolicy {

    /**
     * Restarts the container on failure.
     */
    RESTART_ON_FAILURE,

    /**
     * Does not restart the container on failure.
     */
    NEVER_RESTART;

}
