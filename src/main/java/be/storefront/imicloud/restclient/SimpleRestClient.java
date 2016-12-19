package be.storefront.imicloud.restclient;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.crypto.codec.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wouter on 19/12/2016.
 */
public class SimpleRestClient {

    protected String httpUsername;
    protected String httpPassword;

    public JSONObject getAndReadJson(String targetUrl) throws IOException, JSONException {
        return doRequest("GET", targetUrl, null);
    }

    public JSONObject postAndReadJson(String targetUrl, String body) throws IOException, JSONException {
        return doRequest("POST", targetUrl, body);
    }

    protected JSONObject doRequest(String method, String url, String body) throws IOException, JSONException {

        String charset = "UTF-8";

        URL urlObj;
        HttpURLConnection connection = null;
        try {
            //Create connection
            urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
            if (getHttpPassword() != null && getHttpUsername() != null) {
                String unhashedCode = getHttpUsername() + ":" + getHttpPassword();

                byte[] bytesEncoded = Base64.encode(unhashedCode.getBytes());

                String hashedCode = new String(bytesEncoded);

                connection.setRequestProperty("Authorization", "Basic " + hashedCode);
            }


            if (body != null) {
                connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(body.getBytes().length));
            }

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            if (body != null) {
                //Send request
                DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
                wr.writeBytes(body);
                wr.flush();
                wr.close();
            }

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            connection.disconnect();

            String responseString = response.toString();

            JSONObject jsonObj = new JSONObject(responseString);

            return jsonObj;

        } catch (Exception e) {
            throw e;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String getHttpUsername() {
        return httpUsername;
    }

    public void setHttpUsername(String httpUsername) {
        this.httpUsername = httpUsername;
    }

    public String getHttpPassword() {
        return httpPassword;
    }

    public void setHttpPassword(String httpPassword) {
        this.httpPassword = httpPassword;
    }
}
