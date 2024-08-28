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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is used to contain the http and ssh information required for accessing a git repo
 */
public class GitRepoInfo {
    private final String httpRepoUrl;
    private final String sshRepoUrl;
    private final String repoName;
    private final String identifier;
    private final String owner;

    /**
     * Instantiates a new git repo info.
     *
     * @param repoName
     *            the repo name
     * @param sshRepoUrl
     *            the ssh repo url
     * @param httpRepoUrl
     *            the http repo url
     * @param identifier
     *            - id
     * @param owner
     *            - owner
     */
    public GitRepoInfo(final String repoName, final String sshRepoUrl, final String httpRepoUrl, final String identifier, final String owner) {
        this.sshRepoUrl = sshRepoUrl;
        this.httpRepoUrl = httpRepoUrl;
        this.repoName = repoName;
        this.identifier = identifier;
        this.owner = owner;
    }

    /**
     * @param repoName
     *            - repo name
     * @param sshRepoUrl
     *            - ssh repo
     * @param httpRepoUrl
     *            - http repo
     */
    public GitRepoInfo(final String repoName, final String sshRepoUrl, final String httpRepoUrl) {
        this.sshRepoUrl = sshRepoUrl;
        this.httpRepoUrl = httpRepoUrl;
        this.repoName = repoName;
        this.identifier = null;
        this.owner = null;
    }

    public String getSshRepoUrl() {
        return sshRepoUrl;
    }

    public String getHttpRepoUrl() {
        return httpRepoUrl;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getOwner() {
        return owner;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "GitRepoInfo [httpRepoUrl=" + httpRepoUrl + ", sshRepoUrl=" + sshRepoUrl + ", repoName=" + repoName + ", id=" + identifier + ", owner="
                + owner + "]";
    }

    /**
     * Constructs a GitRepoInfo from Json containing the fields name, ssh_url & html_url.
     *
     * @param json
     *            the json
     * @return the git repo info
     * @throws JSONException
     *             the JSON exception
     */
    public static GitRepoInfo fromJson(final JSONObject json) throws JSONException {
        return new GitRepoInfo(json.getString("name"), json.getString("ssh_url"), json.getString("html_url"));
    }

}
