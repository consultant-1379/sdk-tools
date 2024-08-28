package com.ericsson.component.aia.sdk.pba.translator.metadata;

/**
 * Enum ecapsuates a DeploymentType.
 *
 * @author qchavar
 *
 */
public enum DeploymentType {

    /**
     * Standcluster signifies that the manifest built will be used to deploy the cluster & the application as a standalone application.
     */
    STANDALONE_CLUSTER,

    /**
     * Schedule signifies that the manifest build will be used to deploy the container on an existing cluster.
     */
    SCHEDULE;

}
