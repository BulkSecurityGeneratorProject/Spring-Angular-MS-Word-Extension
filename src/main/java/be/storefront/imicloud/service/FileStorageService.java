package be.storefront.imicloud.service;

import be.storefront.imicloud.config.ImCloudProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class FileStorageService {

//
//    @Autowired
//    private HttpServletRequest request;

    @Inject
    private ImCloudProperties imCloudProperties;


    public String saveFile(MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        String checksum = getFileChecksum(digest, multipartFile.getInputStream());

        String extension = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf("."));

        //request.getServletContext().getRealPath()
        String uploadDir = imCloudProperties.getFileStorageDir();
        String targetFileString = uploadDir + checksum +extension;

        File targetFile = new File(targetFileString);
        multipartFile.transferTo(targetFile);

        String absPath = targetFile.getAbsolutePath();

        String relPath = absPath.substring(uploadDir.length());
        return relPath;
    }


    private static String getFileChecksum(MessageDigest digest, InputStream is) throws IOException
    {

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = is.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        is.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }
}
