package be.storefront.imicloud.service.dto;


/**
 * Created by wouter on 10/01/2017.
 */
public class FullAccountDTO extends UserDTO{

    private OrganizationDTO organizationDTO;
    private BrandingDTO brandingDTO;

    public FullAccountDTO(UserDTO userDTO, OrganizationDTO organizationDTO, BrandingDTO brandingDTO){

        super(userDTO.getId(), userDTO.getLogin(), userDTO.getFirstName(), userDTO.getLastName(),
            userDTO.getEmail(), userDTO.isActivated(), userDTO.getLangKey(), userDTO.getAuthorities());

        this.organizationDTO = organizationDTO;
        this.brandingDTO = brandingDTO;
    }

    public OrganizationDTO getOrganization(){
        return this.organizationDTO;
    }

    public BrandingDTO getBranding(){
        return this.brandingDTO;
    }

}
