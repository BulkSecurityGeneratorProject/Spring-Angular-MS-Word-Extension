package be.storefront.imicloud.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by wouter on 05/01/2017.
 */
@ResponseStatus(value = HttpStatus.OK)
public class OKDocumentExists extends RuntimeException {
}
