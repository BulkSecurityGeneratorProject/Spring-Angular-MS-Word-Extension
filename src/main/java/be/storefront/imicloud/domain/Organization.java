package be.storefront.imicloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Organization.
 */
@Entity
@Table(name = "organization")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "organization")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "magento_customer_id")
    private Integer magentoCustomerId;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @OneToMany(mappedBy = "organization")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Folder> folders = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Organization name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMagentoCustomerId() {
        return magentoCustomerId;
    }

    public Organization magentoCustomerId(Integer magentoCustomerId) {
        this.magentoCustomerId = magentoCustomerId;
        return this;
    }

    public void setMagentoCustomerId(Integer magentoCustomerId) {
        this.magentoCustomerId = magentoCustomerId;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public Organization createdAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Folder> getFolders() {
        return folders;
    }

    public Organization folders(Set<Folder> folders) {
        this.folders = folders;
        return this;
    }

    public Organization addFolder(Folder folder) {
        folders.add(folder);
        folder.setOrganization(this);
        return this;
    }

    public Organization removeFolder(Folder folder) {
        folders.remove(folder);
        folder.setOrganization(null);
        return this;
    }

    public void setFolders(Set<Folder> folders) {
        this.folders = folders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Organization organization = (Organization) o;
        if (organization.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, organization.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Organization{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", magentoCustomerId='" + magentoCustomerId + "'" +
            ", createdAt='" + createdAt + "'" +
            '}';
    }
}
