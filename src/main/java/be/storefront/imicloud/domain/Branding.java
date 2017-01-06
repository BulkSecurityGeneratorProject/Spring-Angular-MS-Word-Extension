package be.storefront.imicloud.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Branding.
 */
@Entity
@Table(name = "branding")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "branding")
public class Branding extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 6)
    @Column(name = "primary_color", length = 6)
    private String primaryColor;

    @Size(max = 6)
    @Column(name = "secundary_color", length = 6)
    private String secundaryColor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public Branding primaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecundaryColor() {
        return secundaryColor;
    }

    public Branding secundaryColor(String secundaryColor) {
        this.secundaryColor = secundaryColor;
        return this;
    }

    public void setSecundaryColor(String secundaryColor) {
        this.secundaryColor = secundaryColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Branding branding = (Branding) o;
        if (branding.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, branding.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Branding{" +
            "id=" + id +
            ", primaryColor='" + primaryColor + "'" +
            ", secundaryColor='" + secundaryColor + "'" +
            '}';
    }
}
