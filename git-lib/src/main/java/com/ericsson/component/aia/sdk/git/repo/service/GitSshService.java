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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.eclipse.jgit.api.DeleteTagCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.git.exceptions.SdkGitException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * This class provide API's to interact with remote GIT repository.
 */
public class GitSshService {

    /** Logger for GitReadOnlySSHRepositoryService. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GitSshService.class);

    /**
     * This method checks if remote repo exists.
     *
     * @param gitRepoUri
     *            the remote repo URI
     * @return true if remote repo exists else false.
     * @throws SdkGitException
     *             the sdk git exception
     */
    public boolean checkGitRepoExists(final String gitRepoUri) throws SdkGitException {
        boolean result = false;

        try {
            final Collection<Ref> remoteGitUris = Git.lsRemoteRepository().setHeads(true).setTags(true).setRemote(gitRepoUri)
                    .setTransportConfigCallback(new GitSSHTransportConfigCallback(new GitSSHJschConfigSessionFactory())).call();
            if (remoteGitUris != null && !remoteGitUris.isEmpty()) {
                result = true;
            }
        } catch (final GitAPIException exp) {
            LOGGER.error("exception occurred, when trying to call remote git URL {}", gitRepoUri, exp);
            throw new SdkGitException(exp);
        }
        return result;
    }

    /**
     * @param gitRepo
     *            - git repo
     * @param tag
     *            - tag
     * @return if tag was deleted
     * @throws SdkGitException
     *             - exception
     */
    public boolean deleteTag(final String gitRepo, final String tag) throws SdkGitException {

        try {

            final DeleteTagCommand cmd = Git.open(new File(gitRepo)).tagDelete();
            return cmd.setTags(tag).call().contains(tag);

        } catch (final Exception ex) {
            LOGGER.error("Exception occurred, when trying to delete tag {} for repo {} ", gitRepo, tag, ex);
            throw new SdkGitException(ex);
        }

    }

    /**
     * This method clone the remote repository to local filesystem.
     *
     * @param gitRepoUri
     *            the git repo uri
     * @param dirName
     *            the local dir to clone
     * @return the repository path
     * @throws SdkGitException
     *             the sdk git exception
     */
    public Path clone(final String gitRepoUri, final String dirName) throws SdkGitException {

        try {
            final Path pathToLocalRepository = Files.createTempDirectory(dirName);
            LOGGER.debug("Creating localRepository {}", pathToLocalRepository.toString());
            try (final Git gitRep = Git.cloneRepository().setURI(gitRepoUri).setDirectory(pathToLocalRepository.toFile())
                    .setTransportConfigCallback(new GitSSHTransportConfigCallback(new GitSSHJschConfigSessionFactory())).call()) {
            }
            return pathToLocalRepository;
        } catch (final Exception exp) {
            LOGGER.error("Exception occurred, when trying to initializing repository git {} with local directory name {} ", gitRepoUri, dirName, exp);
            throw new SdkGitException(exp);
        }
    }

    /**
     * This method checkouts out a tag or branch from the specified repo.
     *
     * @param gitDir
     *            the git dir
     * @param name
     *            the name of either the tag or branch
     * @throws SdkGitException
     *             the sdk git exception
     */
    public void checkout(final Path gitDir, final String name) throws SdkGitException {
        try (final Git gitRep = Git.open(gitDir.toFile())) {
            gitRep.checkout().setName(name).call();
        } catch (IOException | GitAPIException exp) {
            LOGGER.error("Exception occurred, when trying to checkout::{} from repository:: {} ", name, gitDir, exp);
            throw new SdkGitException(exp);
        }
    }

    /**
     * This method checkouts out a branch if the branch doesn't already exist it will be created.
     *
     * @param gitDir
     *            The git repository directory.
     * @param name
     *            The name of the branch.
     * @throws SdkGitException
     *             The sdk git exception.
     */
    public void checkoutBranch(final Path gitDir, final String name) throws SdkGitException {
        try (final Git git = Git.open(gitDir.toFile())) {
            // Check if the branch exists checkout and create if not existing already
            for (final Ref ref : git.branchList().call()) {
                if (ref.getName().endsWith(name)) {
                    git.checkout().setName(name).call();
                    return;
                }
            }
            git.checkout().setCreateBranch(true).setName(name).call();

        } catch (IOException | GitAPIException exp) {
            LOGGER.error("Exception occurred, when trying to checkout::{} from repository:: {} ", name, gitDir, exp);
            throw new SdkGitException(exp);
        }
    }

    /**
     * This method clone the remote repository to local filesystem.
     *
     * @param gitRepo
     *            the git repo
     * @param name
     *            the name of the user to commit with.
     * @param tag
     *            the tag
     * @param email
     *            the email of the user to commit with.
     * @param message
     *            the message
     * @throws SdkGitException
     *             the sdk git exception
     */
    public void pushToGitRepo(final Path gitRepo, final String name, final String tag, final String email, final String message)
            throws SdkGitException {

        try (final Git repository = Git.open(gitRepo.toFile())) {
            repository.add().addFilepattern(".").call();
            repository.commit().setCommitter(name, email).setMessage(message).call();
            repository.tag().setName(tag).call();
            repository.push().setForce(true).setPushTags().setPushAll().call();

        } catch (GitAPIException | IOException exp) {
            LOGGER.error("exception occurred, when trying to push changes to git repository::{} ", gitRepo, exp);
            throw new SdkGitException(exp);
        }
    }

    /**
     * This class is used to configure SSH connection with known_hosts and id_rsa configuration.
     *
     * @author echchik
     *
     */
    static class GitSSHJschConfigSessionFactory extends JschConfigSessionFactory {
        @Override
        protected JSch getJSch(final OpenSshConfig.Host hostConfig, final FS fileSystem) throws JSchException {
            final JSch jsch = super.getJSch(hostConfig, fileSystem);
            final File knownHosts = new File(new File(fileSystem.userHome(), ".ssh"), "known_hosts");
            final File identities = new File(new File(fileSystem.userHome(), ".ssh"), "id_rsa");
            jsch.setKnownHosts(knownHosts.getAbsolutePath());
            jsch.removeAllIdentity();
            jsch.addIdentity(identities.getAbsolutePath());
            return jsch;
        }

        @Override
        protected void configure(final Host hostConfig, final Session session) {
            session.setConfig("StrictHostKeyChecking", "no");
        }
    }

    /**
     * This class is used to configure SSH {@link TransportConfigCallback} with {@link JschConfigSessionFactory} configuration.
     *
     * @author echchik
     *
     */
    static class GitSSHTransportConfigCallback implements TransportConfigCallback {
        private final JschConfigSessionFactory jschConfigSessionFactory;

        /**
         * Constructor
         *
         * @param jschConfigSessionFactory
         *            jschConfigSessionFactory
         */
        GitSSHTransportConfigCallback(final JschConfigSessionFactory jschConfigSessionFactory) {
            this.jschConfigSessionFactory = jschConfigSessionFactory;
        }

        @Override
        public void configure(final Transport transport) {
            final SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(jschConfigSessionFactory);
        }
    }
}
