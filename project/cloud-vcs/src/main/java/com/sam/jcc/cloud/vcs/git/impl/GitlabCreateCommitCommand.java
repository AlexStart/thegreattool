package com.sam.jcc.cloud.vcs.git.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang3.Validate.isTrue;

public class GitlabCreateCommitCommand {

    private final String apiVersion = "/api/v3";
    private final String defaultBranch = "master";

    public void call(VCSRepository repo, String repositoryUri, Integer projectId, String token) {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(getCreateCommitUri(repositoryUri, projectId));
        setRequestHeaders(request, token);
        try {

            request.setEntity(createRequestEntity(repo));
            HttpResponse response = httpClient.execute(request);
            isTrue(response.getStatusLine().getStatusCode() == 201,
                    "Commit to project with id=" + projectId + " failed");
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }

    private String getCreateCommitUri(String repositoryUri, Integer projectId) {
        return repositoryUri + apiVersion + "/projects/" + projectId + "/repository/commits";
    }

    private void setRequestHeaders(HttpPost request, String token) {
        request.setHeader("PRIVATE-TOKEN", token);
        request.setHeader("Content-Type", "application/json; charset=utf-8");
        request.setHeader("Accept-Encoding", "UTF-8");
        request.setHeader("Accept", "*/*");
    }

    private StringEntity createRequestEntity(VCSRepository repo) throws JsonProcessingException, UnsupportedEncodingException {
        CreateCommitData commitData = new CreateCommitData();
        commitData.setBranch(defaultBranch);
        commitData.setMessage(repo.getCommitMessage());
        commitData.setActions(getFileActions(repo.getSources(), EMPTY));
        ObjectMapper mapper = new ObjectMapper();
        return new StringEntity(mapper.writeValueAsString(commitData));
    }

    private List<FileAction> getFileActions(File sources, String dirPath) {
        FileManager fm = new FileManager();
        List<FileAction> allFiles = new ArrayList<>();
        fm.getDirectoryFiles(sources).forEach(s -> {
            String filePath = isEmpty(dirPath) ? s.getName() : dirPath + "/" + s.getName();
            if (s.isDirectory()) {
                allFiles.addAll(getFileActions(s, filePath));
            } else {
                allFiles.add(createFileAction(filePath, new String(fm.read(s), StandardCharsets.UTF_8)));
            }
        });
        return allFiles;
    }

    private FileAction createFileAction(String filePath, String content) {
        FileAction a = new FileAction();
        a.setAction("create");
        a.setFilePath(filePath);
        a.setContent(content);
        return a;
    }

    @Data
    class CreateCommitData {
        //Error in gitlab docs: 'branch_name' should be used instead of 'branch'
        @JsonProperty("branch_name")
        private String branch;
        @JsonProperty("commit_message")
        private String message;
        private List<FileAction> actions;
    }

    @Data
    class FileAction {
        private String action;
        @JsonProperty("file_path")
        private String filePath;
        private String content;
    }
}
