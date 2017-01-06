package be.storefront.imicloud.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Branding entity.
 */
public class BrandingDTO extends AbstractAuditingDTO implements Serializable {

    private Long id;

    @Size(max = 7)
    private String primaryColor;

    @Size(max = 7)
    private String secundaryColor;

    @Size(max = 7)
    private String pageBackgroundColor;

    @Size(max = 7)
    private String textColor;


    private Long logoImageId;
    

    private String logoImageFilename;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }
    public String getSecundaryColor() {
        return secundaryColor;
    }

    public void setSecundaryColor(String secundaryColor) {
        this.secundaryColor = secundaryColor;
    }
    public String getPageBackgroundColor() {
        return pageBackgroundColor;
    }

    public void setPageBackgroundColor(String pageBackgroundColor) {
        this.pageBackgroundColor = pageBackgroundColor;
    }
    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public Long getLogoImageId() {
        return logoImageId;
    }

    public void setLogoImageId(Long imageId) {
        this.logoImageId = imageId;
    }


    public String getLogoImageFilename() {
        return logoImageFilename;
    }

    public void setLogoImageFilename(String imageFilename) {
        this.logoImageFilename = imageFilename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BrandingDTO brandingDTO = (BrandingDTO) o;

        if ( ! Objects.equals(id, brandingDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BrandingDTO{" +
            "id=" + id +
            ", primaryColor='" + primaryColor + "'" +
            ", secundaryColor='" + secundaryColor + "'" +
            ", pageBackgroundColor='" + pageBackgroundColor + "'" +
            ", textColor='" + textColor + "'" +
            '}';
    }
}
