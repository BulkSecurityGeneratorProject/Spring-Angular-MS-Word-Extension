package be.storefront.imicloud.repository;

import be.storefront.imicloud.domain.Organization;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Organization entity.
 */
@SuppressWarnings("unused")
public interface OrganizationRepository extends JpaRepository<Organization,Long> {

    @Query("select organization from Organization organization where organization.user.login = ?#{principal.username}")
    List<Organization> findByUserIsCurrentUser();

}
