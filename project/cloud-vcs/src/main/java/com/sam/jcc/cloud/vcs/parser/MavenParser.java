package com.sam.jcc.cloud.vcs.parser;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import static java.text.MessageFormat.format;

/**
 * @author Alexey Zhytnik
 * @since 28-Nov-16
 */
class MavenParser implements IParser<String> {

    @Override
    public Entry<String, String> parse(String pom) {
        final Document doc = parsePom(pom);
        final String groupId = findValue(doc, "groupId");
        final String artifactId = findValue(doc, "artifactId");

        return new SimpleEntry<>(groupId, artifactId);
    }

    private Document parsePom(String pom) {
        try {
            final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final InputStream stream = new ByteArrayInputStream(pom.getBytes());
            final Document doc = builder.parse(stream);

            doc.getDocumentElement().normalize();
            return doc;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String findValue(Document doc, String tagName) {
        final NodeList elements = doc.getElementsByTagName(tagName);

        failOnNotFound(elements, tagName);
        return elements.item(0).getTextContent();
    }

    private void failOnNotFound(NodeList elements, String tag) {
        if (elements.getLength() == 0) {
            throw new RuntimeException(format("There's no \"{0}\" tag", tag));
        }
    }
}
