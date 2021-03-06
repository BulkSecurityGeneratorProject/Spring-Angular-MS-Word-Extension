package be.storefront.imicloud.config;

import be.storefront.imicloud.domain.util.UrlFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import javax.validation.constraints.NotNull;

/**
 * Properties specific to IM Cloud.
 * <p>
 * <p>
 * Properties are configured in the application.yml file.
 * </p>
 */
@ConfigurationProperties(prefix = "imcloud", ignoreUnknownFields = false)
public class ImCloudProperties {

    private String fileStorageDir = null;
    private String baseUrl = null;
    private String cloudName = null;
    private String forgotPasswordUrl;
    private String registerNewAccountUrl;

    private final Security security = new Security();

    private final FsProCloud fsProCloud = new FsProCloud();


    public String getCloudName() {
        return cloudName;
    }

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }

    public String getFileStorageDir() {
        return fileStorageDir;
    }

    public void setFileStorageDir(String fileStorageDir) {

        // Force ending with "/"
        if (!"/".equals(fileStorageDir.substring(fileStorageDir.length() - 1, fileStorageDir.length()))) {
            fileStorageDir += "/";
        }
        this.fileStorageDir = fileStorageDir;
    }

    public String getBaseUrl(){
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {

        // Force ending with "/"
        if (!"/".equals(baseUrl.substring(baseUrl.length() - 1, baseUrl.length()))) {
            baseUrl += "/";
        }
        this.baseUrl = baseUrl;
    }

    public String getForgotPasswordUrl() {
        return forgotPasswordUrl;
    }

    public void setForgotPasswordUrl(String forgotPasswordUrl) {
        this.forgotPasswordUrl = forgotPasswordUrl;
    }

    public String getRegisterNewAccountUrl() {
        return registerNewAccountUrl;
    }

    public void setRegisterNewAccountUrl(String registerNewAccountUrl) {
        this.registerNewAccountUrl = registerNewAccountUrl;
    }

    public Security getSecurity() {
        return security;
    }

    public static class Security {

        private final FsProCloud fsProCloud = new FsProCloud();
        private final Magento magento = new Magento();

        public FsProCloud getFsProCloud() {
            return fsProCloud;
        }

        public Magento getMagento() {
            return magento;
        }
    }

    public static class Magento {

        private boolean allowMagentoCustomerLogin = false;


        private final Api api = new Api();

        public boolean getAllowMagentoCustomerLogin() {
            return allowMagentoCustomerLogin;
        }

        public void setAllowMagentoCustomerLogin(boolean allowMagentoCustomerLogin) {
            this.allowMagentoCustomerLogin = allowMagentoCustomerLogin;
        }

        public Api getApi() {
            return api;
        }
    }

    public static class SubscriptionCheck {

        private String url = null;
        private String user = null;
        private String pass = null;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            url = UrlFilter.forceUrlToEndWithSlash(url);

            this.url = url;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }
    }

    public static class FsProCloud {

        private final Api api = new Api();

        public Api getApi() {
            return api;
        }
    }

    public static class Api {

        private String url = null;
        private String user = null;
        private String pass = null;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            url = UrlFilter.forceUrlToEndWithSlash(url);

            this.url = url;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }
    }
}
