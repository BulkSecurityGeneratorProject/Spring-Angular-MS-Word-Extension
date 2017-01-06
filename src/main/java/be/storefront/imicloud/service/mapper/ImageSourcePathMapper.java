package be.storefront.imicloud.service.mapper;

import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.service.dto.ImageSourcePathDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity ImageSourcePath and its DTO ImageSourcePathDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ImageSourcePathMapper {

    @Mapping(source = "image.id", target = "imageId")
    @Mapping(source = "imDocument.id", target = "imDocumentId")
    ImageSourcePathDTO imageSourcePathToImageSourcePathDTO(ImageSourcePath imageSourcePath);

    List<ImageSourcePathDTO> imageSourcePathsToImageSourcePathDTOs(List<ImageSourcePath> imageSourcePaths);

    @Mapping(source = "imageId", target = "image")
    @Mapping(source = "imDocumentId", target = "imDocument")
    ImageSourcePath imageSourcePathDTOToImageSourcePath(ImageSourcePathDTO imageSourcePathDTO);

    List<ImageSourcePath> imageSourcePathDTOsToImageSourcePaths(List<ImageSourcePathDTO> imageSourcePathDTOs);

    default Image imageFromId(Long id) {
        if (id == null) {
            return null;
        }
        Image image = new Image();
        image.setId(id);
        return image;
    }

    default ImDocument imDocumentFromId(Long id) {
        if (id == null) {
            return null;
        }
        ImDocument imDocument = new ImDocument();
        imDocument.setId(id);
        return imDocument;
    }
}
