package com.cimonhe.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;


public class ValidateUtil {

    public static final String MOBILE_PATTERN="^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))(\\d{8})$";

    public static final String EMAIL_PATTERN = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$" ;


    public static boolean validateMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)){
            return Boolean.FALSE;
        }
        return mobile.matches(MOBILE_PATTERN);
    }


    public static boolean validateEmail(final String email){
        if(StringUtils.isEmpty(email)){
            return Boolean.FALSE ;
        }
        return email.matches(EMAIL_PATTERN) ;
    }


    public static boolean checkFile(String fileName) {
        String suffixList = "jpg,gif,png,ico,bmp,jpeg";
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (suffixList.contains(suffix.trim().toLowerCase())) {
            return true;
        }
        return false;
    }
    public static boolean checkFiles(MultipartFile[] files) {
        for (MultipartFile file : files) {
            if (!checkFile(file.getOriginalFilename()))
                return false;
        }
        return true;
    }



}