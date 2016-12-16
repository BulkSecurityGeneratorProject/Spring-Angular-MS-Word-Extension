package be.storefront.imicloud.service.mapper;

import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.service.dto.ImMapDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity ImMap and its DTO ImMapDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ImMapMapper {

    @Mapping(source = "imDocument.id", target = "imDocumentId")
    ImMapDTO imMapToImMapDTO(ImMap imMap);

    List<ImMapDTO> imMapsToImMapDTOs(List<ImMap> imMaps);

    @Mapping(source = "imDocumentId", target = "imDocument")
    @Mapping(target = "blocks", ignore = true)
    ImMap imMapDTOToImMap(ImMapDTO imMapDTO);

    List<ImMap> imMapDTOsToImMaps(List<ImMapDTO> imMapDTOs);

    default ImDocument imDocumentFromId(Long id) {
        if (id == null) {
            return null;
        }
        ImDocument imDocument = new ImDocument();
        imDocument.setId(id);
        return imDocument;
    }
}
