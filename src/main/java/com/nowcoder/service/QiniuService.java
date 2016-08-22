package com.nowcoder.service;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.controller.NewsController;
import com.nowcoder.util.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by sjh on 2016/8/22.
 */
@Service
public class QiniuService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(QiniuService.class);

    //设置好账号的ACCESS_KEY和SECRET_KEY
    String ACCESS_KEY = "CenY6cXiS1uIH_bdKPzcw_ysi9H0_hlgbKCAwH6f";
    String SECRET_KEY = "Dd_AjnfgZNdk7WjeA_AKjGp4qa4Qc4mF-HbdnMon";
    //要上传的空间
    String bucketname = "myzone";
//    //上传到七牛后保存的文件名
//    String key = "my-java.png";
//    //上传文件的路径
//    String FilePath = "/.../...";

    //密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    //创建上传对象
    UploadManager uploadManager = new UploadManager();

    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken(){
        return auth.uploadToken(bucketname);
    }

    public String saveImage(MultipartFile file) throws IOException {
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf('.');
            if(dotPos < 0){
                return null;
            }

            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            if(!ToutiaoUtil.isFileAllowed(fileExt)) {
                return null;
            }

            String fileName = UUID.randomUUID().toString().replaceAll("-", "")+"."+fileExt;

            //调用put方法上传
            Response res = uploadManager.put(file.getBytes(), fileName, getUpToken());
            //打印返回的信息
            System.out.println(res.toString());
            if(res.isOK() && res.isJson()){
                String key = JSONObject.parseObject(res.bodyString()).get("key").toString();
                System.out.println(key);
                return ToutiaoUtil.QINIU_DOMAIN_PREFIX + key;
            }else {
                logger.error("七牛异常："+res.bodyString());
                return null;
            }
        } catch (QiniuException e) {
            logger.error("七牛异常" + e.getMessage());
            return null;
        }
    }

    public static void main(String args[]) throws IOException{
//        new QiniuService().saveImage();
    }

}
