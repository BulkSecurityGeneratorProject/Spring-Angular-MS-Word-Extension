package be.storefront.imicloud.security;

import org.springframework.security.crypto.password.PasswordEncoder;

public class DocumentPasswordEncoder implements PasswordEncoder{


    @Override
    public String encode(CharSequence charSequence) {
        return ""+charSequence;
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return s.equals(""+charSequence);
    }
}
