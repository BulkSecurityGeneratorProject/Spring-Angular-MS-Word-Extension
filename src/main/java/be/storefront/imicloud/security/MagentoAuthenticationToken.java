package be.storefront.imicloud.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by wouter on 19/12/2016.
 */
public class MagentoAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String firstName = null;
    private String lastName = null;
    private boolean isActive = false;
    private String langCode = null;

    public MagentoAuthenticationToken(String email, String password, String firstName, String lastName, boolean isActive, String langCode, Collection<GrantedAuthority> authorities) {
        super(email, password, authorities);

        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.langCode = langCode;
    }

    public String getEmail(){
        if(getPrincipal() == null){
            return null;
        }else {
            return getPrincipal().toString();
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getLangCode() {
        return langCode;
    }
}
