package be.storefront.imicloud.security;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.User;
import be.storefront.imicloud.restclient.SimpleRestClient;
import be.storefront.imicloud.service.MagentoCustomerService;
import be.storefront.imicloud.service.UserService;
import org.elasticsearch.common.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class ImCloudSecurity {


    @Inject
    private ImCloudProperties imCloudProperties;

    @Inject
    private UserService userService;

    @Inject
    private MagentoCustomerService magentoCustomerService;

    // This class handles security features connected to the Word plugin FS Pro.

    public User getUserByFsProAccessToken(String accessToken) {
        String targetURL = imCloudProperties.getSecurity().getFsProCloud().getApi().getUrl() + "sapi/Authentication";
        String body = "{\"Key\":\"" + accessToken + "\"}";

        String magentoCustomerId = null;

        try {
            JSONObject jsonObj = getHttpClient().postAndReadJson(targetURL, body);

            magentoCustomerId = jsonObj.getString("Id");

            if (magentoCustomerId != null && magentoCustomerId.length() > 0) {
                int magentoCustomerIdInteger = Integer.parseInt(magentoCustomerId);

                return magentoCustomerService.getUserByMagentoCustomerId(magentoCustomerIdInteger);
            }

        } catch (Exception e) {

        }

        return null;
    }

    public boolean canCustomerUseTheCloud(int magentoCustomerId) {
        return true;

        /*
        $response = $this->_doRequest('POST', 'sapi/Authentication' , null, array('Key' => $token));

        $status = $response->getStatus();
        $body = $response->getBody();

        if ($status === 200) {
            $data = Zend_Json::decode($body);
            if(isset($data['Id'])){
                return $data['Id'];
            }
        }
         */
    }

    protected SimpleRestClient getHttpClient(){
        SimpleRestClient client = new SimpleRestClient();

        client.setHttpUsername(imCloudProperties.getSecurity().getFsProCloud().getApi().getUser());
        client.setHttpPassword(imCloudProperties.getSecurity().getFsProCloud().getApi().getPass());

        return client;
    }



}
