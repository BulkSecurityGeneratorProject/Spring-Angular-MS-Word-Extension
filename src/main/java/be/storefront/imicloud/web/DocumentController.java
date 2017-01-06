package be.storefront.imicloud.web;


import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.repository.ImDocumentRepository;
import be.storefront.imicloud.security.ImCloudSecurity;
import be.storefront.imicloud.service.ImDocumentService;
import be.storefront.imicloud.service.ImageService;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import be.storefront.imicloud.service.mapper.ImDocumentMapper;
import be.storefront.imicloud.web.exception.AccessDeniedException;
import be.storefront.imicloud.web.exception.NotFoundException;
import be.storefront.imicloud.web.exception.UploadIncompleteException;
import be.storefront.imicloud.web.template.HtmlContentProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import java.util.HashMap;

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

    @Inject
    private HtmlContentProcessor htmlContentProcessor;

    @Inject
    private ImageService imageService;

    @GetMapping("/document/{documentId}/{secret}")
    public ModelAndView view(@PathVariable(value = "documentId") Long documentId, @PathVariable("secret") String secret) {
        return processView(documentId, secret, null);
    }

    @GetMapping("/document/{documentId}/{secret}/{template}")
    public ModelAndView viewWithTemplate(@PathVariable(value = "documentId") Long documentId, @PathVariable("secret") String secret, @PathVariable("template") String template) {
        return processView(documentId, secret, template);
    }

    protected ModelAndView processView(Long documentId, String secret, String templateCode) {
        ImDocumentDTO imDocumentDto = imDocumentService.findOne(documentId);
        if (imDocumentDto != null) {
            if (secret != null && secret.equals(imDocumentDto.getSecret())) {

                if (imDocumentDto.getUploadComplete() != null && imDocumentDto.getUploadComplete()) {
                    ImDocument imDocument = imDocumentRepository.getOne(documentId);

                    String template;
                    if (templateCode != null) {
                        template = templateCode;
                    } else {
                        template = imDocumentDto.getDefaultTemplate();
                    }

                    HashMap<String, Object> viewMap = new HashMap<>();
                    viewMap.put("ImDocumentDTO", imDocumentDto);
                    viewMap.put("ImDocument", imDocument);
                    viewMap.put("HtmlContentProcessor", htmlContentProcessor);

                    return new ModelAndView(template + "/index", viewMap);

                } else {
                    // Upload was not completed - remove it
                    //imDocumentRepository.delete(documentId);

                    return error("Document not found.", 404);
                }

            } else {
                return error("Access denied.", 403);
            }

        } else {
            return error("Document not found.", 404);
        }
    }

    private ModelAndView error(String message, int status) {
        HashMap<String, Object> viewMap = new HashMap<>();
        viewMap.put("message", message);
        viewMap.put("status", status);

        ModelAndView errorModelAndView = new ModelAndView("error", viewMap);
        errorModelAndView.setStatus(HttpStatus.valueOf(status));

        return errorModelAndView;
    }
}


