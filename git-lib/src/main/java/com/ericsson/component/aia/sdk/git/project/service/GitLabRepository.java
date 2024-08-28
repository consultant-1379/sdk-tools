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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * @author ezsalro
 */
public class GitLabRepository extends GitProjectService {

    private static final String AUTHORIZATION_HEADER = "Private-Token";
    private static final Logger Log = LoggerFactory.getLogger(GitLabRepository.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Pattern HOST_PATTERN = Pattern.compile("^(http|HTTP)(s|S)?:\\/\\/([A-Za-z0-9_.]+(:\\d+)?)\\/.+");
    private static final Pattern SSH_PATTERN = Pattern.compile("^(ssh:\\/\\/)?git@([A-Za-z0-9_.]+(:\\d+)?)\\/.*");
    private static final String LOCALHOST = "localhost";

    private final OkHttpClient client = new OkHttpClient();
    private final String defaultOwner = System.getProperty("git.service.default.owner");

    private String serviceAddress;
    private String serviceSslAddress;

    @Override
    void setGitServiceUrl(final String gitServiceUrl) {
        if (gitServiceUrl != null) {
            final Matcher matcher = HOST_PATTERN.matcher(gitServiceUrl);
            if (matcher.matches()) {
                serviceAddress = matcher.group(3);
            }
        }
        super.setGitServiceUrl(gitServiceUrl);
    }

    @Override
    void setGitServiceSslUrl(final String gitServiceSslUrl) {
        if (gitServiceSslUrl != null) {
            final Matcher matcher = SSH_PATTERN.matcher(gitServiceSslUrl);
            if (matcher.matches()) {
                serviceSslAddress = matcher.group(2);
            }
        }
        super.setGitServiceSslUrl(gitServiceSslUrl);
    }

    @Override
    public Optional<GitRepoInfo> getExistingGitRepository(final String repoName) {
        Optional<GitRepoInfo> gitSshUrl = Optional.empty();

        try {

            final String identifier = createRepoId(repoName);

            final Request request = new Request.Builder().header(AUTHORIZATION_HEADER, gitAccessToken).url(gitServiceUrl + "/projects/" + identifier)
                    .get().build();

            try (final Response response = client.newCall(request).execute()) {
                Log.info("Checking if repo::{} exists", repoName);
                if (response.isSuccessful()) {
                    final JSONObject body = new JSONObject(response.body().string());
                    final GitRepoInfo gitRepoInfo = fromJson(body, repoName);
                    gitSshUrl = Optional.of(gitRepoInfo);
                }

            } catch (final IOException | JSONException exp) {
                Log.error("An exception occured while checking if git repo {} exists", repoName);
                throw new SdkGitException("Unable to check if repo exists", exp);
            }

            return gitSshUrl;

        } catch (final Exception ex) {
            throw new SdkGitException(ex);
        }
    }

    private String createRepoId(final String repoName) throws UnsupportedEncodingException {
        String owner = (this.defaultOwner != null ? this.defaultOwner : getRepoOwner(repoName));

        if (owner != null) {
            owner += "/";
        } else {
            owner = "";
        }

        return URLEncoder.encode(owner + repoName, "UTF-8");
    }

    @Override
    public GitRepoInfo createGitRepository(final String repoName, final String description) {
        try {
            final CreateRepoForm createRepoForm = new CreateRepoForm();
            createRepoForm.name = repoName;
            createRepoForm.description = StringUtils.abbreviate(description, 50);

            final JSONObject jsonObj = new JSONObject(createRepoForm);
            jsonObj.put("public", "true");
            Log.info("Creating repo::{}", jsonObj.toString());

            final Request request = new Request.Builder().header(AUTHORIZATION_HEADER, gitAccessToken).url(gitServiceUrl + "/projects")
                    .post(RequestBody.create(JSON, jsonObj.toString())).build();

            try (final Response response = client.newCall(request).execute()) {
                Log.info("Creation of new git lab repo REST request received response::{}", response.message());

                if (!response.isSuccessful()) {
                    throw new SdkGitException(
                            "Unsuccessful response recieved to git lab repo creation:" + response.message() + " body:" + response.body());
                }

                final String responseBody = response.body().string();

                Log.info("Got body response from git repo creation: {}", responseBody);

                final GitRepoInfo repo = fromJson(new JSONObject(responseBody), repoName);

                final String filePath = "info?branch=master&author_name=root&content=repo&commit_message=create%20a%20new%20repo";

                final Request req = new Request.Builder().header(AUTHORIZATION_HEADER, gitAccessToken)
                        .url(gitServiceUrl + "/projects/" + repo.getIdentifier() + "/repository/files/" + filePath)
                        .post(RequestBody.create(JSON, "{}")).build();

                try (final Response resp = client.newCall(req).execute()) {

                    if (!resp.isSuccessful()) {
                        throw new SdkGitException(
                                "Unsuccessful response recieved to git lab repo creation:" + response.message() + " body:" + response.body());
                    }
                }

                return repo;

            }
        } catch (IOException | JSONException exp) {
            Log.error("An exception occurred while creating the git repo");
            throw new SdkGitException("unable to create repo successfully", exp);
        }
    }

    private GitRepoInfo fromJson(final JSONObject json, final String repoName) throws JSONException, UnsupportedEncodingException {

        String sslUrl = json.getString("ssh_url_to_repo");
        String httpUrl = json.getString("http_url_to_repo");

        sslUrl = replaceSslServiceAddress(sslUrl);
        httpUrl = replaceServiceAddress(httpUrl, serviceAddress);

        String identifier = null;

        try {
            identifier = json.getString("id");
        } catch (final Exception ex) {
            Log.error("No ID found for git repo");
        }

        if (StringUtils.isEmpty(identifier)) {
            identifier = createRepoId(repoName);
        }

        return new GitRepoInfo(json.getString("name"), sslUrl, httpUrl, identifier, getOwner(json));
    }

    private String replaceServiceAddress(String url, final String address) {
        if (serviceAddress != null && url != null && url.indexOf(LOCALHOST) >= 0) {
            url = url.replace(LOCALHOST, address);
        }
        return url;
    }

    private String replaceSslServiceAddress(String url) {
        if (serviceAddress != null && url != null && url.indexOf(LOCALHOST + ":") >= 0) {
            url = url.replace(LOCALHOST + ":", serviceSslAddress + "/");
            if (!url.startsWith("ssh://")) {
                url = "ssh://" + url;
            }
        }
        return url;
    }

    private String getOwner(final JSONObject json) throws JSONException {
        final JSONObject obj = json.getJSONObject("owner");
        if (obj != null) {
            return obj.getString("username");
        }
        return null;
    }

    @Override
    public boolean deleteGitRepository(final String repoName) {

        return delete(repoName, null);

    }

    private boolean delete(final String repoName, final String tag) {
        try {

            final String identifier = createRepoId(repoName);

            if (identifier == null) {
                return true;
            }

            Log.trace("Delete Git repository method invoked for repository:{} id:{} tag:{}", repoName, identifier, tag);

            final String deletedTag = tag != null ? "/repository/tags/" + tag : "";

            final Request request = new Request.Builder().header(AUTHORIZATION_HEADER, gitAccessToken)
                    .url(gitServiceUrl + "/projects/" + identifier + deletedTag).delete().build();

            try (final Response response = client.newCall(request).execute()) {
                Log.info("Delete of repo REST request received response::{}", response.message());

                if (!response.isSuccessful()) {
                    throw new SdkGitException("Unsuccessful response received to delete repo REST call:" + response.message());
                }

                return true;

            }

        } catch (final IOException exp) {
            Log.error("An exception occured while deleting the git repo: {}", repoName);
            throw new SdkGitException("Unable to delete the repo:" + repoName, exp);
        }
    }

    @Override
    void setAccessToken(final String gitAccessToken) {
        this.gitAccessToken = gitAccessToken;
    }

    /**
     * @param repoName
     *            - repo
     * @return owner
     */
    protected String getRepoOwner(final String repoName) {

        Log.trace("Get the owner for the repository:{}", repoName);
        final Request request = new Request.Builder().header(AUTHORIZATION_HEADER, gitAccessToken).url(gitServiceUrl + "/projects").get().build();

        try (final Response response = client.newCall(request).execute();) {
            if (!response.isSuccessful()) {
                throw new SdkGitException(
                        "Unsuccessful response received for get gos repo REST call for identifying repo owner:" + response.message());
            }

            final String jsonBody = response.body().string();
            final String jsonpathRegex = "$[?(@.name == '" + repoName + "')].owner.username";
            return JsonPath.read(jsonBody, jsonpathRegex).toString().replaceAll("[\\[\\]\"]", "");
        } catch (final IOException exp) {
            Log.error("Unable to get the owner for git the repo:{}", repoName);
            throw new SdkGitException("Unable to get the owner for the repo:" + repoName, exp);
        }
    }

    /**
     * The Class CreateRepoForm.
     */
    public static class CreateRepoForm {
        String name;
        String description;
        @JsonProperty("private")
        boolean privateRepo;
        String license = "";
        String readme = "Default";
        boolean auto_init = true;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public boolean isPrivateRepo() {
            return privateRepo;
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
            return "CreateRepoForm [name=" + name + ", description=" + description + ", privateRepo=" + privateRepo + ", license=" + license
                    + ", readme=" + readme + ", auto_init=" + auto_init + "]";
        }

    }

    @Override
    public boolean deleteGitRepositoryTag(final String repoName, final String tag) {
        return delete(repoName, tag);

    }
}
