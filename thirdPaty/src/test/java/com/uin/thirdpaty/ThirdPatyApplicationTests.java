package com.uin.thirdpaty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
class ThirdPatyApplicationTests {

    @Autowired
    OSSClient ossClient;

    @Test
    public void test() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream("/Users/wanglufei/Desktop/mall/谷粒商城/pics/0d40c24b264aa511.jpg");
        ossClient.putObject("u-mall-bearbrick0", "0d40c24b264aa511.jpg", inputStream);
        ossClient.shutdown();
        System.out.println("上传完成");
    }

}
