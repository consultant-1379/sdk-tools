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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.pba.model.Docker;
import com.ericsson.component.aia.sdk.util.docker.exception.SdkArtifactoryDockerException;
import com.ericsson.component.aia.sdk.util.docker.exception.SdkDockerException;
import com.ericsson.component.aia.sdk.util.docker.exception.SdkDockerImageNotFoundException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;

/**
 * The Class SdkDockerService provides functionality to push docker images, delete docker images and check for the existence of docker images.
 */
public class SdkDockerService {

    private static final String COLON = ":";
    private static final String FORWARD_SLASH = "/";

    private static final Logger LOGGER = LoggerFactory.getLogger(SdkDockerService.class);
    private final DockerClientProvider dockerClientProvider;

    private final String artifactoryServerPath;
    private final String dockerRepoBasePath;
    private final String dockerRepoServerUrl;

    /**
     * Instantiates a new SDK docker service.
     *
     * @param dockerClientProvider
     *            the docker client provider
     *
     * @param artifactoryServerPath
     *            the Artifactory server path
     *
     * @param dockerRepoBasePath
     *            the docker repo base path
     *
     * @param dockerRepoServerUrl
     *            the docker repo server URL
     */
    public SdkDockerService(final DockerClientProvider dockerClientProvider, final String artifactoryServerPath, final String dockerRepoBasePath,
                            final String dockerRepoServerUrl) {

        this.dockerClientProvider = dockerClientProvider;
        this.artifactoryServerPath = artifactoryServerPath;
        this.dockerRepoBasePath = dockerRepoBasePath;
        this.dockerRepoServerUrl = dockerRepoServerUrl;
    }

    //TODO This method operates in the wrong way it should be searching for an artifact instead of assuming it exists.
    /**
     * Check if Docker repository exist.
     *
     * @param repoPath
     *            the repository path
     * @param imagePath
     *            the image path with respect to repoPath
     * @return true if docker image exists else false.
     */
    public boolean isDockerImageExistsInRepo(final String repoPath, String imagePath) {
        final Artifactory artifactory = dockerClientProvider.getArtifactoryClient();
        try {
            imagePath = imagePath.replace(COLON, FORWARD_SLASH);
            LOGGER.info("imagePath::{} repoPath::{} ", imagePath, repoPath);
            final File file = artifactory.repository(artifactoryServerPath).file(repoPath + FORWARD_SLASH + imagePath).info();
            if (file != null && !file.isFolder()) {
                return true;
            }

        } catch (final Exception exp) {
            return false; // if the specified artifact doesn't exist the info method throws an exception
        } finally {
            artifactory.close();
        }
        return false;
    }

    /**
     * Publish docker image from application zip to docker repository. This method will only push the specified docker image if it doesn't already
     * exist within the specified Docker repository.
     *
     * @param dockerImagesPath
     *            The path to the docker images zip file.
     * @param dockerInstance
     *            The docker entry from application PBA.
     * @param applicationVersion
     *            the version of the application
     */
    public void publishDockerImage(final Path dockerImagesPath, final Docker dockerInstance, final String applicationVersion) {

        try (final InputStream dockerImageStream = Files.newInputStream(dockerImagesPath);
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(dockerImageStream);
                final DockerClient docker = dockerClientProvider.getDockerClient();) {

            pushDockerImageToRepo(dockerInstance, docker, bufferedInputStream, applicationVersion);

        } catch (final IOException exp) {
            throw new SdkDockerException(exp);

        } catch (DockerException | InterruptedException exp) {
            throw new SdkDockerException(exp);

        } catch (final NoSuchElementException exp) {
            throw new SdkDockerImageNotFoundException("The docker tar file doesn't contain a valid docker image", exp);
        }
    }

    private void pushDockerImageToRepo(final Docker dockerInstance, final DockerClient docker, final BufferedInputStream bufferedInputStream,
                                       final String applicationVersion)
            throws DockerException, InterruptedException {

        Optional<String> dockerImageId = Optional.empty();
        try {

            final String imageName = docker.load(bufferedInputStream).iterator().next();
            final String remoteImageName = getRemoteImageName(imageName, applicationVersion);

            LOGGER.info("processing docker image::{}", imageName);
            dockerImageId = Optional.of(docker.inspectImage(imageName).id());

            if (!isDockerImageExistsInRepo(dockerRepoBasePath, remoteImageName)) {
                final String remoteImageTag = dockerRepoServerUrl + FORWARD_SLASH + dockerRepoBasePath + FORWARD_SLASH
                        + getRemoteImageName(imageName, applicationVersion);

                LOGGER.info("Tagging and pushing docker image::{}", remoteImageTag);
                docker.tag(imageName, remoteImageTag);
                docker.push(remoteImageTag);
            }

            dockerInstance.setName(imageName);
            dockerInstance.setRepoBaseUrl(dockerRepoServerUrl);
            dockerInstance.setRepoPath(dockerRepoBasePath);
            dockerInstance.setImagePath(remoteImageName);

        } finally {
            if (dockerImageId.isPresent()) {
                docker.removeImage(dockerImageId.get(), true, false);
            }
        }
    }

    /**
     * This version changes the version of a docker image so that it will be pushed to the remote repository with the same version as that of its
     * corresponding application.
     *
     * @param imageNameAndVerion
     *            the image Name And version separated by a colon.
     * @param applicationVersion
     *            The version of the docker images corresponding application.
     * @return imageNameAndVerion updated to the application version.
     */
    private String getRemoteImageName(final String imageNameAndVerion, final String applicationVersion) {
        final String imageName = imageNameAndVerion.split(COLON)[0];
        return imageName + COLON + applicationVersion;
    }

    /**
     * Deletes a docker image from the specified artifactory repository if it exits exists.
     *
     * @param repoBaseUrl
     *            the repo base URL
     * @param repoPath
     *            the repo name/path
     * @param imagePath
     *            the image path with respect to repoPath
     * @return true, if successful false if the image was not present or the delete was unsuccessful
     */
    public boolean deleteDockerImageFromRepo(final String repoBaseUrl, final String repoPath, final String imagePath) {

        final Artifactory artifactory = dockerClientProvider.getArtifactoryClient();
        try {
            final File file = artifactory.repository(artifactoryServerPath).file(repoPath + FORWARD_SLASH + imagePath).info();

            if (file != null && !file.isFolder()) {
                artifactory.repository(artifactoryServerPath).delete(repoPath + FORWARD_SLASH + imagePath);
                return true;
            }

        } catch (final Exception exp) {
            final String errMsg = String.format("Error deleting docker image:: %s/%s/%s from repository", repoBaseUrl, repoPath, imagePath);
            LOGGER.error(errMsg, exp);
            throw new SdkArtifactoryDockerException(errMsg, exp);
        } finally {
            artifactory.close();
        }

        return false;
    }

    public DockerClientProvider getDockerClientProvider() {
        return dockerClientProvider;
    }
}
