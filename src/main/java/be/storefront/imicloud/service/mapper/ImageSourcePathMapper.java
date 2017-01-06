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

    ImageSourcePathDTO imageSourcePathToImageSourcePathDTO(ImageSourcePath imageSourcePath);

    List<ImageSourcePathDTO> imageSourcePathsToImageSourcePathDTOs(List<ImageSourcePath> imageSourcePaths);

    ImageSourcePath imageSourcePathDTOToImageSourcePath(ImageSourcePathDTO imageSourcePathDTO);

    List<ImageSourcePath> imageSourcePathDTOsToImageSourcePaths(List<ImageSourcePathDTO> imageSourcePathDTOs);
}
