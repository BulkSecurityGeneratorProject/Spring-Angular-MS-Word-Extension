package be.storefront.imicloud.service.mapper;

import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.service.dto.ImageDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Image and its DTO ImageDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ImageMapper {

    @Mapping(source = "imBlock.id", target = "imBlockId")
    ImageDTO imageToImageDTO(Image image);

    List<ImageDTO> imagesToImageDTOs(List<Image> images);

    @Mapping(source = "imBlockId", target = "imBlock")
    Image imageDTOToImage(ImageDTO imageDTO);

    List<Image> imageDTOsToImages(List<ImageDTO> imageDTOs);

    default ImBlock imBlockFromId(Long id) {
        if (id == null) {
            return null;
        }
        ImBlock imBlock = new ImBlock();
        imBlock.setId(id);
        return imBlock;
    }
}
