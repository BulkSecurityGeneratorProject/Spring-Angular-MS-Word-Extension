package be.storefront.imicloud.web;

import be.storefront.imicloud.domain.User;
import be.storefront.imicloud.security.ImCloudSecurity;
import be.storefront.imicloud.security.SecurityUtils;
import be.storefront.imicloud.service.ImDocumentService;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import com.codahale.metrics.annotation.Timed;
import org.springframework.security.core.Authentication;
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

    @Inject private ImDocumentService imDocumentService;

    @GetMapping("/loginAndRedirect/document/{documentId}")
    @Timed
    public ModelAndView loginAndRedirect(@PathVariable(value="documentId") Long documentId, @RequestParam("access_token") String accessToken) {

        // TODO login the user
        User uploadingUser = imCloudSecurity.getUserByFsProAccessToken(accessToken);




        if(uploadingUser != null) {
            // Auto login

            Authentication auth =
                SecurityUtils.createUserDetailsFromDBUser(uploadingUser, null);

            SecurityContextHolder.getContext().setAuthentication(auth);


        }else{
            // Cannot log in, but continue anyway...
        }

        return new ModelAndView("redirect:/document/" + documentId);
    }

}


