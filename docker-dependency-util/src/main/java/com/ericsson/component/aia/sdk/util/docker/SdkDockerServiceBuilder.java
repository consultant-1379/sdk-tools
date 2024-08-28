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
package com.ericsson.component.aia.sdk.util.docker;

/**
 * The Class SdkDockerServiceBuilder.
 */
public class SdkDockerServiceBuilder {

    private String artifactoryServerUrl;
    private String dockerClientUsername;
    private String dockerClientPassword;

    private String artifactoryServerPath;
    private String dockerRepoBasePath;
    private String dockerRepoServerUrl;
    private DockerClientProvider dockerProvider;

    /**
     * Sets the artifactory server url.
     *
     * @param artifactoryServerUrl
     *            the artifactory server url
     * @return the sdk docker service builder
     */
    public SdkDockerServiceBuilder setArtifactoryServerUrl(final String artifactoryServerUrl) {
        this.artifactoryServerUrl = artifactoryServerUrl;
        return this;
    }

    /**
     * Sets the docker provider.
     *
     * @param dockerProvider
     *            the docker provider
     * @return the sdk docker service builder
     */
    public SdkDockerServiceBuilder setDockerProvider(final DockerClientProvider dockerProvider) {
        this.dockerProvider = dockerProvider;
        return this;
    }

    /**
     * Sets the docker client username.
     *
     * @param dockerClientUsername
     *            the docker client username
     * @return the sdk docker service builder
     */
    public SdkDockerServiceBuilder setDockerClientUsername(final String dockerClientUsername) {
        this.dockerClientUsername = dockerClientUsername;
        return this;
    }

    /**
     * Sets the docker client password.
     *
     * @param dockerClientPassword
     *            the docker client password
     * @return the sdk docker service builder
     */
    public SdkDockerServiceBuilder setDockerClientPassword(final String dockerClientPassword) {
        this.dockerClientPassword = dockerClientPassword;
        return this;
    }

    /**
     * Sets the artifactory server path.
     *
     * @param artifactoryServerPath
     *            the artifactory server path
     * @return the sdk docker service builder
     */
    public SdkDockerServiceBuilder setArtifactoryServerPath(final String artifactoryServerPath) {
        this.artifactoryServerPath = artifactoryServerPath;
        return this;
    }

    /**
     * Sets the docker repo base path.
     *
     * @param dockerRepoBasePath
     *            the docker repo base path
     * @return the sdk docker service builder
     */
    public SdkDockerServiceBuilder setDockerRepoBasePath(final String dockerRepoBasePath) {
        this.dockerRepoBasePath = dockerRepoBasePath;
        return this;
    }

    /**
     * Sets the docker repo server url.
     *
     * @param dockerRepoServerUrl
     *            the docker repo server url
     * @return the sdk docker service builder
     */
    public SdkDockerServiceBuilder setDockerRepoServerUrl(final String dockerRepoServerUrl) {
        this.dockerRepoServerUrl = dockerRepoServerUrl;
        return this;
    }

    /**
     * Builds the SdkDockerService.
     *
     * @return the sdk docker service
     */
    public SdkDockerService build() {
        if (dockerProvider == null) {
            dockerProvider = new DockerClientProvider(artifactoryServerUrl, dockerClientUsername, dockerClientPassword, dockerRepoServerUrl);
        }
        return new SdkDockerService(dockerProvider, artifactoryServerPath, dockerRepoBasePath, dockerRepoServerUrl);
    }

}
