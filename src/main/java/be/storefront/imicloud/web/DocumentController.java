package be.storefront.imicloud.web;


import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.domain.document.ImDocumentStructure;
import be.storefront.imicloud.repository.ImDocumentRepository;
import be.storefront.imicloud.repository.ImageSourcePathRepository;
import be.storefront.imicloud.security.DocumentPasswordEncoder;
import be.storefront.imicloud.security.ImCloudSecurity;
import be.storefront.imicloud.security.MyUserDetails;
import be.storefront.imicloud.security.SecurityUtils;
import be.storefront.imicloud.service.BrandingService;
import be.storefront.imicloud.service.ImDocumentService;
import be.storefront.imicloud.service.ImageService;
import be.storefront.imicloud.service.UrlHelperService;
import be.storefront.imicloud.service.dto.BrandingDTO;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import be.storefront.imicloud.service.mapper.ImDocumentMapper;
import be.storefront.imicloud.web.exception.AccessDeniedException;
import be.storefront.imicloud.web.exception.NotFoundException;
import be.storefront.imicloud.web.exception.UploadIncompleteException;
import be.storefront.imicloud.web.helper.CssHelper;
import be.storefront.imicloud.web.session.DocumentAccess;
import be.storefront.imicloud.web.template.HtmlContentProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
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

    @Inject
    private DocumentAccess documentAccess;

    @Inject private UrlHelperService urlHelperService;

    @Inject private ImCloudProperties imCloudProperties;

    @Inject private BrandingService brandingService;

    private PasswordEncoder documentPasswordEncoder = new DocumentPasswordEncoder();

    @Inject private ImageSourcePathRepository imageSourcePathRepository;

    private int staticVersion;


    public DocumentController(){
        this.staticVersion = (int) Math.ceil(Math.random() * 1000000);
    }

    @GetMapping("/document/{documentId}/{secret}")
    public ModelAndView view(@PathVariable(value = "documentId") Long documentId, @PathVariable("secret") String secret) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        return processView(documentId, secret, null);
    }

    @GetMapping("/document/{documentId}/{secret}/{template}")
    public ModelAndView viewWithTemplate(@PathVariable(value = "documentId") Long documentId, @PathVariable("secret") String secret, @PathVariable("template") String template) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        return processView(documentId, secret, template);
    }

    @PostMapping("/document/password/")
    public String receiveDocumentPasswordAndViewWithTemplate(@RequestParam(required = false) String password, @RequestParam(value = "documentId") Long documentId, @RequestParam("templateCode") String templateCode) {

        // Accept POSTed password
        if(password != null && password.length() > 0){
            documentAccess.rememberDocumentPassword(documentId, password);
        }

        ImDocument imDocument = imDocumentRepository.findOne(documentId);

        return "redirect:"+urlHelperService.getDocumentPublicUrl(imDocument, templateCode);
    }

    protected ModelAndView processView(Long documentId, String secret, String templateCode) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        ImDocumentDTO imDocumentDto = imDocumentService.findOne(documentId);
        if (imDocumentDto != null) {
            if (secret != null && secret.equals(imDocumentDto.getSecret())) {

                if (imDocumentDto.getUploadComplete() != null && imDocumentDto.getUploadComplete()) {
                    ImDocument imDocument = imDocumentRepository.getOne(documentId);

                    // Check document password
                    boolean accessGranted = true;
                    if (imDocument.getPassword() != null && imDocument.getPassword().length() > 0) {
                        // Document is password protected

                        MyUserDetails currentUser = SecurityUtils.getCurrentUser();
                        if(currentUser != null && imDocument.getUser().getId().equals(currentUser.getId())){
                            // I am the uploader of the document

                        }else {
                            // Someone else wants to see the document
                            String rememberedPass = documentAccess.getRememberedDocumentPassword(imDocument.getId());

                            if (documentPasswordEncoder.matches(rememberedPass, imDocument.getPassword())) {
                                // Access allowed
                            } else {
                                accessGranted = false;
                            }
                        }

                    }

                    String template;
                    if (templateCode != null) {
                        template = templateCode;
                    } else {
                        template = imDocumentDto.getDefaultTemplate();
                    }

                    BrandingDTO branding = brandingService.findByDocument(imDocument);
                    branding.setUrlHelperService(urlHelperService);

                    HashMap<String, Object> viewMap = new HashMap<>();
                    viewMap.put("baseUrl", imCloudProperties.getBaseUrl());
                    viewMap.put("allDocumentsUrl", urlHelperService.getAllDocumentsUrl());
                    viewMap.put("branding", branding);
                    viewMap.put("cssHelper", new CssHelper());
                    viewMap.put("urlHelperService", urlHelperService);
                    viewMap.put("templateCode", template);
                    viewMap.put("staticVersion", staticVersion);

                    if (accessGranted) {
                        viewMap.put("ImDocumentStructure", new ImDocumentStructure(imDocument,imageSourcePathRepository));
                        viewMap.put("ImDocumentDTO", imDocumentDto);
                        viewMap.put("ImDocument", imDocument);
                        viewMap.put("HtmlContentProcessor", htmlContentProcessor);

                        return new ModelAndView(template + "/index", viewMap);

                    } else {
                        // Access denied due to wrong password
                        viewMap.put("passwordSubmitUrl", urlHelperService.getDocumentPasswordSubmitUrl());
                        viewMap.put("documentId", imDocument.getId());

                        return new ModelAndView("password_form", viewMap);
                    }

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


