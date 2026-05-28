package com.wei.exception;

/**
 * oss异常
 *
 * @author wei
 * @date 2025/2/11 17:12
 */
public class weiOssException extends RuntimeException {

    public weiOssException() {
    }

    public weiOssException(String msg) {
        super(msg);
    }

    public weiOssException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
