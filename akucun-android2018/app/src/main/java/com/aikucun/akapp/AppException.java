package com.aikucun.akapp;

/**
 * 应用程序异常：用于捕获异常和提示错误信息
 * Created by jarry on 16/9/8.
 */
public class AppException extends Exception implements Thread.UncaughtExceptionHandler
{


    @Override
    public void uncaughtException(Thread thread, Throwable e)
    {

    }
}
