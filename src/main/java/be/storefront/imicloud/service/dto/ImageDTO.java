package be.storefront.imicloud.service.dto;

import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Image entity.
 */
public class ImageDTO extends AbstractAuditingDTO implements Serializable {

    private Long id;

    @NotNull
    private String filename;

    private ZonedDateTime createdAt;


    private Set<ImBlockDTO> imBlocks = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<ImBlockDTO> getImBlocks() {
        return imBlocks;
    }

    public void setImBlocks(Set<ImBlockDTO> imBlocks) {
        this.imBlocks = imBlocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ImageDTO imageDTO = (ImageDTO) o;

        if ( ! Objects.equals(id, imageDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImageDTO{" +
            "id=" + id +
            ", filename='" + filename + "'" +
            ", createdAt='" + createdAt + "'" +
            '}';
    }
}
