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

import static com.spotify.docker.client.messages.RegistryConfigs.create;
import static java.util.Collections.singletonMap;

import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.util.docker.exception.SdkDockerAuthorisationException;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.auth.FixedRegistryAuthSupplier;
import com.spotify.docker.client.auth.RegistryAuthSupplier;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.messages.RegistryAuth;

/**
 * The Class DockerRepoClient provides implementations of 3pp libraries that are used to interact with docker.
 */
public class DockerClientProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerClientProvider.class);
    private final String artifactoryServerUrl;

    private final String dockerClientUsername;
    private final String dockerClientPassword;

    private final String dockerRepoServerUrl;

    /**
     * Instantiates a new docker client provider.
     *
     * @param artifactoryServerUrl
     *            the artifactory server url
     * @param dockerClientUsername
     *            the docker client username
     * @param dockerClientPassword
     *            the docker client password
     * @param dockerRepoServerUrl
     *            the docker repo server url
     */
    public DockerClientProvider(final String artifactoryServerUrl, final String dockerClientUsername, final String dockerClientPassword,
                                final String dockerRepoServerUrl) {
        this.artifactoryServerUrl = artifactoryServerUrl;
        this.dockerClientUsername = dockerClientUsername;
        this.dockerClientPassword = dockerClientPassword;
        this.dockerRepoServerUrl = dockerRepoServerUrl;

        final String dockerUserPassword = dockerClientPassword == null ? "null" : dockerClientPassword.replaceAll(".", "*");
        LOGGER.info("logging into artifactory/docker repo using username: {} , pasword: {}", dockerClientUsername, dockerUserPassword);
    }

    /**
     * Gets a Artifactory client using the configured username and password. This client is capable of deleting docker images.
     *
     * @return {@link Artifactory} used to delete docker images
     */
    public Artifactory getArtifactoryClient() {
        return ArtifactoryClient.create(artifactoryServerUrl, dockerClientUsername, dockerClientPassword);
    }

    /**
     * Gets a DockerClient using the configured username and password. This client is capable of pushing docker images.
     *
     * @return {@link DockerClient} The created client
     * @throws SdkApplicationException
     */
    public DockerClient getDockerClient() {
        try {
            final RegistryAuth registryAuth = RegistryAuth.builder().serverAddress(dockerRepoServerUrl).username(dockerClientUsername)
                    .password(dockerClientPassword).build();

            final RegistryAuthSupplier registryAuthSupplier = new FixedRegistryAuthSupplier(registryAuth,
                    create(singletonMap(dockerRepoServerUrl, registryAuth)));

            return DefaultDockerClient.fromEnv().registryAuthSupplier(registryAuthSupplier).build();
        } catch (final DockerCertificateException exp) {
            throw new SdkDockerAuthorisationException(exp);
        }
    }

}
