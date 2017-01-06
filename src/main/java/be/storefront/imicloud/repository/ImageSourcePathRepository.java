package be.storefront.imicloud.repository;

import be.storefront.imicloud.domain.ImageSourcePath;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ImageSourcePath entity.
 */
@SuppressWarnings("unused")
public interface ImageSourcePathRepository extends JpaRepository<ImageSourcePath,Long> {

}
