package be.storefront.imicloud.security;

import be.storefront.imicloud.domain.User;
import be.storefront.imicloud.service.MagentoCustomerService;
import be.storefront.imicloud.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.Serializable;

@Component("magentoAuthenticationProvider")
public class MagentoAuthenticationProvider implements AuthenticationProvider, Serializable{

    @Inject
    private UserService userService;


    @Inject
    private MagentoCustomerService magentoCustomerService;

    private final Logger log = LoggerFactory.getLogger(MagentoAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

//        List<String> authoritiesList = Arrays.asList(StringUtils.split("ROLE_USER", ","));
//        List<GrantedAuthority> authorities = authoritiesMapper.mapAuthorities(GrantedAuthority);

//            String firstName = "Wouter";
//            String lastName = "Samaey";
//            boolean isActive = true;
//            String langCode = "NL";
//
//            ArrayList<GrantedAuthority> authorities = new ArrayList<>();
//            authorities.add(new GrantedAuthority() {
//                @Override
//                public String getAuthority() {
//                    return AuthoritiesConstants.USER;
//                }
//            });
//
//            MagentoAuthenticationToken result = new MagentoAuthenticationToken(email, password, firstName, lastName, isActive, langCode, authorities);
//            result.setDetails(authentication.getDetails());
//
//            return result;

        String email = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        // Try to authenticate the user in Magento
        try {
            User user = magentoCustomerService.getUserByMagentoLogin(email, password);

        } catch (Exception e) {
            log.error("Could not get customer data from Magento", e);
        }


        // Never authenticate. The next mechanism will pick up the user we have created (if any).
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}



