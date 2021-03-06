package be.storefront.imicloud.security;

import be.storefront.imicloud.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user
     */
    public static String getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = null;
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                userName = springSecurityUser.getUsername();
            } else if (authentication.getPrincipal() instanceof String) {
                userName = (String) authentication.getPrincipal();
            }
        }
        return userName;
    }

    public static MyUserDetails getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof MyUserDetails) {
                MyUserDetails currentUser = (MyUserDetails) principal;
                return currentUser;
            }
        }
        return null;
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(AuthoritiesConstants.ANONYMOUS));
        }
        return false;
    }

    /**
     * If the current user has a specific authority (security role).
     * <p>
     * <p>The name of this method comes from the isUserInRole() method in the Servlet API</p>
     *
     * @param authority the authority to check
     * @return true if the current user has the authority, false otherwise
     */
    public static boolean isCurrentUserInRole(String authority) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
        }
        return false;
    }

    public static MyUserDetails createUserDetailsFromDBUser(User userFromDatabase, String password, List<GrantedAuthority> grantedAuthorities) {
        String lowercaseLogin = userFromDatabase.getEmail().toLowerCase();

        MyUserDetails myUserDetails = new MyUserDetails(userFromDatabase.getId(), lowercaseLogin, password, grantedAuthorities);
        return myUserDetails;
    }

    public static String generateSecret() {
        SecureRandom random = new SecureRandom();
        String r = new BigInteger(130, random).toString(32);
        return r;
    }
}
