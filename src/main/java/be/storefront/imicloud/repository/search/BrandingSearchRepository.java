package be.storefront.imicloud.repository.search;

import be.storefront.imicloud.domain.Branding;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Branding entity.
 */
public interface BrandingSearchRepository extends ElasticsearchRepository<Branding, Long> {
}
