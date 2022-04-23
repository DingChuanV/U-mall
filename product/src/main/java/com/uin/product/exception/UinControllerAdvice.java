package com.uin.product.exception;

import com.uin.exception.UinException;
import com.uin.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wanglufei
 * @description: 集中处理（感知）错误
 * @date 2022/4/22/2:43 PM
 */
@Slf4j
//@ControllerAdvice(basePackages = "com.uin.product.controller")
//@ResponseBody
//效果一样
@RestControllerAdvice(basePackages = "com.uin.product.controller")
public class UinControllerAdvice {
    /**
     * 处理数据校验
     *
     * @param e
     * @return com.uin.utils.R
     * @author wanglufei
     * @date 2022/4/22 3:02 PM
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handlerFormNumException(MethodArgumentNotValidException e) {
        log.error("数据校验出现问题：{},异常类型：{}", e.getMessage(), e.getClass());
        BindingResult result = e.getBindingResult();
        Map<String, String> map = new HashMap<>();
        result.getFieldErrors().forEach((item) -> {
            //错误消息提示
            String message = item.getDefaultMessage();
            //那个字段出现了问题
            String field = item.getField();
            //将这些错误的信息用map装起来
            map.put(field, message);
        });
        return R.error(UinException.VAILD_EXCEPTION.getCode(), UinException.VAILD_EXCEPTION.getMessage()).put(
                "data", map);
    }

    @ExceptionHandler(value = Throwable.class)
    public R maxHandlerException(Throwable e) {
        log.error("异常{ }", e);
        return R.error(UinException.UNKOWN_EXCEPTION.getCode(), UinException.UNKOWN_EXCEPTION.getMessage());
    }

}
