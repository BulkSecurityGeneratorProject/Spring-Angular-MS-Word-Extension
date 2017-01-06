package be.storefront.imicloud.web.rest.response;

/**
 * Created by wouter on 06/01/2017.
 */
public class ImDocumentUploadError extends ImDocumentResponse{

    public final static String ERROR_STORAGE_FULL = "STORAGE_FULL";
    public final static String ERROR_ACCESS_DENIED = "ACCESS_DENIED";
    public final static String ERROR_OTHER = "OTHER";

    private String errorCode;
    private String errorMsg;

    public ImDocumentUploadError(String errorCode, String errorMsg){
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
