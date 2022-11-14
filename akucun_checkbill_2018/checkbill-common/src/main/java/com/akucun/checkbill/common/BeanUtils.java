package com.akucun.checkbill.common;

import org.springframework.cglib.beans.BeanCopier;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhaojin on 3/31/2018.
 */
public class BeanUtils {

    private static ConcurrentHashMap<String,BeanCopier> cache=new ConcurrentHashMap<String, BeanCopier>();
    public static <T> T copyBeanProperties(Object sourceObj, T target, boolean useConverter)
    {
        if(sourceObj==null || target==null)
            return null;

        String key=sourceObj.getClass().getSimpleName()+target.getClass().getSimpleName();
        BeanCopier copier = cache.get(key);
        if(copier==null){
            copier = BeanCopier.create(sourceObj.getClass(), target.getClass(), useConverter);
            cache.putIfAbsent(key, copier);
        }
        copier.copy(sourceObj, target, null);
        return target;
    }
}
