package be.storefront.imicloud.service.mapper;

import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.service.dto.ImBlockDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity ImBlock and its DTO ImBlockDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ImBlockMapper {

    @Mapping(source = "imMap.id", target = "imMapId")
    @Mapping(source = "labelImage.id", target = "labelImageId")
    ImBlockDTO imBlockToImBlockDTO(ImBlock imBlock);

    List<ImBlockDTO> imBlocksToImBlockDTOs(List<ImBlock> imBlocks);

    @Mapping(source = "imMapId", target = "imMap")
    @Mapping(target = "images", ignore = true)
    @Mapping(source = "labelImageId", target = "labelImage")
    ImBlock imBlockDTOToImBlock(ImBlockDTO imBlockDTO);

    List<ImBlock> imBlockDTOsToImBlocks(List<ImBlockDTO> imBlockDTOs);

    default ImMap imMapFromId(Long id) {
        if (id == null) {
            return null;
        }
        ImMap imMap = new ImMap();
        imMap.setId(id);
        return imMap;
    }

    default Image imageFromId(Long id) {
        if (id == null) {
            return null;
        }
        Image image = new Image();
        image.setId(id);
        return image;
    }
}
