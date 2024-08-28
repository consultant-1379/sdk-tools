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
package com.ericsson.component.aia.sdk.git.repo.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.ericsson.component.aia.sdk.git.exceptions.SdkGitException;

/**
 * JUNIT class for GitReadOnlySSHRepositoryService.
 *
 * @author echchik
 *
 */
public class GitSshServiceTest {

    private final GitSshService gitSSHService = new GitSshService();

    @Test
    public void shouldCreateNewBranchIfNotAlreadyExisting() throws SdkGitException, IllegalStateException, GitAPIException {
        final Path testGitRepo = Paths.get("target/TestREPO2");
        try (final Git git = Git.init().setDirectory(testGitRepo.toFile()).call();) {
            git.commit().setMessage("Test commit 1 ").call();

            gitSSHService.checkoutBranch(testGitRepo, "Branch1");
            gitSSHService.checkoutBranch(testGitRepo, "Branch2");
            gitSSHService.checkoutBranch(testGitRepo, "Branch1");

            final List<String> branches = new ArrayList<>();
            for (final Ref ref : git.branchList().call()) {
                branches.add(ref.getName());
            }

            Assert.assertEquals(branches.get(0), "refs/heads/Branch1");
            Assert.assertEquals(branches.get(1), "refs/heads/Branch2");
        }
    }

    @Test
    public void shouldReturnTrueIfRemoteRepoExistsIsSuccessful() throws SdkGitException {
        assertTrue(
                gitSSHService.checkGitRepoExists("ssh://gerrit.ericsson.se:29418/AIA/com.ericsson.component.aia.sdk.templates/aia-flink-streaming"));
    }

    @Test(expected = SdkGitException.class)
    public void shouldThrowSdkGitExceptionIfRemoteRepoExistsIsUnSuccessful() throws SdkGitException {
        gitSSHService.checkGitRepoExists("ssh://gerrit.ericsson.se:29418/AIA/com.ericsson.component.aia.sdk.templates/aia-xxxxx-tttttt");
    }

    @Test
    public void shouldReturnValidPathIfCloneIsSuccessful() throws SdkGitException {
        final Path path = gitSSHService.clone("ssh://gerrit.ericsson.se:29418/AIA/com.ericsson.component.aia.sdk.templates/aia-flink-streaming",
                "test-dir");
        assertTrue(path.toFile().exists());
    }

    @Test(expected = SdkGitException.class)
    public void shouldThrowSdkGitExceptionIfCloneIsUnSuccessful() throws SdkGitException {
        gitSSHService.clone("ssh://gerrit.ericsson.se:29418/AIA/com.ericsson.component.aia.sdk.templates/aia-xxxxx-tttttt", "test-dir");
    }

    @Test
    public void shouldCheckoutTags() throws IllegalStateException, GitAPIException, IOException {
        final Path testGitRepo = Paths.get("target/TestREPO");
        final Path testFile = Paths.get("target/TestREPO/TestFile.txt");

        testGitRepo.toFile().mkdir();
        final Git git = Git.init().setDirectory(testGitRepo.toFile()).call();

        git.commit().setMessage("Test commit 1 ").call();
        git.tag().setName("TEST_TAG_1").call();

        testFile.toFile().createNewFile();
        git.add().addFilepattern("TestFile.txt").call();
        git.commit().setMessage("Test commit 2").call();
        git.tag().setName("TEST_TAG_2").call();
        assertTrue(testFile.toFile().exists());

        gitSSHService.checkout(testGitRepo, "TEST_TAG_1");
        assertFalse(testFile.toFile().exists());
    }

    @Test(expected = SdkGitException.class)
    public void shouldThrowExceptionWhenCheckoutFails() throws IllegalStateException, GitAPIException, IOException {
        gitSSHService.checkout(Paths.get("target/DOESNT_EXIST"), "TEST_TAG_1");
    }

    @AfterClass
    public static void cleanup() throws IOException {
        final Path testGitRepo = Paths.get("target/TestREPO");
        FileUtils.deleteDirectory(testGitRepo.toFile());
    }
}
