package be.storefront.imicloud.repository.search;

import be.storefront.imicloud.domain.ImBlock;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ImBlock entity.
 */
public interface ImBlockSearchRepository extends ElasticsearchRepository<ImBlock, Long> {
}
