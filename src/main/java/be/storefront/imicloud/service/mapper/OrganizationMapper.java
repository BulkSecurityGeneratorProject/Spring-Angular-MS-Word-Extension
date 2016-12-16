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

    OrganizationDTO organizationToOrganizationDTO(Organization organization);

    List<OrganizationDTO> organizationsToOrganizationDTOs(List<Organization> organizations);

    @Mapping(target = "folders", ignore = true)
    Organization organizationDTOToOrganization(OrganizationDTO organizationDTO);

    List<Organization> organizationDTOsToOrganizations(List<OrganizationDTO> organizationDTOs);
}
