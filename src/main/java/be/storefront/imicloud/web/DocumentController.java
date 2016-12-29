package be.storefront.imicloud.web;


import be.storefront.imicloud.security.ImCloudSecurity;
import be.storefront.imicloud.service.ImDocumentService;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import be.storefront.imicloud.web.exception.AccessDeniedException;
import be.storefront.imicloud.web.exception.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by wouter on 20/12/2016.
 */

@Controller
public class DocumentController {

    @Inject
    private ImCloudSecurity imCloudSecurity;

    @Inject
    private ImDocumentService imDocumentService;

    @GetMapping("/document/{documentId}/{secret}/{template}")
    public ModelAndView view(@PathVariable(value = "documentId") Long documentId, @PathVariable("secret") String secret, @PathVariable("template") Optional<String> optionalTemplate) {


        ImDocumentDTO imDocumentDto = imDocumentService.findOne(documentId);
        if (imDocumentDto != null) {
            if (secret != null && secret.equals(imDocumentDto.getSecret())) {

String template;
               if(!optionalTemplate.isPresent()){
template = optionalTemplate.get();
               }else{
                   template = imDocumentDto.get
               }
                return new ModelAndView("redirect:/document/" + documentId);

            } else {
                throw new AccessDeniedException();
            }

        } else {
            throw new NotFoundException();
        }



    }

}


