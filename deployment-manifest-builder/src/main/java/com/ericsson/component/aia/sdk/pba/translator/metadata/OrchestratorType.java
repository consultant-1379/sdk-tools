package com.ericsson.component.aia.sdk.pba.translator.metadata;

/**
 * Class signifies the supported orchestrator engine types.
 */
public enum OrchestratorType {

    /**
     * Signifies deployment on Kubernetes cluster.
     */
    KUBERNETES,

    /**
     * Signifies deployment on openstack cluster.
     */
    OPENSTACK,

    /**
     * Signifies deployment on Bare metal.
     */
    BAREMETAL;
}
