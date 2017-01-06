package be.storefront.imicloud.repository;

import be.storefront.imicloud.domain.Organization;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Organization entity.
 */
@SuppressWarnings("unused")
public interface OrganizationRepository extends JpaRepository<Organization,Long> {

    @Query("select organization from Organization organization where organization.user.login = ?#{principal.username}")
    List<Organization> findByUserIsCurrentUser();

    @Query("select organization from Organization organization where organization.user.id = :userId")
    List<Organization> findByUserId(@Param("userId") Long userId);

}
