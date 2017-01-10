package be.storefront.imicloud.repository;

import be.storefront.imicloud.domain.ImDocument;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the ImDocument entity.
 */
@SuppressWarnings("unused")
public interface ImDocumentRepository extends JpaRepository<ImDocument,Long> {

    @Query("select imDocument from ImDocument imDocument where imDocument.user.login = ?#{principal.username}")
    List<ImDocument> findByUserIsCurrentUser();

    @Query("select imDocument from ImDocument imDocument where imDocument.user.id = :userId and document_name = :documentName")
    List<ImDocument> findByUserAndDocumentName(@Param("userId") Long userId, @Param("documentName") String documentName);

    @Query("select imDocument from ImDocument imDocument where imDocument.user.id = :userId")
    Page<ImDocument> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
