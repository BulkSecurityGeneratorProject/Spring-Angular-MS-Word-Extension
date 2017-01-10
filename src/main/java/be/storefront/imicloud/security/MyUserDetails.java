package be.storefront.imicloud.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Created by wouter on 19/12/2016.
 */
public class MyUserDetails extends User{

    private static final long serialVersionUID = 1L;

    private Long id;

    public MyUserDetails(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);

        this.id = userId;
    }

    public Long getId() {
        return id;
    }
}
