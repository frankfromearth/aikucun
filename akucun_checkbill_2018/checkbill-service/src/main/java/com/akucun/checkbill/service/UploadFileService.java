package com.akucun.checkbill.service;

import java.io.File;

/**
 * Created by zhaojin on 3/30/2018.
 */
public interface UploadFileService {
    public String  uploadFile(String filename,File file) throws Exception  ;
}
