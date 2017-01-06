package be.storefront.imicloud.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the ImageSourcePath entity.
 */
public class ImageSourcePathDTO extends AbstractAuditingDTO implements Serializable {

    private Long id;

    @NotNull
    private String source;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ImageSourcePathDTO imageSourcePathDTO = (ImageSourcePathDTO) o;

        if ( ! Objects.equals(id, imageSourcePathDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImageSourcePathDTO{" +
            "id=" + id +
            ", source='" + source + "'" +
            '}';
    }
}
