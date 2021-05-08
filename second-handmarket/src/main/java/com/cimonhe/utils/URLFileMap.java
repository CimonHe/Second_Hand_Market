package com.cimonhe.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class URLFileMap {

    private URLFileMap() {
    }

    public static String GOODS_FILEPATH = "C:\\second_hand_market\\goods\\";

    public static String GOODS_URL_PATH = "/goods/";

    public static String DEFAULT_PORTRAIT_FILEPATH = "C:\\second_hand_market\\user\\portrait\\0\\";


    public static String PORTRAIT_FILEPATH = "C:\\second_hand_market\\user\\portrait\\";

    public static String PORTRAIT_URL_PATH = "/user/portrait/";

    public static String getGoodsFilePath(int goodsId){
        return GOODS_FILEPATH +goodsId+"/";
    }

    public static String getGoodsURLPath(int goodsId){
        return GOODS_URL_PATH+goodsId+"/";
    }

    public static String getPortraitFilepath(int userId){
        return PORTRAIT_FILEPATH +userId+"/";
    }

    public static String getPortraitUrlPath(int userId){
        return PORTRAIT_URL_PATH+userId+"/";
    }

    public static List<String> getGoodsImgPaths(int goodsId){
        List<String> imgPaths = new ArrayList<>();
        File file = new File(getGoodsFilePath(goodsId));
        if (file.listFiles()==null)
            return imgPaths;
        for (File img : file.listFiles()) {
            imgPaths.add(getGoodsURLPath(goodsId)+img.getName());
        }
        return imgPaths;
    }

    public static String getGoodsImgPath(int goodsId){
        File file = new File(getGoodsFilePath(goodsId));
        if (file.listFiles()==null)
            return null;
        return getGoodsURLPath(goodsId)+file.listFiles()[0].getName();
    }

    public static String getPortraitImgPath(int userId){
        File file = new File(getPortraitFilepath(userId));
        if (file.listFiles()==null)
            return null;
        return getPortraitUrlPath(userId)+file.listFiles()[0].getName();
    }

    public static boolean defaultPortrait(int userId){
        try {
            File fileFrom = new File(DEFAULT_PORTRAIT_FILEPATH);
            File fileTo = new File(getPortraitFilepath(userId)+"/"+fileFrom.listFiles()[0].getName());
            for (File file : fileFrom.listFiles()) {
                java.nio.file.Files.copy(file.toPath(),fileTo.toPath());
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void deleteDirect(File fileDir) {

        // 如果是目录
        if (fileDir.exists() && fileDir.isDirectory()) {
            File[] listFiles = fileDir.listFiles();

            for (File file : listFiles) {
                deleteDirect(file);
            }
        }
        fileDir.delete();
    }

    public static void upPortrait(int userId, MultipartFile file) throws IOException {
        File originPic = new File(getPortraitFilepath(userId));
        if(originPic.exists())
            deleteDirect((originPic));
        originPic.mkdirs();
        String fileName = file.getOriginalFilename();
        file.transferTo(new File((getPortraitFilepath(userId))+fileName.substring(fileName.lastIndexOf("."))));
    }



    public static void main(String[] args) {
        defaultPortrait(50);
    }
}
