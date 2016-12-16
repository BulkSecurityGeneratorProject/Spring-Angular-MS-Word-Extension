package be.storefront.imicloud.service.mapper;

import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.service.dto.FolderDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Folder and its DTO FolderDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FolderMapper {

    @Mapping(source = "organization.id", target = "organizationId")
    FolderDTO folderToFolderDTO(Folder folder);

    List<FolderDTO> foldersToFolderDTOs(List<Folder> folders);

    @Mapping(source = "organizationId", target = "organization")
    @Mapping(target = "documents", ignore = true)
    Folder folderDTOToFolder(FolderDTO folderDTO);

    List<Folder> folderDTOsToFolders(List<FolderDTO> folderDTOs);

    default Organization organizationFromId(Long id) {
        if (id == null) {
            return null;
        }
        Organization organization = new Organization();
        organization.setId(id);
        return organization;
    }
}
