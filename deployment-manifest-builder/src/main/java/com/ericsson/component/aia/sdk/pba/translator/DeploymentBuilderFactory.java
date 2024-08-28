package com.ericsson.component.aia.sdk.pba.translator;

import com.ericsson.component.aia.sdk.pba.translator.metadata.DeploymentMetaData;
import com.ericsson.component.aia.sdk.pba.translator.metadata.OrchestratorType;

/**
 * Factory class to get the appropriate builder
 */
public final class DeploymentBuilderFactory {

    private DeploymentBuilderFactory() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Gets the builder based on the metadata supplied.
     *
     * @param type
     *            - {@code OrchestratorType}
     * @param dMetaData
     *            - {@code DeploymentMetaData}
     * @return - a type of {@code DeploymentManifestBuilder} or null if the orchestrator type is not supported yet.
     */
    public static DeploymentManifestBuilder getBuilderFor(final OrchestratorType type, final DeploymentMetaData dMetaData) {

        if (type == OrchestratorType.KUBERNETES) {
            return new KubernetesManifestBuilder(dMetaData);
        }

        return null;
    }

}
