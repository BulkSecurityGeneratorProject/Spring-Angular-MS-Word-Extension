package be.storefront.imicloud.service.mapper;

import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.service.dto.ImDocumentDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity ImDocument and its DTO ImDocumentDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, })
public interface ImDocumentMapper {

    @Mapping(source = "folder.id", target = "folderId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    ImDocumentDTO imDocumentToImDocumentDTO(ImDocument imDocument);

    List<ImDocumentDTO> imDocumentsToImDocumentDTOs(List<ImDocument> imDocuments);

    @Mapping(source = "folderId", target = "folder")
    @Mapping(target = "maps", ignore = true)
    @Mapping(source = "userId", target = "user")
    ImDocument imDocumentDTOToImDocument(ImDocumentDTO imDocumentDTO);

    List<ImDocument> imDocumentDTOsToImDocuments(List<ImDocumentDTO> imDocumentDTOs);

    default Folder folderFromId(Long id) {
        if (id == null) {
            return null;
        }
        Folder folder = new Folder();
        folder.setId(id);
        return folder;
    }
}
