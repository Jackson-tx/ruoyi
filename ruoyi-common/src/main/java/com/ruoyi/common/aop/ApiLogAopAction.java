package com.ruoyi.common.aop;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ruoyi.common.annotation.AopLog;
import com.ruoyi.common.core.redis.RedisCache;
import org.apache.commons.collections4.MapUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Aspect
public class ApiLogAopAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiLogAopAction.class);

    @Autowired
    HttpServletRequest request;

    @Autowired
    private RedisCache redisCache;
    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_COUNT_KEY = "login_counts:";
    /**
     * 登录用户 redis key
     */
    public static final int COUNT_1 = 1;

    /**
     * 定义切面
     * - 此处代表com.example.demo.module.controller包下的所有接口都会被统计
     */
    @Pointcut("@annotation(com.ruoyi.common.annotation.AopLog)")
    //@Pointcut("execution(* cn.agile.platform.core.web.controller..*.*(..))")
    public void log() {
    }

    /**
     * 在接口原有的方法执行前，将会首先执行此处的代码
     */
    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        AopLog annotation = joinPoint.getTarget().getClass().getAnnotation(AopLog.class);
        LOGGER.info("URI:[{}], 入方法了:{}", request.getServletPath());
        Map<String, Object> cacheMap = redisCache.getCacheMap(LOGIN_COUNT_KEY);
        if (MapUtils.isEmpty(cacheMap)){
            Map<String, Object> map = new HashMap<>();
            map.put( request.getServletPath(),COUNT_1);
            redisCache.setCacheMap(LOGIN_COUNT_KEY,map);
        }else {
            if (cacheMap.containsKey(request.getServletPath())){
                cacheMap.put(request.getServletPath(),Integer.valueOf(String.valueOf(cacheMap.get(request.getServletPath())))+1);
            }else {
                cacheMap.put(request.getServletPath(),COUNT_1);
            }
            redisCache.setCacheMap(LOGIN_COUNT_KEY,cacheMap);
        }
        Set<String> strings = cacheMap.keySet();
        strings.stream().forEach(e ->{
            LOGGER.info("记录接口访问次数{}---{}",e,cacheMap.get(e).toString());
        });
    }

    /**
     * 只有正常返回才会执行此方法
     * 如果程序执行失败，则不执行此方法
     */
    @AfterReturning(returning = "returnVal", pointcut = "log()")
    public void doAfterReturning(JoinPoint joinPoint, Object returnVal) {
        LOGGER.info("URI:[{}]", request.getServletPath());
    }

    /**
     * 当接口报错时执行此方法
     */
    @AfterThrowing(pointcut = "log()")
    public void doAfterThrowing(JoinPoint joinPoint) {
        LOGGER.info("接口访问失败，URI:[{}]", request.getServletPath());
        Map<String, Object> cacheMap = redisCache.getCacheMap(LOGIN_COUNT_KEY);
        if (MapUtils.isEmpty(cacheMap)){
            Map<String, Object> map = new HashMap<>();
            map.put( request.getServletPath(),COUNT_1);
            cacheMap.put(LOGIN_COUNT_KEY,map);
        }else {
            if (cacheMap.containsKey(request.getServletPath())){
                cacheMap.put(request.getServletPath(),Integer.valueOf(String.valueOf(cacheMap.get(request.getServletPath())))+1);
            }else {
                cacheMap.put(request.getServletPath(),COUNT_1);
            }
        }
        System.out.println("记录接口访问此时"+cacheMap.get(LOGIN_COUNT_KEY));
    }

    /**
     * 在接口原有的方法执行后，都会执行此处的代码（final）
     */
    @After("log()")
    public void doAfter(JoinPoint joinPoint) {
        LOGGER.info("该方法结束了");
    }
}
