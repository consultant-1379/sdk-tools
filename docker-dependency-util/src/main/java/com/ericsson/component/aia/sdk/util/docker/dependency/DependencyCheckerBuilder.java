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
import com.ericsson.component.aia.sdk.pba.tools.PBASchemaTool;
import com.ericsson.component.aia.sdk.util.docker.SdkDockerService;

/**
 * This class is responsible for checking that an applications dependencies can be resolved to an active application or service
 */
public class DependencyCheckerBuilder {

    private String serviceCatalogName;
    private String applicationCatalogName;

    private MetaDataServiceIfc metaDataServiceManager;
    private PBASchemaTool pbaSchemaTool;
    private SdkDockerService sdkDockerService;

    /**
     * Sets the service catalog name.
     *
     * @param serviceCatalogName
     *            the service catalog name
     * @return the dependency checker builder
     */
    public DependencyCheckerBuilder setServiceCatalogName(final String serviceCatalogName) {
        this.serviceCatalogName = serviceCatalogName;
        return this;
    }

    /**
     * Sets the application catalog name.
     *
     * @param applicationCatalogName
     *            the application catalog name
     * @return the dependency checker builder
     */
    public DependencyCheckerBuilder setApplicationCatalogName(final String applicationCatalogName) {
        this.applicationCatalogName = applicationCatalogName;
        return this;
    }

    /**
     * Sets the meta data service manager.
     *
     * @param metaDataServiceManager
     *            the meta data service manager
     * @return the dependency checker builder
     */
    public DependencyCheckerBuilder setMetaDataServiceManager(final MetaDataServiceIfc metaDataServiceManager) {
        this.metaDataServiceManager = metaDataServiceManager;
        return this;
    }

    /**
     * Sets the pba schema tool.
     *
     * @param pbaSchemaTool
     *            the pba schema tool
     * @return the dependency checker builder
     */
    public DependencyCheckerBuilder setPbaSchemaTool(final PBASchemaTool pbaSchemaTool) {
        this.pbaSchemaTool = pbaSchemaTool;
        return this;
    }

    /**
     * Sets the sdk docker service.
     *
     * @param sdkDockerService
     *            the sdk docker service
     * @return the dependency checker builder
     */
    public DependencyCheckerBuilder setSdkDockerService(final SdkDockerService sdkDockerService) {
        this.sdkDockerService = sdkDockerService;
        return this;
    }

    /**
     * Builds a new DependencyChecker.
     *
     * @return the dependency checker
     */
    public DependencyChecker build() {
        return new DependencyChecker(serviceCatalogName, applicationCatalogName, metaDataServiceManager, pbaSchemaTool, sdkDockerService);
    }
}
