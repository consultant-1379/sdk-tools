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

/**
 * The Enum GitServiceType is used to select which git services can be used.
 */
public enum GitServiceType {

    GOGS(new GogsRepository()), GitLab(new GitLabRepository());

    private final GitProjectService gitProjectRepository;

    /**
     * Instantiates a new git service type.
     *
     * @param gitProjectRepository
     *            the git project repository
     */
    GitServiceType(final GitProjectService gitProjectRepository) {
        this.gitProjectRepository = gitProjectRepository;
    }

    public GitProjectService getGitProjectRepository() {
        return gitProjectRepository;
    }

}
