package be.storefront.imicloud.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the ImMap entity.
 */
public class ImMapDTO extends AbstractAuditingDTO implements Serializable {

    private Long id;

    private String guid;

    @NotNull
    private String label;

    @NotNull
    private Float position;


    private Long imDocumentId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    public Float getPosition() {
        return position;
    }

    public void setPosition(Float position) {
        this.position = position;
    }

    public Long getImDocumentId() {
        return imDocumentId;
    }

    public void setImDocumentId(Long imDocumentId) {
        this.imDocumentId = imDocumentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ImMapDTO imMapDTO = (ImMapDTO) o;

        if ( ! Objects.equals(id, imMapDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImMapDTO{" +
            "id=" + id +
            ", guid='" + guid + "'" +
            ", label='" + label + "'" +
            ", position='" + position + "'" +
            '}';
    }
}
