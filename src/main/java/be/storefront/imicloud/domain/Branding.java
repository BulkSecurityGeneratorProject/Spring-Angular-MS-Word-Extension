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

    @Size(max = 7)
    @Column(name = "primary_color", length = 7)
    private String primaryColor;

    @Size(max = 7)
    @Column(name = "secundary_color", length = 7)
    private String secundaryColor;

    @Size(max = 7)
    @Column(name = "page_background_color", length = 7)
    private String pageBackgroundColor;

    @Size(max = 7)
    @Column(name = "text_color", length = 7)
    private String textColor;

    @Column(name = "name")
    private String name;

    @OneToOne
    @JoinColumn(unique = true)
    private Image logoImage;

    @ManyToOne
    @NotNull
    private Organization organization;

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

    public String getPageBackgroundColor() {
        return pageBackgroundColor;
    }

    public Branding pageBackgroundColor(String pageBackgroundColor) {
        this.pageBackgroundColor = pageBackgroundColor;
        return this;
    }

    public void setPageBackgroundColor(String pageBackgroundColor) {
        this.pageBackgroundColor = pageBackgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public Branding textColor(String textColor) {
        this.textColor = textColor;
        return this;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getName() {
        return name;
    }

    public Branding name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getLogoImage() {
        return logoImage;
    }

    public Branding logoImage(Image image) {
        this.logoImage = image;
        return this;
    }

    public void setLogoImage(Image image) {
        this.logoImage = image;
    }

    public Organization getOrganization() {
        return organization;
    }

    public Branding organization(Organization organization) {
        this.organization = organization;
        return this;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
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
            ", pageBackgroundColor='" + pageBackgroundColor + "'" +
            ", textColor='" + textColor + "'" +
            ", name='" + name + "'" +
            '}';
    }
}
