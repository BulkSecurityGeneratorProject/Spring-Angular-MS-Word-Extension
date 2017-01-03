package be.storefront.imicloud.service.dto;

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

    @NotNull
    private String originalName;


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
    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
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
            ", originalName='" + originalName + "'" +
            '}';
    }
}
