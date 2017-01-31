package be.storefront.imicloud.domain.document.structure;

import be.storefront.imicloud.service.dto.ImBlockDTO;

import java.util.ArrayList;

/**
 * Created by wouter on 30/01/2017.
 */
public class StructureMap extends TreeNode{

    private ArrayList<ImBlockDTO> blocks = new ArrayList<>();

    public String getCode(){
        return "map";
    }



    public void addBlock(ImBlockDTO blockDTO) {
        blocks.add(blockDTO);
    }

    public ArrayList<ImBlockDTO> getBlocks(){
        return blocks;
    }
}
