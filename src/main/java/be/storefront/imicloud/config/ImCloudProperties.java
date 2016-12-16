package be.storefront.imicloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import javax.validation.constraints.NotNull;

/**
 * Properties specific to IM Cloud.
 *
 * <p>
 *     Properties are configured in the application.yml file.
 * </p>
 */
@ConfigurationProperties(prefix = "imcloud", ignoreUnknownFields = false)
public class ImCloudProperties {

    private String fileStorageDir = null;

    public String getFileStorageDir() {
        return fileStorageDir;
    }

    public void setFileStorageDir(String fileStorageDir){

        // Force ending with "/"
        if(!"/".equals(fileStorageDir.substring(fileStorageDir.length() - 1, fileStorageDir.length()))){
            fileStorageDir += "/";
        }
        this.fileStorageDir = fileStorageDir;
    }
}
