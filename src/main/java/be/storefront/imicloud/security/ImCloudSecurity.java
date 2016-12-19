package be.storefront.imicloud.security;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.User;
import be.storefront.imicloud.service.UserService;
import org.elasticsearch.common.inject.Inject;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class ImCloudSecurity {


    @Inject
    private ImCloudProperties imCloudProperties;

    @Inject
    private UserService userService;

    // This class handles security features connected to the Word plugin FS Pro.

    public User getUserByFsProAccessToken(String accessToken){
        String targetURL = imCloudProperties.getSecurity().getFsProCloud().getApi().getUrl()+"sapi/Authentication";
        String body = "{\"Key\":\""+accessToken+"\"}";

        String charset = "UTF-8";

        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);

            connection.setRequestProperty("Content-Length", "" +
                Integer.toString(body.getBytes().length));

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                connection.getOutputStream ());
            wr.writeBytes (body);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            String responseString = response.toString();

            JSONObject jsonObj = new JSONObject(responseString);
            String magentoCustomerId = jsonObj.getString("Id");

            if(magentoCustomerId != null && magentoCustomerId.length() > 0{
                
            }
            //userService.

            return null;

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }



    public boolean canCustomerUseTheCloud(int magentoCustomerId){
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
}
