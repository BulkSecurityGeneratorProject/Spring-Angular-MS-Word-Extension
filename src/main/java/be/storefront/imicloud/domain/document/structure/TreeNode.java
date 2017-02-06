package be.storefront.imicloud.domain.document.structure;


import java.util.ArrayList;

/**
 * Created by wouter on 30/01/2017.
 */
abstract public class TreeNode {
    abstract public String getCode();

    private TreeNode parent = null;
    private ArrayList<TreeNode> children = new ArrayList<>();
    private ArrayList<StructureMap> contentMaps = new ArrayList<>();
    private String title = null;
    private String guid = null;
    private String contentGuid = null;

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public void addChild(TreeNode child) {
        this.children.add(child);
        child.setParent(this);
    }

    public ArrayList<TreeNode> getChildren(){
        return this.children;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getContentGuid() {
        return contentGuid;
    }

    public void setContentGuid(String contentGuid) {
        this.contentGuid = contentGuid;
    }

    public void addContentMap(StructureMap map) {
        this.contentMaps.add(map);
    }

    public ArrayList<StructureMap> getContentMaps(){
        return this.contentMaps;
    }

    public StructureMap findFirstMap() {
        return findFirstMapInNode(this);
    }

    private StructureMap findFirstMapInNode(TreeNode treeNode){
        if(treeNode instanceof StructureMap){
            return (StructureMap) treeNode;
        }else{
            for(TreeNode child : treeNode.getChildren()){
                return findFirstMapInNode(child);
            }
        }
        return null;
    }
}
