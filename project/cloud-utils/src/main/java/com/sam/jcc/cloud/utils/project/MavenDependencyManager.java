package com.sam.jcc.cloud.utils.project;

import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.utils.project.DependencyManager.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static java.util.stream.Collectors.toList;

/**
 * @author Alexey Zhytnik
 * @since 13.01.2017
 */
class MavenDependencyManager implements IDependencyManager<Dependency> {

    private MavenXpp3Reader reader = new MavenXpp3Reader();
    private MavenXpp3Writer writer = new MavenXpp3Writer();

    @Override
    public String add(File config, Dependency dependency) {
        try (Reader pom = new FileReader(config)) {
            final Model model = reader.read(pom);
            model.addDependency(convert(dependency));

            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            writer.write(output, model);
            return new String(output.toByteArray(), UTF_8);
        } catch (IOException | XmlPullParserException e) {
            throw new InternalCloudException(e);
        }
    }

    @Override
    public List<Dependency> getAllDependencies(File config) {
        try (Reader pom = new FileReader(config)) {
            return reader.read(pom)
                    .getDependencies()
                    .stream()
                    .map(this::convert)
                    .collect(toList());
        } catch (IOException | XmlPullParserException e) {
            throw new InternalCloudException(e);
        }
    }

    private org.apache.maven.model.Dependency convert(Dependency dependency) {
        final org.apache.maven.model.Dependency d = new org.apache.maven.model.Dependency();

        d.setScope(dependency.getScope());
        d.setVersion(dependency.getVersion());
        d.setGroupId(dependency.getGroupId());
        d.setArtifactId(dependency.getArtifactId());
        return d;
    }

    private Dependency convert(org.apache.maven.model.Dependency dependency) {
        final Dependency d = new Dependency();

        d.setScope(dependency.getScope());
        d.setVersion(dependency.getVersion());
        d.setGroupId(dependency.getGroupId());
        d.setArtifactId(dependency.getArtifactId());
        return d;
    }
}
