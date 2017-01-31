package be.storefront.imicloud.domain.document;

import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.domain.document.structure.*;
import be.storefront.imicloud.domain.util.XmlDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by wouter on 30/01/2017.
 */
public class ImDocumentStructure {

    private ImDocument imDocument;
    private TreeNode rootTreeNode;

    public ImDocumentStructure(ImDocument imDocument) throws IOException, SAXException, ParserConfigurationException {
        this.imDocument = imDocument;
        this.rootTreeNode = generateTree(imDocument.getOriginalXml());
    }

    public TreeNode generateTree(String xml) throws ParserConfigurationException, SAXException, IOException {
        //TreeNode root = new TreeNode();

        // Cleanup XML string
        xml = xml.trim().replaceFirst("^([\\W]+)<", "<");

        // Test XML document
        Document xmlDoc = XmlDocument.getXmlDocumentFromString(xml);

        Element fsprodocumentElement = getRootNode(xmlDoc);

        ArrayList<Element> childElements = getChildElements(fsprodocumentElement);

        TreeNode top = null;

        StructurePublication currentPublication = null;
        StructurePart currentPart = null;
        StructureChapter currentChapter = null;
        StructureSection currentSection = null;
        StructureMap currentMap = null;

        for (Element e : childElements) {

            if ("documentinfo".equals(e.getNodeName())) {
                // Skip this node
                continue;
            }

            TreeNode current = null;
            String mapType = XmlDocument.getAttribute(e, "maptype");

            if ("overviewmap".equals(e.getNodeName()) && "publication".equals(mapType)) {
                // Start new publication
                currentPublication = new StructurePublication();
                current = currentPublication;

                top = currentPublication;

            } else if ("overviewmap".equals(e.getNodeName()) && "part".equals(mapType)) {
                // Start new part
                currentPart = new StructurePart();
                current = currentPart;

                // Reset all lower levels
                currentChapter = null;
                currentSection = null;
                currentMap = null;

                if (top == null) {
                    top = currentPart;
                }

                // Add this part to the publication
                if (currentPublication != null) {
                    currentPublication.addChild(currentPart);

                }


            } else if ("overviewmap".equals(e.getNodeName()) && "chapter".equals(mapType)) {
                // Start new chapter
                currentChapter = new StructureChapter();
                current = currentChapter;

                // Reset all lower levels
                currentSection = null;
                currentMap = null;

                if (top == null) {
                    top = currentChapter;
                }

                // Add this chapter to the part
                if (currentPart != null) {
                    currentPart.addChild(currentChapter);

                }else if(currentPublication != null){
                    // Structure is missing a level, attach to one above
                    currentPublication.addChild(currentChapter);
                }

            } else if ("overviewmap".equals(e.getNodeName()) && "section".equals(mapType)) {
                // Start new section
                currentSection = new StructureSection();
                current = currentSection;

                // Reset all lower levels
                currentMap = null;

                if (top == null) {
                    top = currentSection;
                }

                // Add this section to the chapter
                if (currentChapter != null) {
                    currentChapter.addChild(currentSection);

                }else if(currentPart != null){
                    currentPart.addChild(currentSection);

                }else if(currentPublication != null){
                    currentPublication.addChild(currentSection);
                }

            } else if ("map".equals(e.getNodeName())) {
                // Start new map
                currentMap = new StructureMap();
                current = currentMap;

                if (top == null) {
                    top = currentMap;
                }

                // add this map to the section
                if (currentSection != null) {
                    currentSection.addChild(currentMap);

                }else if (currentChapter != null) {
                    currentChapter.addChild(currentMap);

                }else if(currentPart != null){
                    currentPart.addChild(currentMap);

                }else if(currentPublication != null){
                    currentPublication.addChild(currentMap);
                }

            } else {
                // Unsupported type => Do nothing
                continue;
            }


            current.setTitle(getTitleFromElement(e));
            current.setGuid(XmlDocument.getAttribute(e, "guid"));
        }

        return top;


//        NodeList mapList = xmlDoc.getElementsByTagName("map");
//
//        for (int i = 0; i < mapList.getLength(); i++) {
//
//            Node oneMap = mapList.item(i);
//            if (oneMap.getNodeType() == Node.ELEMENT_NODE) {
//                Element oneMapElement = (Element) oneMap;
//
//                String mapGuid = oneMapElement.getAttribute("guid");
//                String mapLabel = getText(oneMap, "label");
//
//                // Is the map in an overviewmap? Use that label instead...
//                Node parentNode = oneMap.getParentNode();
//                if("overviewmap".equals(parentNode.getNodeName())){
//                    String overviewMapTitle = getText(parentNode, "title");
//
//                    if(mapLabel.length() == 0){
//                        mapLabel = overviewMapTitle;
//                    }else{
//                        mapLabel = overviewMapTitle + " - "+mapLabel;
//                    }
//                }
//
//                // Save all maps
//                ImMapDTO newMapDto = new ImMapDTO();
//                newMapDto.setGuid(mapGuid);
//                newMapDto.setImDocumentId(doc.getId());
//                newMapDto.setLabel(mapLabel);
//                newMapDto.setPosition((float) i);
//                newMapDto = imMapService.save(newMapDto);
//
//                NodeList blockList = oneMapElement.getElementsByTagName("block");
//
//                for (int j = 0; j < blockList.getLength(); j++) {
//                    Node oneBlock = blockList.item(j);
//
//                    if (oneBlock.getNodeType() == Node.ELEMENT_NODE) {
//                        Element oneBlockElement = (Element) oneBlock;
//
//                        String blockGuid = oneBlockElement.getAttribute("guid");
//                        String blockLabel = getText(oneBlock, "label");
//                        String blockImageSource = getAttributeFromChildNode(oneBlock, "image", "source");
//
//                        String contentText = null;
//
//                        NodeList contentNodes = oneBlockElement.getElementsByTagName("content");
//                        if (contentNodes.getLength() > 0) {
//                            Element contentElement = (Element) contentNodes.item(0);
//
//                            contentText = nodeToString(contentElement).trim();
//                        }
//
//                        contentText = transformContentToHtml(contentText);
//
//                        // Save all blocks
//                        ImBlockDTO newBlockDto = new ImBlockDTO();
//                        newBlockDto.setImMapId(newMapDto.getId());
//                        newBlockDto.setLabel(blockLabel);
//                        newBlockDto.setGuid(blockGuid);
//                        newBlockDto.setPosition((float) j);
//                        newBlockDto.setContent(contentText);
//                        newBlockDto.setLabelImageSource(blockImageSource);
//
//                        newBlockDto = imBlockService.save(newBlockDto);
//                    }
//                }
//            }
//        }

    }

    private Element getRootNode(Document xmlDoc) {
        NodeList rootChildren = xmlDoc.getChildNodes();

        for (int i = 0; i < rootChildren.getLength(); i++) {
            Node oneNode = rootChildren.item(i);
            if (oneNode.getNodeType() == Node.ELEMENT_NODE) {
                Element oneNodeElement = (Element) oneNode;

                if ("fsprodocument".equals(oneNodeElement.getNodeName())){
                    return oneNodeElement;
                }

            }
        }
        return null;
    }

    private String getTitleFromElement(Element e) {
        String r = XmlDocument.getText(e, "title");
        if (r == null) {
            r = XmlDocument.getText(e, "label");
        }
        return r;
    }


    /**
     * Loop all child nodes and only return the actual nodes
     *
     * @param parent
     * @return
     */
    private ArrayList<Element> getChildElements(Element parent) {
        NodeList children = parent.getChildNodes();
        ArrayList<Element> r = new ArrayList<>();

        for (int i = 0; i < children.getLength(); i++) {
            Node oneNode = children.item(i);
            if (oneNode.getNodeType() == Node.ELEMENT_NODE) {
                Element oneNodeElement = (Element) oneNode;
                r.add(oneNodeElement);
            }
        }
        return r;
    }

    public TreeNode getRootTreeNode() {
        return rootTreeNode;
    }

}
