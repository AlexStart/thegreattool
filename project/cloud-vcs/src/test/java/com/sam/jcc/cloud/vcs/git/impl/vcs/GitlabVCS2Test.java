package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.auth.InitOnceAdminBean;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.testcontainers.containers.wait.Wait.forHttp;

public class GitlabVCS2Test extends AbstractVCSTest {

    @ClassRule
    public static GenericContainer gitlab =
            new GenericContainer("gitlab/gitlab-ce:latest")
                    .withExposedPorts(443, 80, 22);

    public GitlabVCS2Test() {
        super(new GitlabServerVCS(new InitOnceAdminBean()));
    }

    @Test
    public void test() throws InterruptedException, IOException {
        sleep(180000);
        HttpGet request = new HttpGet("http://localhost:8080");
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertThat(response.getStatusLine().getStatusCode() == 200).isTrue();
    }
}
