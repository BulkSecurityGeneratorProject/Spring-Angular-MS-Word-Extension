package be.storefront.imicloud.service.mapper;

import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.service.dto.UserInfoDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity UserInfo and its DTO UserInfoDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, })
public interface UserInfoMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "organization.id", target = "organizationId")
    @Mapping(source = "organization.name", target = "organizationName")
    UserInfoDTO userInfoToUserInfoDTO(UserInfo userInfo);

    List<UserInfoDTO> userInfosToUserInfoDTOs(List<UserInfo> userInfos);

    @Mapping(source = "userId", target = "user")
    @Mapping(source = "organizationId", target = "organization")
    UserInfo userInfoDTOToUserInfo(UserInfoDTO userInfoDTO);

    List<UserInfo> userInfoDTOsToUserInfos(List<UserInfoDTO> userInfoDTOs);

    default Organization organizationFromId(Long id) {
        if (id == null) {
            return null;
        }
        Organization organization = new Organization();
        organization.setId(id);
        return organization;
    }
}
