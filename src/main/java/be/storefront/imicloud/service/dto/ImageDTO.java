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
    private String contentType;

    @NotNull
    private Integer imageWidth;

    @NotNull
    private Integer imageHeight;

    @NotNull
    private Long contentLength;

    private String originalSource;


    private Set<ImBlockDTO> imBlocks = new HashSet<>();

    private Long uploadedByUserId;
    

    private String uploadedByUserEmail;

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
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }
    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }
    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }
    public String getOriginalSource() {
        return originalSource;
    }

    public void setOriginalSource(String originalSource) {
        this.originalSource = originalSource;
    }

    public Set<ImBlockDTO> getImBlocks() {
        return imBlocks;
    }

    public void setImBlocks(Set<ImBlockDTO> imBlocks) {
        this.imBlocks = imBlocks;
    }

    public Long getUploadedByUserId() {
        return uploadedByUserId;
    }

    public void setUploadedByUserId(Long userId) {
        this.uploadedByUserId = userId;
    }


    public String getUploadedByUserEmail() {
        return uploadedByUserEmail;
    }

    public void setUploadedByUserEmail(String userEmail) {
        this.uploadedByUserEmail = userEmail;
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
            ", contentType='" + contentType + "'" +
            ", imageWidth='" + imageWidth + "'" +
            ", imageHeight='" + imageHeight + "'" +
            ", contentLength='" + contentLength + "'" +
            ", originalSource='" + originalSource + "'" +
            '}';
    }
}
