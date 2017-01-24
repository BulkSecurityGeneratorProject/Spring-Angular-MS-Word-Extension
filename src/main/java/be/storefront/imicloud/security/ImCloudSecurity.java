package be.storefront.imicloud.security;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.repository.BrandingRepository;
import be.storefront.imicloud.repository.ImDocumentRepository;
import be.storefront.imicloud.repository.OrganizationRepository;
import be.storefront.imicloud.repository.UserInfoRepository;
import be.storefront.imicloud.restclient.SimpleRestClient;
import be.storefront.imicloud.service.MagentoCustomerService;
import be.storefront.imicloud.service.UserService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class ImCloudSecurity {

    @Inject
    private ImCloudProperties imCloudProperties;

    @Inject
    private UserService userService;

    @Inject
    private MagentoCustomerService magentoCustomerService;

    @Inject private ImDocumentRepository imDocumentRepository;

    // This class handles security features connected to the Word plugin FS Pro.

    /**
     * Get the User that matches the submitted access_token.
     *
     * @param accessToken
     * @return
     */
    public User getUserByFsProAccessToken(String accessToken) {
        String targetURL = imCloudProperties.getSecurity().getFsProCloud().getApi().getUrl() + "sapi/Authentication";
        String body = "{\"Key\":\"" + accessToken + "\"}";

        String magentoCustomerId = null;

        try {
            JSONObject jsonObj = getHttpClient().postAndReadJson(targetURL, body);

            magentoCustomerId = jsonObj.getString("Id");

            if (magentoCustomerId != null && magentoCustomerId.length() > 0) {
                int magentoCustomerIdInteger = Integer.parseInt(magentoCustomerId);

                User r = magentoCustomerService.getUserByMagentoCustomerId(magentoCustomerIdInteger);

                userService.checkUserInfoAndOrganization(r, magentoCustomerIdInteger);

                return r;
            }

        } catch (Exception e) {

        }

        return null;
    }

    protected SimpleRestClient getHttpClient(){
        SimpleRestClient client = new SimpleRestClient();

        client.setHttpUsername(imCloudProperties.getSecurity().getFsProCloud().getApi().getUser());
        client.setHttpPassword(imCloudProperties.getSecurity().getFsProCloud().getApi().getPass());

        return client;
    }


    public boolean canUserUploadDocuments(User uploadingUser) {
        if(uploadingUser != null) {
            // The user was originally defined by an access token from FS Pro Cloud, meaning he already has FS Pro, and should be allowed.
            // More security checks can be added in the future.
            return true;
        }else{
            return false;
        }
    }

    public int getNumberOfDocumentsUploaded(User u){
        List<ImDocument> docsAlreadyUploaded = imDocumentRepository.findByUserId(u.getId());
        return docsAlreadyUploaded.size();
    }

    public boolean hasUserAvailableStorage(User uploadingUser, boolean isOverwritingExistingDocument) {
        if(uploadingUser != null) {

            int inUse = getNumberOfDocumentsUploaded(uploadingUser);
            int allowed = getDocumentStorageSize(uploadingUser);

            if(isOverwritingExistingDocument){
                allowed++;
            }

            if(inUse < allowed){
                return true;
            }else{
                return false;
            }

        }else{
            return false;
        }
    }

    /**
     * Get how many documents the user is allowed to upload
     * @param u
     * @return
     */
    public int getDocumentStorageSize(User u){
        if(u.getEmail() != null && (u.getEmail().endsWith("@informationmapping.com") || u.getEmail().endsWith("@storefront.be"))){
            return 1000000;
        }else{
            return 5;
        }
    }
}
