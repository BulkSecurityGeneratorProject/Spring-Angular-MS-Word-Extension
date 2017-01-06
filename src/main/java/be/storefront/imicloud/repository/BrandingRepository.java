package be.storefront.imicloud.repository;

import be.storefront.imicloud.domain.Branding;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Branding entity.
 */
@SuppressWarnings("unused")
public interface BrandingRepository extends JpaRepository<Branding,Long> {

}
