package com.zzg.mybatis.generator.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 是否忽略Excel中的值不填充数据
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD})
public @interface IsIgnore
{
    
    /**
     * 有注解:忽略  没注解:不忽略
     */
}
