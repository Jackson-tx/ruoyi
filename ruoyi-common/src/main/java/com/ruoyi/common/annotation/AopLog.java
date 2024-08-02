package com.ruoyi.common.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Method;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AopLog {
    String module()  default "";//模块
    String method()  default "";//方法
    String operateType() default "OTHER" ;//事件类型：LOGIN；LOGINOUT；ADD；DELETE；UPDATE；SELETE；UPLOAD；DOWNLOAD；OTHER
    String logType() default "0";//日志类型：0：系统日志；1：业务日志
    String interfaceName() default "";//接口名称
}
