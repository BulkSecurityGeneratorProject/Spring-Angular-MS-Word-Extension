package be.storefront.imicloud.repository;

import be.storefront.imicloud.domain.Branding;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Branding entity.
 */
@SuppressWarnings("unused")
public interface BrandingRepository extends JpaRepository<Branding,Long> {

    @Query("select branding from Branding branding where branding.organization.id = :organizationId")
    List<Branding> findByOrganizationId(@Param("organizationId") Long id);

 
}
