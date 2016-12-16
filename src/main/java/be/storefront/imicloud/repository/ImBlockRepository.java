package be.storefront.imicloud.repository;

import be.storefront.imicloud.domain.ImBlock;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ImBlock entity.
 */
@SuppressWarnings("unused")
public interface ImBlockRepository extends JpaRepository<ImBlock,Long> {

}
