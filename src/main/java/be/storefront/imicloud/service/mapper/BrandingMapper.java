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

    BrandingDTO brandingToBrandingDTO(Branding branding);

    List<BrandingDTO> brandingsToBrandingDTOs(List<Branding> brandings);

    Branding brandingDTOToBranding(BrandingDTO brandingDTO);

    List<Branding> brandingDTOsToBrandings(List<BrandingDTO> brandingDTOs);
}
