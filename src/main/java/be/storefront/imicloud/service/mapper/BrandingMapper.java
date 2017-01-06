package be.storefront.imicloud.service.mapper;

import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.service.dto.BrandingDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Branding and its DTO BrandingDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BrandingMapper {

    @Mapping(source = "logoImage.id", target = "logoImageId")
    @Mapping(source = "logoImage.filename", target = "logoImageFilename")
    BrandingDTO brandingToBrandingDTO(Branding branding);

    List<BrandingDTO> brandingsToBrandingDTOs(List<Branding> brandings);

    @Mapping(source = "logoImageId", target = "logoImage")
    Branding brandingDTOToBranding(BrandingDTO brandingDTO);

    List<Branding> brandingDTOsToBrandings(List<BrandingDTO> brandingDTOs);

    default Image imageFromId(Long id) {
        if (id == null) {
            return null;
        }
        Image image = new Image();
        image.setId(id);
        return image;
    }
}
