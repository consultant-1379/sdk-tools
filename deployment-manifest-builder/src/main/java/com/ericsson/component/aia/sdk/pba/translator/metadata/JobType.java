package com.ericsson.component.aia.sdk.pba.translator.metadata;

/**
 * Class signifies if the job is either a Batch or a streaming application.
 */
public enum JobType {

    /**
     * A stream application.
     */
    STREAMING,

    /**
     * A Batch job.
     */
    BATCH;
}
