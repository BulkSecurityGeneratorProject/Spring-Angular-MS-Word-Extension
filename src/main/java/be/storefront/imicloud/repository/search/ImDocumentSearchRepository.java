package be.storefront.imicloud.repository.search;

import be.storefront.imicloud.domain.ImDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ImDocument entity.
 */
public interface ImDocumentSearchRepository extends ElasticsearchRepository<ImDocument, Long> {
}
