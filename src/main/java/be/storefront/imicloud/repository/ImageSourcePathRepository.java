package be.storefront.imicloud.repository;

import be.storefront.imicloud.domain.ImageSourcePath;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the ImageSourcePath entity.
 */
@SuppressWarnings("unused")
public interface ImageSourcePathRepository extends JpaRepository<ImageSourcePath,Long> {

    @Query("select imageSourcePath from ImageSourcePath imageSourcePath where imageSourcePath.imDocument.id = :documentId and imageSourcePath.source = :source")
    List<ImageSourcePath> findByDocumentIdAndSource(@Param("documentId") Long documentId, @Param("source") String source);


    @Query("select imageSourcePath from ImageSourcePath imageSourcePath where imageSourcePath.imDocument.id = :documentId and imageSourcePath.source = :source and imageSourcePath.uploadComplete = 1")
    ImageSourcePath findByDocumentIdAndSourceAndUploadComplete(@Param("documentId") Long documentId, @Param("source") String source);


    @Query("select imageSourcePath from ImageSourcePath imageSourcePath where imageSourcePath.imDocument.id = :documentId")
    List<ImageSourcePath> findByDocumentId(@Param("documentId") Long id);

    @Query("select imageSourcePath from ImageSourcePath imageSourcePath where imageSourcePath.imDocument.id = :documentId and imageSourcePath.uploadComplete = 1")
    List<ImageSourcePath> findByDocumentIdAndUploadComplete(@Param("documentId") Long id);
}
