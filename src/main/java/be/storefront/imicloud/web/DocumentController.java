package be.storefront.imicloud.web;


import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.repository.ImDocumentRepository;
import be.storefront.imicloud.security.ImCloudSecurity;
import be.storefront.imicloud.service.ImDocumentService;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import be.storefront.imicloud.service.mapper.ImDocumentMapper;
import be.storefront.imicloud.web.exception.AccessDeniedException;
import be.storefront.imicloud.web.exception.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import java.util.HashMap;
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

    @Inject
    private ImDocumentRepository imDocumentRepository;

    @Inject
    private ImDocumentMapper imDocumentMapper;

    @GetMapping("/document/{documentId}/{secret}/{template}")
    public ModelAndView view(@PathVariable(value = "documentId") Long documentId, @PathVariable("secret") String secret, @PathVariable("template") Optional<String> optionalTemplate) {




        ImDocumentDTO imDocumentDto = imDocumentService.findOne(documentId);
        if (imDocumentDto != null) {
            if (secret != null && secret.equals(imDocumentDto.getSecret())) {

                ImDocument imDocument = imDocumentRepository.getOne(documentId);

                String template;
                if (optionalTemplate.isPresent()) {
                    template = optionalTemplate.get();
                } else {
                    template = imDocumentDto.getDefaultTemplate();
                }

                imDocument.getMaps();

                HashMap<String, Object> viewMap = new HashMap<>();
                viewMap.put("ImDocumentDTO", imDocumentDto);
                viewMap.put("ImDocument", imDocument);

                return new ModelAndView(template + "/index", viewMap);

            } else {
                throw new AccessDeniedException();
            }

        } else {
            throw new NotFoundException();
        }


    }

}


