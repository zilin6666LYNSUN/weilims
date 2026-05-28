package com.wei.common.mybatis.plugin.sensitive;

/**
 * 脱敏处理
 *
 * @author wei
 * @date 2025/1/7 22:42
 */
@FunctionalInterface
public interface SensitiveDataProcessor {
    /**
     * 处理方法
     */
    String processor(String s);
}
