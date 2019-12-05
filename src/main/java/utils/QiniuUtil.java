package utils;

import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;


/**
 * 七牛云工具类
 */
public class QiniuUtil {
    static String accessKey = "liTE4hA0XYcqf7jClgZoadLuUmxGNpdKZaJze7o4";//2、公钥
    static String secretKey = "00ePU6JewOO4kBq_64gjuYpujp5H7noxpCvASpKV";//3、密钥
    static String bucket = "health-wangqinglong";//4、你创建的空间名称

    static Configuration cfg = new Configuration(Region.huadong());//使用最新的依赖

    /**
     * 文件上传
     *
     * @param img
     * @param fileName
     */
    public static void upload(byte[] img, String fileName) {
        //构造一个带指定 Region(区域) 对象的配置类
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(img, fileName, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);//把json字符串住转成对象
//            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

            System.out.println(putRet.key);//http://q1czp3g0c.bkt.clouddn.com/q5l.jpg
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }


    /**
     * 删除图片
     *
     * @param fileName
     */
    public static void delete(String fileName) {
        //构造一个带指定 Region 对象的配置类
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, fileName);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }

}