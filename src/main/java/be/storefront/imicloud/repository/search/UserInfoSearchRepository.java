package be.storefront.imicloud.repository.search;

import be.storefront.imicloud.domain.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the UserInfo entity.
 */
public interface UserInfoSearchRepository extends ElasticsearchRepository<UserInfo, Long> {
}
