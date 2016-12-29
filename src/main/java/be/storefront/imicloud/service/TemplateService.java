package be.storefront.imicloud.service;

import org.springframework.stereotype.Service;

/**
 * Created by wouter on 30/12/2016.
 */
@Service
public class TemplateService {

    public String[] getAllTemplates(){
        String[] all = {"web1","presentation1"};
        return all;
    }

}
