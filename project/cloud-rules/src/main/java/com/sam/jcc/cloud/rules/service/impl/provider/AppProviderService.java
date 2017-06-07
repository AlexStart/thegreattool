/**
 *
 */
package com.sam.jcc.cloud.rules.service.impl.provider;

import com.sam.jcc.cloud.app.AppMetadata;
import com.sam.jcc.cloud.app.AppProvider;
import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.rules.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author olegk
 *         <p>
 *         TODO
 */
@Service
public class AppProviderService implements IService<IAppMetadata> {

    @Autowired
    private AppProvider appProvider;

    @Override
    public IAppMetadata create(IAppMetadata t) {
        return null;
    }

    @Override
    public IAppMetadata read(IAppMetadata t) {
        return appProvider.read(t);
    }

    @Override
    public IAppMetadata update(IAppMetadata t) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(IAppMetadata t) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<? super IAppMetadata> findAll() {
        return appProvider.findAll();
    }

    @Override
    public IAppMetadata create(Map<?, ?> props) {
        AppMetadata project = new AppMetadata();
        project.setProjectName((String) props.get("projectName"));
        project.setType((String) props.get("type"));
        IAppMetadata created = appProvider.create(project);
        return created;
    }

    @Override
    public void delete(Map<String, String> props) {
        IAppMetadata project = new AppMetadata();
        project.setId(Long.valueOf(props.get("id")));
        appProvider.delete(project);
    }

    @Override
    public IAppMetadata update(Map<?, ?> props) {
        IAppMetadata project = new AppMetadata();
        project.setId(Long.valueOf((String) props.get("id")));
        project.setProjectName((String) props.get("projectName"));
        IAppMetadata updated = appProvider.update(project);
        return updated;
    }

    @Override
    public void findAndDelete(Map<String, String> props) {
        // TODO Auto-generated method stub
    }

    @Override
    public Map<Long, String> getNames() {
        // TODO Auto-generated method stub
        return null;
    }

}
