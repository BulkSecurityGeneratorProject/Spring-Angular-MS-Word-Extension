package be.storefront.imicloud.repository.search;

import be.storefront.imicloud.domain.ImageSourcePath;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ImageSourcePath entity.
 */
public interface ImageSourcePathSearchRepository extends ElasticsearchRepository<ImageSourcePath, Long> {
}
