package be.storefront.imicloud.repository.search;

import be.storefront.imicloud.domain.Folder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Folder entity.
 */
public interface FolderSearchRepository extends ElasticsearchRepository<Folder, Long> {
}
