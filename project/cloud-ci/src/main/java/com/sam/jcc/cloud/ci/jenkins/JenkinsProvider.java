package com.sam.jcc.cloud.ci.jenkins;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.CIProjectStatus;
import com.sam.jcc.cloud.ci.exception.CIBuildNotFoundException;
import com.sam.jcc.cloud.ci.exception.CIProjectAlreadyExistsException;
import com.sam.jcc.cloud.ci.exception.CIServerNotAvailableException;
import com.sam.jcc.cloud.ci.impl.CIProjectDao;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.i.ci.ICIMetadata;
import com.sam.jcc.cloud.i.ci.ICIProvider;
import com.sam.jcc.cloud.provider.AbstractProvider;
import com.sam.jcc.cloud.provider.UnsupportedTypeException;
import lombok.Setter;
import org.apache.commons.lang.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.ci.CIProjectStatus.*;

/**
 * @author Alexey Zhytnik
 * @since 22-Dec-16
 */
@Component
public class JenkinsProvider extends AbstractProvider<ICIMetadata> implements ICIProvider, IHealth {

    private static final long JENKINS_PROVIDER_ID = 5L;

    @Setter
    @Autowired(required = false)
    @VisibleForTesting
    private Jenkins jenkins;

    @Setter
    @Autowired
    @VisibleForTesting
    private CIProjectDao dao;

    public JenkinsProvider(List<IEventManager<ICIMetadata>> eventManagers) {
        super(eventManagers);
    }

    @Override
    public boolean supports(ICIMetadata m) {
        return m instanceof CIProject;
    }

    @Override
    public boolean isEnabled() {
        return jenkins.isEnabled();
    }

    @Override
    public ICIMetadata preprocess(ICIMetadata m) {
        final CIProject project = asCIProject(m);
        if (dao.exist(project)) {
            throw new CIProjectAlreadyExistsException(project);
        }
        return project;
    }

    @Override
    public ICIMetadata process(ICIMetadata m) {
        checkAccess();

        final CIProject project = asCIProject(m);
        jenkins.create(project);
        jenkins.build(project);
        return project;
    }

    @Override
    public ICIMetadata postprocess(ICIMetadata m) {
        dao.create(asCIProject(m));
        updateStatus(m, CREATED);
        return m;
    }

    @Override
    public ICIMetadata read(ICIMetadata m) {
        checkAccess();

        final CIProject project = asCIProject(m);
        try {
            final byte[] build = jenkins.getLastSuccessfulBuild(project);
            project.setBuild(build);
            updateStatus(project, HAS_BUILD);
        } catch (CIBuildNotFoundException e) {
            updateStatus(project, HAS_NO_BUILD);
            throw e;
        }
        return project;
    }

    @Override
    public ICIMetadata update(ICIMetadata m) {
        checkAccess();

        jenkins.build(asCIProject(m));
        updateStatus(m, UPDATED);
        return m;
    }

    @Override
    public void delete(ICIMetadata m) {
        checkAccess();
        final CIProject project = asCIProject(m);

        jenkins.delete(project);
        dao.delete(project);
        updateStatus(project, DELETED);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ICIMetadata> findAll() {
        checkAccess();
        return (List<ICIMetadata>) (List<?>) jenkins.getAllProjects();
    }

    private void checkAccess() {
        if (!jenkins.isEnabled()) {
            throw new CIServerNotAvailableException();
        }
    }

    private void updateStatus(ICIMetadata project, CIProjectStatus status) {
        asCIProject(project).setStatus(status);
        notify(project);
    }

    private CIProject asCIProject(ICIMetadata metadata) {
        if (!supports(metadata)) {
            throw new UnsupportedTypeException(metadata);
        }
        return (CIProject) metadata;
    }

    @Override
    public Long getId() {
        return JENKINS_PROVIDER_ID;
    }

    @Override
    public IHealthMetadata checkHealth() {
        return new IHealthMetadata() {

            private final CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            @Override
            public Long getId() {
                return JENKINS_PROVIDER_ID;
            }

            @Override
            public String getName() {
                return getI18NDescription();
            }

            @Override
            public String getHost() {
                return getProperty("ci.jenkins.host");
            }

            @Override
            public String getPort() {
                return getProperty("ci.jenkins.port");
            }

            @Override
            public String getUrl() {
                try {
                    URL apiUrl = new URL(getProperty("ci.jenkins.url.api"));
                    HttpGet request = new HttpGet(apiUrl.toString());
                    HttpResponse response = httpClient.execute(request);
                    Validate.isTrue(response.getStatusLine().getStatusCode() == 200);
                    return createHostReplacedJenkinsUrl().toString() +
                            "\nJenkins: " +
                            response.getFirstHeader("X-Jenkins").getValue();
                } catch (Exception e) {
                    try {
                        return createHostReplacedJenkinsUrl().toString();
                    } catch (Exception e2) {
                        return null;
                    }
                }

            }

            /**
             * Create jenkins url with replacing host if need
             * @return url
             */
            private URL createHostReplacedJenkinsUrl() throws MalformedURLException, UnknownHostException {
                return new URL(
                        getProperty("ci.jenkins.protocol"),
                        //TODO[rfisenko 6/20/17]: why it need?
                        getHost().equals("localhost") ? InetAddress.getLocalHost().getHostName() : getHost(),
                        Integer.valueOf(getPort()),
                        getProperty("ci.jenkins.postfix"));
            }

            @Override
            public boolean isAlive() {
                try {
                    URL url = new URL(getProperty("ci.jenkins.url.api"));
                    HttpGet request = new HttpGet(url.toString());
                    HttpResponse response = httpClient.execute(request);
                    return (response.getStatusLine().getStatusCode() == 200);
                } catch (Exception e) {
                    return false;
                }
            }

        };
    }

    @Override
    public String getType() {
        return "jenkins";
    }

}
