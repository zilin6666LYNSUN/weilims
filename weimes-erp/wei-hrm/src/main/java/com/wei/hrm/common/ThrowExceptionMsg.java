package com.wei.hrm.common;

/**
 * 抛异常信息
 *
 * @author wei
 * @date 2024/9/12 21:25
 */

@FunctionalInterface
public interface ThrowExceptionMsg {
    void throwMsg(String msg);
}
