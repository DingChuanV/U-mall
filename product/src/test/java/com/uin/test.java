package com.uin;

import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * @author wanglufei
 * @description: TODO
 * @date 2022/5/13/4:12 PM
 */
@SpringBootTest
public class test {

    public static void main(String[] args) {
        String s = getOldNum(9999L);
        System.out.println(s);
    }

    public static String getOldNum(Long count) {
        String prefix = "label";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String middle = dateFormat.format(new Date());
        String suffix = "001";
        String serialNumber = prefix + middle + suffix;

        if (serialNumber != null && !serialNumber.isEmpty()) {
            long l = Long.parseLong(String.valueOf(count));
            long l1 = ++l;
            serialNumber = String.format(prefix + middle + "%06d", l1);
        }
        return serialNumber;
    }

}
