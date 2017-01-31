package be.storefront.imicloud.domain.document.structure;

import java.util.ArrayList;

/**
 * Created by wouter on 30/01/2017.
 */
abstract public class TreeNode {
    abstract public String getCode();
    private ArrayList<TreeNode> children = new ArrayList<>();
    private String title = null;
    private String guid = null;

    public void addChild(TreeNode child) {
        this.children.add(child);
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
}
