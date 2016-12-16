package be.storefront.imicloud.repository;

import be.storefront.imicloud.domain.Folder;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Folder entity.
 */
@SuppressWarnings("unused")
public interface FolderRepository extends JpaRepository<Folder,Long> {

}
