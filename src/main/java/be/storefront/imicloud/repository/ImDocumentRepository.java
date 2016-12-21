package be.storefront.imicloud.repository;

import be.storefront.imicloud.domain.ImDocument;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ImDocument entity.
 */
@SuppressWarnings("unused")
public interface ImDocumentRepository extends JpaRepository<ImDocument,Long> {

    @Query("select imDocument from ImDocument imDocument where imDocument.user.login = ?#{principal.username}")
    List<ImDocument> findByUserIsCurrentUser();

}
