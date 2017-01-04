package be.storefront.imicloud.repository;

import be.storefront.imicloud.domain.Image;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Image entity.
 */
@SuppressWarnings("unused")
public interface ImageRepository extends JpaRepository<Image,Long> {

    @Query("select image from Image image where image.uploadedByUser.login = ?#{principal.username}")
    List<Image> findByUploadedByUserIsCurrentUser();

    @Query("select distinct image from Image image left join fetch image.imBlocks")
    List<Image> findAllWithEagerRelationships();

    @Query("select image from Image image left join fetch image.imBlocks where image.id =:id")
    Image findOneWithEagerRelationships(@Param("id") Long id);

}
