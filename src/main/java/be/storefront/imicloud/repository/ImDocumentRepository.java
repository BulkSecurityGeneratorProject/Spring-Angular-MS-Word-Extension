package be.storefront.imicloud.repository;

import be.storefront.imicloud.domain.EntityAuditEvent;
import be.storefront.imicloud.domain.ImDocument;

import be.storefront.imicloud.service.dto.ImDocumentDTO;
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


//    @Query("select imDocument from ImDocument imDocument where imDocument.user_id = :user_id")
//    List<ImDocument> findByUserId(@Param("user_id") Long userId, Pageable pageable);
//

    Page<ImDocument> findAllByUserId(Long userId, Pageable pageRequest);

}
