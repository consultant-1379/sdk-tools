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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.component.aia.sdk.git.exceptions.SdkGitException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

/**
 * JUNIT class for Git repository service.
 *
 */
public class GitProjectRepositoryTest {
    private static WireMockServer wireMockServer;
    private static final String AUTHORIZATION_TOKEN = "1234";
    private static final String INVALID_AUTHORIZATION_TOKEN = "4321";
    private static String serviceUrl;

    @BeforeClass
    public static void setupServer() {
        wireMockServer = new WireMockServer(wireMockConfig().withRootDirectory("src/test/resources").dynamicPort());
        wireMockServer.start();
        WireMock.configureFor(wireMockServer.port());

        wireMockServer.stubFor(post(urlEqualTo("/api/v1/user/repos")).withHeader("Authorization", matching("token " + AUTHORIZATION_TOKEN))
                .willReturn(aResponse().withBodyFile("GogsResponseJson.json").withStatus(200)));

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/repos/root/repoThatExists"))
                .willReturn(aResponse().withBodyFile("GogsRepoResponseJson.json").withStatus(200)));

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/repos/root/repoThatDoesntExists")).willReturn(aResponse().withBody("").withStatus(404)));

        serviceUrl = "http://localhost:" + wireMockServer.port() + "/api/v1";
    }

    @Test
    public void shouldReturnUrlOfNewRepo() throws SdkGitException, Exception {
        final GitProjectService gitProjectRepository = GitProjectService.newGitProjectRepository("GOGS", AUTHORIZATION_TOKEN, serviceUrl);

        final String scmUrl = gitProjectRepository.createGitRepository("MyNewTestRepo", "description").getSshRepoUrl();
        assertThat(scmUrl, is("ssh://git@localhost/root/MyNewTestRepo.git"));
    }

    @Test(expected = SdkGitException.class)
    public void shouldThrowGitExceptionIfRepoNotCreated() throws SdkGitException, Exception {
        final GitProjectService gitProjectRepository = GitProjectService.newGitProjectRepository("GOGS", INVALID_AUTHORIZATION_TOKEN, serviceUrl);

        final String scmUrl = gitProjectRepository.createGitRepository("MyNewTestRepo", "description").getSshRepoUrl();
        assertThat(scmUrl, is("ssh://git@localhost/root/MyNewTestRepo.git"));
    }

    @Test
    public void shouldReturnTrueIfReposistoryExists() throws SdkGitException, Exception {
        final GitProjectService gitProjectRepository = GitProjectService.newGitProjectRepository("GOGS", "b35b6595056573f80c60819a63e0a3947b37137d",
                serviceUrl);

        assertTrue(gitProjectRepository.getExistingGitRepository("repoThatExists").isPresent());
    }

    @Test
    public void shouldReturnFalseIfReposistoryDoesntExists() throws SdkGitException, Exception {
        final GitProjectService gitProjectRepository = GitProjectService.newGitProjectRepository("GOGS", "b35b6595056573f80c60819a63e0a3947b37137d",
                serviceUrl);

        assertFalse(gitProjectRepository.getExistingGitRepository("repoThatDoesntExists").isPresent());
    }

    @AfterClass
    public static void serverShutdown() {
        wireMockServer.stop();
    }

}
