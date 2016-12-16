package be.storefront.imicloud.repository.search;

import be.storefront.imicloud.domain.ImMap;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ImMap entity.
 */
public interface ImMapSearchRepository extends ElasticsearchRepository<ImMap, Long> {
}
