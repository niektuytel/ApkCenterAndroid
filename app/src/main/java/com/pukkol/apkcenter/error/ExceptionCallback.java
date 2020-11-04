package com.pukkol.apkcenter.error;

public class ExceptionCallback {
    public interface onExceptionListener{
        void onException(Throwable throwable);
    }
}
