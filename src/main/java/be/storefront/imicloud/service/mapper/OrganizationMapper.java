package be.storefront.imicloud.service.mapper;

import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.service.dto.OrganizationDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Organization and its DTO OrganizationDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface OrganizationMapper {

    @Mapping(source = "branding.id", target = "brandingId")
    OrganizationDTO organizationToOrganizationDTO(Organization organization);

    List<OrganizationDTO> organizationsToOrganizationDTOs(List<Organization> organizations);

    @Mapping(target = "folders", ignore = true)
    @Mapping(source = "brandingId", target = "branding")
    Organization organizationDTOToOrganization(OrganizationDTO organizationDTO);

    List<Organization> organizationDTOsToOrganizations(List<OrganizationDTO> organizationDTOs);

    default Branding brandingFromId(Long id) {
        if (id == null) {
            return null;
        }
        Branding branding = new Branding();
        branding.setId(id);
        return branding;
    }
}
