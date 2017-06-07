/**
 *
 */
package com.sam.jcc.cloud.rules.service.impl.provider;

import com.google.common.io.Files;
import com.sam.jcc.cloud.app.AppMetadata;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.i.ci.ICIMetadata;
import com.sam.jcc.cloud.i.ci.ICIProvider;
import com.sam.jcc.cloud.project.ProjectMetadata;
import com.sam.jcc.cloud.provider.UnsupportedCallException;
import com.sam.jcc.cloud.rules.service.IService;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author olegk
 *         <p>
 *         TODO
 */
@Service
public class CIProviderService implements IService<ICIMetadata> {

    @Autowired
    private List<ICIProvider> ciProviders;

    @Autowired
    private ProjectProviderService projectProviderService;

    @Autowired
    private AppProviderService appProviderService;

    @Autowired
    private FileManager files;

    @Autowired
    private ZipArchiveManager zipManager;

    @Override
    public ICIMetadata create(ICIMetadata t) {
        throw new UnsupportedCallException();
    }

    @Override
    public ICIMetadata read(ICIMetadata t) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ICIMetadata update(ICIMetadata t) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(ICIMetadata t) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<? super ICIMetadata> findAll() {
        List<? super ICIMetadata> cis = new ArrayList<>();
        ciProviders.forEach(ciProvider -> cis.add(new ICIMetadata() {

            @Override
            public String getName() {
                return ciProvider.getI18NName();
            }

            @Override
            public String getDescription() {
                return ciProvider.getI18NDescription();
            }

            @Override
            public String getType() {
                // TODO Auto-generated method stub
                return null;
            }

        }));
        return cis;
    }

    @Override
    public ICIMetadata create(Map<?, ?> props) {
        throw new UnsupportedCallException();
    }

    @Override
    public void delete(Map<String, String> props) {
        // TODO Auto-generated method stub

    }

    @Override
    @Transactional
    public ICIMetadata update(Map<?, ?> props) {
        ProjectMetadata project = getProjectMetadata((Long) props.get("id"));
        if (project == null || !project.hasSources()) {
            return null;
        }
        File tempDir = extractProjectSources(project);

        //TODO[rfisenko 6/6/17]: think about better solution without calling appService. In future it will replaced by getting git url.
        IAppMetadata appMetadata = getAppMetadata((String) props.get("name"));

        Long providerId = (Long) props.get("providerId");
        ICIProvider targetProvider = ciProviders.stream().filter(p -> p.getId().equals(providerId)).findAny()
                .orElse(null);
        if (targetProvider != null) {
            final CIProject job = new CIProject();
            job.setArtifactId((String) props.get("projectName"));
            job.setSources(tempDir);
            job.setType(targetProvider.getType());
            job.setVcsType(appMetadata.getVcs());
            return targetProvider.create(job);
        }
        return null;
    }

    @Override
    public void findAndDelete(Map<String, String> props) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<Long, String> getNames() {
        ciProviders.sort((p1, p2) -> {
            if (p1 != null && p2 != null && p1.getId() != null && p2.getId() != null) {
                return p1.getId().compareTo(p2.getId());
            }
            return 0;
        });

        Map<Long, String> names = new LinkedHashMap<>();
        ciProviders.forEach(p -> names.put(p.getId(), p.getI18NName()));

        return names;
    }

    /**
     * Extract project sources to temp directory
     *
     * @param project project data
     * @return temp directory with sources
     */
    private File extractProjectSources(ProjectMetadata project) {
        if (!project.hasSources()) {
            return null;
        }
        File tempZip; //TODO[rfisenko 6/6/17]: may be use com.sam.jcc.cloud.utils.files.TempFile
        try {
            tempZip = File.createTempFile(project.getArtifactId() + "-" + new Random().nextInt(10000), ".zip");
        } catch (IOException e) {
            return null;
        }
        File tempDir = Files.createTempDir();
        files.write(project.getProjectSources(), tempZip);
        zipManager.unzip(tempZip, tempDir);
        return tempDir;
    }

    private ProjectMetadata getProjectMetadata(Long id) {
        ProjectMetadata projectMetadata = new ProjectMetadata();
        projectMetadata.setId(id);
        return (ProjectMetadata) projectProviderService.read(projectMetadata);
    }

    private IAppMetadata getAppMetadata(String name) {
        AppMetadata appMetadata = new AppMetadata();
        appMetadata.setProjectName(name);
        return appProviderService.read(appMetadata);
    }

}
