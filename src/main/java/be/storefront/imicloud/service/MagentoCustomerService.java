package be.storefront.imicloud.service;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.User;
import be.storefront.imicloud.restclient.SimpleRestClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.*;
import java.net.URLEncoder;
import java.util.Optional;

@Service
public class MagentoCustomerService {

    @Inject
    private UserService userService;

    @Inject
    private ImCloudProperties imCloudProperties;

    @Inject
    private PasswordEncoder passwordEncoder;

    private final Logger log = LoggerFactory.getLogger(MagentoCustomerService.class);

    public User getUserByMagentoCustomerId(int customerId) throws IOException, JSONException {
        String url = imCloudProperties.getSecurity().getMagento().getApi().getUrl() + "fspro/accounts/?id="+ URLEncoder.encode(""+customerId, "UTF-8");

        SimpleRestClient client = getHttpClient();
        JSONObject jsonObj = client.getAndReadJson(url);

        JSONArray accountArray = jsonObj.getJSONArray("account");
        JSONObject account = accountArray.getJSONObject(0);
        String email = account.getString("email");
        String firstName = account.getString("first_name");
        String lastName = account.getString("last_name");
        String langKey = account.getString("lang_code");
        Boolean isActive = account.getBoolean("active");

        Optional<User> optionalExistingUser = userService.getUserWithAuthoritiesByLogin(email);

        User r = null;

        if(optionalExistingUser.isPresent()){
            // Update existing user with data from Magento

            User existingUser = optionalExistingUser.get();
            existingUser.setFirstName(firstName);
            existingUser.setLangKey(langKey);
            existingUser.setLastName(lastName);
            existingUser.setActivated(isActive);
            existingUser.setCreatedBy("magento");

            r = userService.saveUser(existingUser);

        }else{
            // Create new user that resembles the Magento customer
            String tempPassword = Long.toHexString(Double.doubleToLongBits(Math.random()));

            r = userService.createUser( email,  tempPassword,  firstName,  lastName,  email, langKey);
            r.setActivated(isActive);
            r.setCreatedBy("magento");
            r = userService.saveUser(r);
        }

        

        return r;
    }

    public User getUserByMagentoLogin(String email, String password) throws JSONException, IOException {
        String url = imCloudProperties.getSecurity().getMagento().getApi().getUrl() + "fspro/authentication";
        String body = "{\"email\":\""+email+"\",\"password\":\""+password+"\"}";

        SimpleRestClient client = getHttpClient();
        JSONObject jsonObj = client.postAndReadJson(url, body);

        int id = Integer.parseInt(jsonObj.getString("id"));

        User u = getUserByMagentoCustomerId(id);

        String hashedPassword = passwordEncoder.encode(password);
        u.setPassword(hashedPassword);

        return userService.saveUser(u);
    }

    protected SimpleRestClient getHttpClient(){
        SimpleRestClient client = new SimpleRestClient();

        client.setHttpUsername(imCloudProperties.getSecurity().getMagento().getApi().getUser());
        client.setHttpPassword(imCloudProperties.getSecurity().getMagento().getApi().getPass());

        return client;
    }


}
