package be.storefront.imicloud.repository;

import be.storefront.imicloud.domain.ImMap;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ImMap entity.
 */
@SuppressWarnings("unused")
public interface ImMapRepository extends JpaRepository<ImMap,Long> {

}
