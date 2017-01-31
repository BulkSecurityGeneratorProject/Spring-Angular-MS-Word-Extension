package be.storefront.imicloud.domain.util;

import org.joox.Match;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by wouter on 30/01/2017.
 */
public class XmlDocument {

    public static Document getXmlDocumentFromString(String xml) throws IOException, SAXException, ParserConfigurationException {
        // Check the XML
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        Document xmlDoc = dBuilder.parse(is);
        //optional, but recommended, read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        xmlDoc.getDocumentElement().normalize();

        return xmlDoc;
    }

    public static String getText(Node node, String childNodeName) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() == Node.ELEMENT_NODE && childNodeName.equals(childNode.getNodeName())) {
                return childNode.getTextContent().trim();
            }
        }
        return null;
    }

    public static String getAttributeFromChildNode(Node node, String childNodeName, String attrName) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNodeName.equals(childNode.getNodeName())) {
                return getAttribute(childNode, attrName);
            }
        }
        return null;
    }

    public static String getAttribute(Node node, String attrName) {
        if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap nnm = node.getAttributes();
            if (nnm != null) {
                Node namedItem = nnm.getNamedItem(attrName);
                if (namedItem != null) {
                    return namedItem.getNodeValue();
                }
            }
        }

        return null;
    }

    public static String nodeToString(Node node) throws TransformerException {
        StringWriter sw = new StringWriter();

        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.transform(new DOMSource(node), new StreamResult(sw));

        return sw.toString();
    }

    public static Match renameAttr(Match m, String oldName, String newName) {
        if (m.size() > 0) {
            for (Match item : m.each()) {
                String src = item.attr(oldName);
                item.attr(newName, src);
                item.removeAttr(oldName);
            }
        }
        return m;
    }

}
