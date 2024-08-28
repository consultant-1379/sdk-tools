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
package com.ericsson.component.aia.sdk.util.docker.dependency;

import com.ericsson.aia.metadata.api.MetaDataServiceIfc;
import com.ericsson.aia.metadata.exception.MetaDataServiceException;
import com.ericsson.component.aia.sdk.pba.model.Container;
import com.ericsson.component.aia.sdk.pba.model.Docker;
import com.ericsson.component.aia.sdk.pba.model.PBAInstance;
import com.ericsson.component.aia.sdk.pba.model.Pba;
import com.ericsson.component.aia.sdk.pba.tools.PBASchemaTool;
import com.ericsson.component.aia.sdk.util.docker.SdkDockerService;
import com.ericsson.component.aia.sdk.util.docker.exception.SdkDockerImageNotFoundException;
import com.ericsson.component.aia.sdk.util.docker.exception.SdkPbaNotFoundException;

/**
 * This class is responsible for checking that an applications dependencies can be resolved to an active application or service
 */
public class DependencyChecker {

    private final MetaDataServiceIfc metaDataServiceManager;
    private final PBASchemaTool pbaSchemaTool;
    private final SdkDockerService sdkDockerService;
    private final String serviceCatalogName;
    private final String applicationCatalogName;

    /**
     * Instantiates a new dependency checker.
     *
     * @param serviceCatalogName
     *            the service catalog name
     * @param applicationCatalogName
     *            the application catalog name
     * @param metaDataServiceManager
     *            the meta data service manager
     * @param pbaSchemaTool
     *            the pba schema tool
     * @param sdkDockerService
     *            the sdk docker service
     */
    DependencyChecker(final String serviceCatalogName, final String applicationCatalogName, final MetaDataServiceIfc metaDataServiceManager,
                      final PBASchemaTool pbaSchemaTool, final SdkDockerService sdkDockerService) {

        this.metaDataServiceManager = metaDataServiceManager;
        this.pbaSchemaTool = pbaSchemaTool;
        this.sdkDockerService = sdkDockerService;
        this.applicationCatalogName = applicationCatalogName;
        this.serviceCatalogName = serviceCatalogName;
    }

    /**
     * Verify an applications dependencies exist. This method will throw a SdkApplicationException if the applications dependencies are not met.
     *
     * @param pba
     *            the pba
     */
    public void verifyDependenciesExist(final Pba pba) {
        for (final String applicationDependency : pba.getBuildInfo().getDependencies()) {
            if (!isExistingPba(applicationDependency, serviceCatalogName) && !isExistingPba(applicationDependency, applicationCatalogName)) {
                throw new SdkPbaNotFoundException();
            }
        }
    }

    private boolean isExistingPba(final String dependency, final String schema) {
        try {
            final PBAInstance pbaModel = pbaSchemaTool.getPBAModelInstance(metaDataServiceManager.get(schema, dependency));
            checkDockerImageExists(pbaModel);

            // If the PBA has dependencies check that they exist.
            if (!pbaModel.getPba().getBuildInfo().getDependencies().isEmpty()) {
                verifyDependenciesExist(pbaModel.getPba());
            }

            return pbaModel.getPba().getStatus().equalsIgnoreCase("active");
        } catch (final MetaDataServiceException e) {
            return false;
        }
    }

    private void checkDockerImageExists(final PBAInstance pbaModel) {
        final Container container = pbaModel.getPba().getBuildInfo().getContainer();
        if (container != null) {
            final Docker docker = container.getDocker();
            if (!sdkDockerService.isDockerImageExistsInRepo(docker.getRepoPath(), docker.getImagePath())) {
                throw new SdkDockerImageNotFoundException();
            }
        }
    }
}
