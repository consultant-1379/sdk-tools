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

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.git.exceptions.SdkGitException;
import com.jayway.jsonpath.JsonPath;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A factory for creating Repository objects.
 */
public class GogsRepository extends GitProjectService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Logger Log = LoggerFactory.getLogger(GogsRepository.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public Optional<GitRepoInfo> getExistingGitRepository(final String repoName) {
        Optional<GitRepoInfo> gitSshUrl = Optional.empty();

        final Request request = new Request.Builder().header(AUTHORIZATION_HEADER, gitAccessToken).url(gitServiceUrl + "/repos/root/" + repoName)
                .get().build();

        try (final Response response = client.newCall(request).execute();) {
            Log.info("Checking if repo::{} exists", repoName);
            if (response.isSuccessful()) {
                final JSONObject body = new JSONObject(response.body().string());
                final GitRepoInfo gitRepoInfo = GitRepoInfo.fromJson(body);
                gitSshUrl = Optional.of(gitRepoInfo);
            }

        } catch (final IOException | JSONException exp) {
            Log.error("An exception occured while checking if git repo {} exists", repoName);
            throw new SdkGitException("Unable to check if gogs repo exists", exp);
        }

        return gitSshUrl;
    }

    @Override
    public GitRepoInfo createGitRepository(final String repoName, final String description) {
        try {
            final CreateRepoForm createRepoForm = new CreateRepoForm();
            createRepoForm.name = repoName;
            createRepoForm.description = StringUtils.abbreviate(description, 50);

            final JSONObject jsonObj = new JSONObject(createRepoForm);
            Log.info("Creating repo::{}", jsonObj.toString());

            final Request request = new Request.Builder().header(AUTHORIZATION_HEADER, gitAccessToken).url(gitServiceUrl + "/user/repos")
                    .post(RequestBody.create(JSON, jsonObj.toString())).build();

            try (final Response response = client.newCall(request).execute();) {
                Log.info("Creation of new repo REST request received response::{}", response.message());

                if (!response.isSuccessful()) {
                    throw new SdkGitException(
                            "Unsuccessful response recieved to repo creation call message:" + response.message() + " body::" + response.body());
                }

                final JSONObject body = new JSONObject(response.body().string());
                return GitRepoInfo.fromJson(body);
            }
        } catch (IOException | JSONException exp) {
            Log.error("An exception occurred while creating the git repo");
            throw new SdkGitException("unable to create gogs repo successfully", exp);
        }
    }

    @Override
    public boolean deleteGitRepository(final String repoName) {
        final String repoOwner = getRepoOwner(repoName);

        Log.trace("Delete Git repository method invoked for repository:{} owned by:{}", repoName, repoOwner);

        final Request request = new Request.Builder().header(AUTHORIZATION_HEADER, gitAccessToken)
                .url(gitServiceUrl + "/repos/" + (StringUtils.isNoneEmpty(repoOwner) ? repoOwner + "/" : "") + repoName).delete().build();

        try (final Response response = client.newCall(request).execute();) {
            Log.info("Delete of repo REST request received response::{}", response.message());

            if (!response.isSuccessful()) {
                throw new SdkGitException("Unsuccessful response received to delete repo REST call:" + response.message());
            }

            return true;

        } catch (final IOException exp) {
            Log.error("An exception occured while deleting the git repo: {}", repoName);
            throw new SdkGitException("Unable to delete the Gogs repo:" + repoName + " owned by:" + repoOwner, exp);
        }

    }

    @Override
    void setAccessToken(final String gitAccessToken) {
        this.gitAccessToken = "token " + gitAccessToken;
    }

    private String getRepoOwner(final String repoName) {

        Log.trace("Get the owner for the repository:{}", repoName);
        final Request request = new Request.Builder().header(AUTHORIZATION_HEADER, gitAccessToken).url(gitServiceUrl + "/user/repos").get().build();

        try (final Response response = client.newCall(request).execute();) {
            if (!response.isSuccessful()) {
                throw new SdkGitException("Unsuccessful response received for get repo REST call when determining repo owner:" + response.message());
            }

            final String jsonBody = response.body().string();

            final String jsonpathRegex = "$[?(@.name == '" + repoName + "')].owner.username";
            // The JsonPath returns a string of form '["root"]'. We need to remove brackets + quotes to get the owner.
            return JsonPath.read(jsonBody, jsonpathRegex).toString().replaceAll("[\\[\\]\"]", "");
        } catch (final IOException exp) {
            Log.error("Unable to get the owner for the Gogs repo:{}", repoName);
            throw new SdkGitException("Unable to get the owner for the Gogs repo:" + repoName, exp);
        }
    }

    /**
     * The Class CreateRepoForm.
     */
    public static class CreateRepoForm {
        String name;
        String description;
        @JsonProperty("private")
        boolean private_repo;
        String license = "";
        String readme = "Default";
        boolean auto_init = true;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public boolean isPrivate_repo() {
            return private_repo;
        }

        public String getLicense() {
            return license;
        }

        public String getReadme() {
            return readme;
        }

        public boolean isAuto_init() {
            return auto_init;
        }

        @Override
        public String toString() {
            return "CreateRepoForm [name=" + name + ", description=" + description + ", private_repo=" + private_repo + ", license=" + license
                    + ", readme=" + readme + ", auto_init=" + auto_init + "]";
        }

    }

    @Override
    public boolean deleteGitRepositoryTag(final String repoName, final String tag) {
        throw new UnsupportedOperationException();
    }
}
