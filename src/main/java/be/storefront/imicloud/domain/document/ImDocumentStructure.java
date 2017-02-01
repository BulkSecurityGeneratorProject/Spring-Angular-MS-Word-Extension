package be.storefront.imicloud.domain.document;

import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.domain.document.structure.*;
import be.storefront.imicloud.domain.util.XmlDocument;
import be.storefront.imicloud.service.dto.ImBlockDTO;
import org.joox.Match;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;

import static org.joox.JOOX.$;

/**
 * Created by wouter on 30/01/2017.
 */
public class ImDocumentStructure {

    private ImDocument imDocument;
    private TreeNode rootTreeNode;

    public ImDocumentStructure(ImDocument imDocument) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        this.imDocument = imDocument;
        this.rootTreeNode = generateTree(imDocument.getOriginalXml());
    }

    public TreeNode generateTree(String xml) throws ParserConfigurationException, SAXException, IOException, TransformerException {
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
            String currentGuid = XmlDocument.getAttribute(e, "guid");

            if ("overviewmap".equals(e.getNodeName()) && "publication".equals(mapType)) {
                // Start new publication
                currentPublication = new StructurePublication();
                current = currentPublication;

                top = currentPublication;

                // Add the maps under this level
                createMapsAndAddToList(current, e);

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

                // Add the maps under this level
                createMapsAndAddToList(current, e);

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

                // Add the maps under this level
                createMapsAndAddToList(current, e);


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

                // Add the maps under this level
                createMapsAndAddToList(current, e);

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


                // Add the blocks to the map
                createBlocksAndAddToMap(currentMap, e);

                // For maps the content is always in the map
                currentMap.setContentGuid(currentMap.getGuid());

                currentMap.setContentGuid(currentGuid);


            } else {
                // Unsupported type => Do nothing
                continue;
            }


            current.setTitle(getTitleFromElement(e));
            current.setGuid(currentGuid);
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

    /**
     * Search for maps in the current tree node (publication, part, chapter, section)
     * @param treeNode
     * @param e
     * @throws TransformerException
     */
    private void createMapsAndAddToList(TreeNode treeNode, Element e) throws TransformerException {

        NodeList mapList = e.getElementsByTagName("map");

        for (int j = 0; j < mapList.getLength(); j++) {
            Node oneMap = mapList.item(j);

            if (oneMap.getNodeType() == Node.ELEMENT_NODE) {
                Element oneMapElement = (Element) oneMap;

                StructureMap map = new StructureMap();

                map.setTitle(getTitleFromElement(e));
                map.setGuid(XmlDocument.getAttribute(oneMapElement, "guid"));

                treeNode.addContentMap(map);

                if(treeNode.getContentGuid() == null){
                    treeNode.setContentGuid(map.getGuid());
                }

                createBlocksAndAddToMap(map, oneMapElement);
            }
        }

    }

    private void createBlocksAndAddToMap(StructureMap currentMap, Element e) throws TransformerException {

        NodeList blockList = e.getElementsByTagName("block");

        for (int j = 0; j < blockList.getLength(); j++) {
            Node oneBlock = blockList.item(j);

            if (oneBlock.getNodeType() == Node.ELEMENT_NODE) {
                Element oneBlockElement = (Element) oneBlock;

                String blockGuid = oneBlockElement.getAttribute("guid");
                String blockLabel = XmlDocument.getText(oneBlock, "label");
                String blockImageSource = XmlDocument.getAttributeFromChildNode(oneBlock, "image", "source");

                String contentText = null;

                NodeList contentNodes = oneBlockElement.getElementsByTagName("content");
                if (contentNodes.getLength() > 0) {
                    Element contentElement = (Element) contentNodes.item(0);

                    contentText = XmlDocument.nodeToString(contentElement).trim();
                }

                contentText = transformContentToHtml(contentText);

                // Save all blocks
                ImBlockDTO newBlockDto = new ImBlockDTO();
                //newBlockDto.setImMapId(newMapDto.getId());
                newBlockDto.setLabel(blockLabel);
                newBlockDto.setGuid(blockGuid);
                newBlockDto.setPosition((float) j);
                newBlockDto.setContent(contentText);
                newBlockDto.setLabelImageSource(blockImageSource);

                currentMap.addBlock(newBlockDto);

                //newBlockDto = imBlockService.save(newBlockDto);
            }
        }
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

    public ArrayList<StructureMap> getAllMaps(){
        TreeNode root = getRootTreeNode();

        ArrayList<StructureMap> r = new ArrayList<>();

        scanChildrenAndAddMapsToArray(root, r);

        return r;
    }

    private void scanChildrenAndAddMapsToArray(TreeNode parent, ArrayList<StructureMap> r ){
        if(parent.getContentMaps().size() > 0){
            for(StructureMap contentMap : parent.getContentMaps()){
                r.add(contentMap);
            }
        }
        if(parent.getChildren().size() > 0){
            for(TreeNode child : parent.getChildren()){
                scanChildrenAndAddMapsToArray(child, r);
            }
        }else if(parent instanceof StructureMap){
            r.add((StructureMap) parent);
        }
    }



    public TreeNode getRootTreeNode() {
        return rootTreeNode;
    }


    public static String transformContentToHtml(String contentText) {
        Match root = $(contentText);

        root.find("paragraph").rename("p");
        // Nested text in hyperlink
        root.find("hyperlink > text").rename("span");

        // All other text should be <p>
        root.find("text").rename("p");

        root.find("format[formattype=bold]").rename("strong").removeAttr("formattype");
        root.find("list[numbered=false]").rename("ul").removeAttr("numbered");
        root.find("list[numbered=true]").rename("ol").removeAttr("numbered");
        root.find("listitem").rename("li");

        XmlDocument.renameAttr(root.find("hyperlink").rename("a"), "address", "href");

        XmlDocument.renameAttr(root.find("image").rename("img"), "source", "data-source");


//        List<Match> imgs = root.find("image").rename("img").each();
//
//        // Rename image attr
//        for (Match img : imgs) {
//            renameAttr(img, "source", "src");
//        }

        // Rename table type to class
        XmlDocument.renameAttr(root.find("table[type]"), "type", "class");

        root.find("headerrow cell").rename("th");
        root.find("row cell").rename("td");

        // Convert header rows into <thead>
        Match headerRows = root.find("headerrow");
        headerRows.wrap("thead");
        headerRows.rename("tr");

        // Add a <tbody> and more normal rows inside it
        Match allTables = root.find("table");

        // Rename to <tr>
        root.find("row").rename("tr");

        // Move all direct child rows into <tbody>
        for (Match table : allTables.each()) {
            table.append("<tbody></tbody>");
            Match childRows = table.children("tr");
            Match tbody = table.find("tbody");

            // Add the rows to the <tbody>
            tbody.append(childRows);

            // Remove the original wrongly positioned rows
            //childRows.remove();
        }

        // If table cells only contain a single <p>, unwrap it


        //.wrap("tbody");

        // We need <li> around a sub-list
        root.find("ul > ul").wrap("li");
        root.find("ol > ol").wrap("li");
        root.find("ol > ul").wrap("li");
        root.find("ul > ol").wrap("li");

        // Unwrap nested <p><p>
        root.find("p > p").rename("span");

        contentText = root.toString();

        // Remove <content> and </content>
        contentText = contentText.replaceAll("<content>", "");
        contentText = contentText.replaceAll("</content>", "");

        // Remove whitespace between tags
        contentText = contentText.replaceAll(">\\s+<", "><");


        // Remove meaningless <p><p>... paragraph in paragraph
        // WOUTER: This can cause problems, for example <p bookmark="..."><p> is not handled
//        while (contentText.indexOf("<p><p>") != -1) {
//            contentText = contentText.replaceAll("<p><p>", "<p>");
//        }
//        while (contentText.indexOf("</p></p>") != -1) {
//            contentText = contentText.replaceAll("</p></p>", "</p>");
//        }

        // Remove meaningless <p/><p/><p/><p/>...
        while (contentText.indexOf("<p/><p/>") != -1) {
            contentText = contentText.replaceAll("<p/><p/>", "<p/>");
        }

        // Remove meaningless <p/><p>...
        while (contentText.indexOf("<p/><p>") != -1) {
            contentText = contentText.replaceAll("<p/><p>", "<p>");
        }

        // Remove meaningless </strong><strong>...
        while (contentText.indexOf("</strong><strong>") != -1) {
            contentText = contentText.replaceAll("</strong><strong>", "");
        }

        // Final trim
        contentText = contentText.trim();


        // Remove meaningless <p/> at the beginning
        while (contentText.length() > 4 && "<p/>".equals(contentText.substring(0, 4))) {
            contentText = contentText.substring(4);
        }

        // Remove meaningless <p/> at the end
        while (contentText.length() > 4 && "<p/>".equals(contentText.substring(contentText.length() - 4))) {
            contentText = contentText.substring(0, contentText.length() - 4);
        }

        return contentText;
    }

    public TreeNode getByGuid(String guid) {
        return findByGuid(guid, getRootTreeNode());
    }

    private TreeNode findByGuid(String guid, TreeNode treeNode){
        if(guid != null && guid.equals(treeNode.getGuid())){
            return treeNode;
        }else{
            for(TreeNode child : treeNode.getChildren()){
                TreeNode found = findByGuid(guid, child);
                if(found != null){
                    return found;
                }
            }
        }
        return null;
    }
}
