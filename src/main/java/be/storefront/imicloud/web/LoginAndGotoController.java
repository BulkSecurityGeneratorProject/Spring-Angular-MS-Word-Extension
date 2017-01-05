package be.storefront.imicloud.web;

import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.domain.User;
import be.storefront.imicloud.repository.ImDocumentRepository;
import be.storefront.imicloud.security.ImCloudSecurity;
import be.storefront.imicloud.service.ImDocumentService;
import be.storefront.imicloud.web.exception.AccessDeniedException;
import com.codahale.metrics.annotation.Timed;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;

/**
 * Created by wouter on 20/12/2016.
 */

@Controller
public class LoginAndGotoController {

    @Inject
    private ImCloudSecurity imCloudSecurity;

    @Inject
    private ImDocumentService imDocumentService;

    @Inject
    private ImDocumentRepository imDocumentRepository;

    @Inject
    private UserDetailsService userDetailsService;

    @GetMapping("/loginAndRedirect/")
    @Timed
    public ModelAndView loginAndRedirect(@RequestParam("access_token") String accessToken) {
        loginUser(accessToken);

        return new ModelAndView("redirect:/");
    }


    @GetMapping("/loginAndRedirect/document/{documentId}")
    @Timed
    public ModelAndView loginAndRedirectToDocument(@PathVariable(value = "documentId") Long documentId, @RequestParam("access_token") String accessToken) {
        loginUser(accessToken);

        ImDocument imDocument = imDocumentRepository.findOne(documentId);

        return new ModelAndView("redirect:/document/" + documentId + "/" + imDocument.getSecret());
    }

    protected void loginUser(String accessToken) {
        User uploadingUser = imCloudSecurity.getUserByFsProAccessToken(accessToken);

        if (uploadingUser != null) {
            // Auto login
            UserDetails userDetails = userDetailsService.loadUserByUsername(uploadingUser.getLogin());
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } else {
            throw new AccessDeniedException();
        }
    }


}


