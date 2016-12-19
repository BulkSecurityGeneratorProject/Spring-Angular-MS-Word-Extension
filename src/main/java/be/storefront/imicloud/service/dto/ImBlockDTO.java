package be.storefront.imicloud.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import javax.persistence.Lob;


/**
 * A DTO for the ImBlock entity.
 */
public class ImBlockDTO extends AbstractAuditingDTO implements Serializable {

    private Long id;

    private String label;

    @Lob
    private String content;

    @NotNull
    private Float position;


    private Long imMapId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public Float getPosition() {
        return position;
    }

    public void setPosition(Float position) {
        this.position = position;
    }

    public Long getImMapId() {
        return imMapId;
    }

    public void setImMapId(Long imMapId) {
        this.imMapId = imMapId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ImBlockDTO imBlockDTO = (ImBlockDTO) o;

        if ( ! Objects.equals(id, imBlockDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImBlockDTO{" +
            "id=" + id +
            ", label='" + label + "'" +
            ", content='" + content + "'" +
            ", position='" + position + "'" +
            '}';
    }
}
