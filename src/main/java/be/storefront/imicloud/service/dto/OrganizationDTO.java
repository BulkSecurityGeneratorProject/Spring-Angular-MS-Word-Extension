package be.storefront.imicloud.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Organization entity.
 */
public class OrganizationDTO extends AbstractAuditingDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private Integer magentoCustomerId;


    private Long brandingId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Integer getMagentoCustomerId() {
        return magentoCustomerId;
    }

    public void setMagentoCustomerId(Integer magentoCustomerId) {
        this.magentoCustomerId = magentoCustomerId;
    }

    public Long getBrandingId() {
        return brandingId;
    }

    public void setBrandingId(Long brandingId) {
        this.brandingId = brandingId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrganizationDTO organizationDTO = (OrganizationDTO) o;

        if ( ! Objects.equals(id, organizationDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "OrganizationDTO{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", magentoCustomerId='" + magentoCustomerId + "'" +
            '}';
    }
}
