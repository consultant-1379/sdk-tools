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
package com.ericsson.component.aia.sdk.git.project.service;

import java.util.Optional;

/**
 * The Class GitProjectService.
 */
public abstract class GitProjectService {

    String gitAccessToken;
    String gitServiceUrl;
    String gitServiceSslUrl;

    /**
     * Creates the git repository.
     *
     * @param repoName
     *            the name of the git repo to create.
     * @param description
     *            description of the new Git repository.
     * @return the ssh url of the created git repository.
     */
    public abstract GitRepoInfo createGitRepository(final String repoName, final String description);

    /**
     * Deletes a git repository.
     *
     * @param repoName
     *            the name of the git repo to delete.
     * @return true if operation is successful else false
     */
    public abstract boolean deleteGitRepository(final String repoName);

    /**
     * Deletes a git repository tag.
     *
     * @param repoName
     *            the name of the git repo to delete.
     * @param tag
     *            - tag
     * @return true if operation is successful else false
     */
    public abstract boolean deleteGitRepositoryTag(final String repoName, final String tag);

    /**
     * Gets a new git project repository.
     *
     * @param serviceName
     *            the name of the scm service being used to create repos e.g. (gogs, gerrit, github)
     *
     * @param gitAccessToken
     *            access token used to make rest requests
     *
     * @param gitServiceUrl
     *            The URL of the rest service
     *
     * @return the new git project repository
     */
    public static GitProjectService newGitProjectRepository(final String serviceName, final String gitAccessToken, final String gitServiceUrl) {
        final GitProjectService gitProjectRepository = GitServiceType.valueOf(serviceName).getGitProjectRepository();
        gitProjectRepository.setAccessToken(gitAccessToken);
        gitProjectRepository.setGitServiceUrl(gitServiceUrl);
        return gitProjectRepository;
    }

    /**
     * Gets a new git project repository.
     *
     * @param serviceName
     *            the name of the scm service being used to create repos e.g. (gogs, gerrit, github)
     *
     * @param gitAccessToken
     *            access token used to make rest requests
     *
     * @param gitServiceUrl
     *            The URL of the rest service
     *
     * @param gitServiceSslUrl
     *            - service ssl url
     *
     * @return the new git project repository
     */
    public static GitProjectService newGitProjectRepository(final String serviceName, final String gitAccessToken, final String gitServiceUrl,
                                                            final String gitServiceSslUrl) {
        final GitProjectService gitProjectRepository = GitServiceType.valueOf(serviceName).getGitProjectRepository();
        gitProjectRepository.setAccessToken(gitAccessToken);
        gitProjectRepository.setGitServiceUrl(gitServiceUrl);
        gitProjectRepository.setGitServiceSslUrl(gitServiceSslUrl);
        return gitProjectRepository;
    }

    void setGitServiceUrl(final String gitServiceUrl) {
        this.gitServiceUrl = gitServiceUrl;
    }

    void setGitServiceSslUrl(final String gitServiceSslUrl) {
        this.gitServiceSslUrl = gitServiceSslUrl;
    }

    void setAccessToken(final String gitAccessToken) {
        this.gitAccessToken = gitAccessToken;
    }

    /**
     * Checks if the specified repository exists.
     *
     * @param repoName
     *            The name of the GIT repository.
     * @return A GitRepoInfo which optionally contains the http and the ssh url of the git repo
     */
    public abstract Optional<GitRepoInfo> getExistingGitRepository(final String repoName);

}
