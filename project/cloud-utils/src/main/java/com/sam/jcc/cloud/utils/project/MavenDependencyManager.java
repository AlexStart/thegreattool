package com.sam.jcc.cloud.utils.project;

import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.i.Experimental;
import lombok.Data;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static org.w3c.dom.Node.TEXT_NODE;

/**
 * @author Alexey Zhytnik
 * @since 13.01.2017
 */
class MavenDependencyManager {

    private static final String SCOPE_TAG = "scope";
    private static final String VERSION_TAG = "version";
    private static final String GROUP_ID_TAG = "groupId";
    private static final String DEPENDENCY_TAG = "dependency";
    private static final String ARTIFACT_ID_TAG = "artifactId";
    private static final String DEPENDENCIES_TAG = "dependencies";

    private final XmlSupport xmlSupport = new XmlSupport();

    public String add(File config, Dependency dependency) {
        final Document doc = xmlSupport.getDocument(config);

        failOnExistence(config, dependency);
        add(doc, getDependencyTag(doc), dependency);
        return xmlSupport.toString(doc);
    }

    private void failOnExistence(File config, Dependency dependency) {
        if (getAllDependencies(config).contains(dependency)) {
            throw new InternalCloudException();
        }
    }

    public List<Dependency> getAllDependencies(File config) {
        return getDependencies(config)
                .stream()
                .map(this::toDependency)
                .collect(toList());
    }

    private void add(Document doc, Node dependencies, Dependency dependency) {
        final Node dep = doc.createElement(DEPENDENCY_TAG);

        final Node artifactId = doc.createElement(ARTIFACT_ID_TAG);
        artifactId.setTextContent(dependency.getArtifactId());

        final Node groupId = doc.createElement(GROUP_ID_TAG);
        groupId.setTextContent(dependency.getGroupId());

        final Node version = doc.createElement(VERSION_TAG);
        version.setTextContent(dependency.getVersion());

        final Node scope = doc.createElement(SCOPE_TAG);
        scope.setTextContent(dependency.getScope());

        dep.appendChild(artifactId);
        dep.appendChild(groupId);
        dep.appendChild(version);
        dep.appendChild(scope);
        dependencies.appendChild(dep);
    }

    private List<Node> getDependencies(File config) {
        final Document doc = xmlSupport.getDocument(config);
        return xmlSupport.filter(getDependencyTag(doc), DEPENDENCY_TAG);
    }

    private Node getDependencyTag(Document doc) {
        final Node project = doc.getFirstChild();
        return xmlSupport.find(project, DEPENDENCIES_TAG).get();
    }

    private String getValue(Node node, String tag) {
        return xmlSupport.value(xmlSupport.find(node, tag).get())
                .orElseThrow(InternalCloudException::new);
    }

    private Dependency toDependency(Node node) {
        final Dependency d = new Dependency();

        d.setVersion(getValue(node, VERSION_TAG));
        d.setGroupId(getValue(node, GROUP_ID_TAG));
        d.setArtifactId(getValue(node, ARTIFACT_ID_TAG));

        final Optional<Node> scope = xmlSupport.find(node, SCOPE_TAG);
        scope.ifPresent(s -> d.setScope(xmlSupport.value(s).get()));
        return d;
    }

    @Data
    public static class Dependency {
        private String artifactId;
        private String groupId;
        private String version;
        private String scope = "compile";
    }

    @Experimental("XML DOM parser wrapper")
    private static class XmlSupport {

        public Document getDocument(File file) {
            try {
                final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                final Document doc = builder.parse(file);
                doc.getDocumentElement().normalize();
                return doc;
            } catch (ParserConfigurationException | SAXException | IOException e) {
                throw new InternalCloudException(e);
            }
        }

        public List<Node> filter(Node root, String tagName) {
            final List<Node> result = newArrayList();
            final NodeList nodes = root.getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {
                final Node n = nodes.item(i);
                if (n.getNodeName().equals(tagName)) result.add(n);
            }
            return result;
        }

        public Optional<Node> find(Node root, String tagName) {
            final NodeList nodes = root.getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {
                final Node n = nodes.item(i);
                if (n.getNodeName().equals(tagName)) return of(n);
            }
            return empty();
        }

        public Optional<String> value(Node root) {
            final NodeList nodes = root.getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {
                final Node n = nodes.item(i);
                if (n.getNodeType() == TEXT_NODE) return of(n.getNodeValue());
            }
            return empty();
        }

        public String toString(Node node) {
            try {
                final Transformer t = TransformerFactory.newInstance().newTransformer();
                t.setOutputProperty(OMIT_XML_DECLARATION, "yes");
                t.setOutputProperty(INDENT, "yes");

                final Writer writer = new StringWriter();
                t.transform(new DOMSource(node), new StreamResult(writer));
                return writer.toString();
            } catch (TransformerException e) {
                throw new InternalCloudException(e);
            }
        }
    }
}
