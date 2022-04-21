package com.uin;

import com.aliyun.oss.*;
import com.aliyun.oss.model.PutObjectRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.uin.product.entity.BrandEntity;
import com.uin.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ProductApplicationTests {
    @Autowired(required = false)
    BrandService brandService;
    @Autowired
    OSSClient ossClient;

    @Test
    public void test() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream("/Users/wanglufei/Desktop/mall/谷粒商城/pics/0d40c24b264aa511.jpg");
        ossClient.putObject("u-mall-bearbrick0","0d40c24b264aa511.jpg",inputStream);
        ossClient.shutdown();
        System.out.println("上传完成");
    }


    @Test
    public void test01() {
        BrandEntity brand = new BrandEntity();
        brand.setBrandId(1L);
        brand.setName("Iphone 12Pro");
        boolean save = brandService.updateById(brand);
        System.out.println(save);
    }

    /**
     * @author wanglufei
     * @date 2022/4/18 9:22 PM
     */
    @Test
    public void test02() {
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1));
        list.forEach((item) -> {
            System.out.println(item);
        });
        System.out.println(list);
    }
    /**
     * 测试OSS
     * @author wanglufei
     * @date 2022/4/21 8:33 PM
     */
    @Test
    public void test03() {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-zhangjiakou.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        //用户登录名称 u-mall-bearbrick0@1872205434747247.onaliyun.com
        //AccessKey ID LTAI5t8D9u8mBzNiVSndThrZ
        //AccessKey Secret LTAI5t8D9u8mBzNiVSndThrZ

//        用户登录名称 mall@1872205434747247.onaliyun.com
//        AccessKey ID LTAI5t5gAM5Mihiti8KNTwBY
//        AccessKey Secret v0Tz5kI4iWi5Un51Wh0VOrrdZ675gk
        String accessKeyId = "LTAI5t8D9u8mBzNiVSndThrZ";
        String accessKeySecret = "LTAI5t8D9u8mBzNiVSndThrZ";
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "u-mall-bearbrick0";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "/pics/0d40c24b264aa511.jpg";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
        String filePath = "";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new File(filePath));
            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // metadata.setObjectAcl(CannedAccessControlList.Private);
            // putObjectRequest.setMetadata(metadata);

            // 上传文件。
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

    }
}
