package com.fixbug.controller;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

/**
 * @description:
 * @author: Mr.Bin
 * @time: 2022/3/8 21:05
 */
public class NrpcController implements RpcController {

    private String errText;
    private boolean isFailed;

    @Override
    public void reset() {
        this.isFailed = false;
        this.errText = "";
    }

    @Override
    public boolean failed() {
        return isFailed;
    }

    @Override
    public String errorText() {
        return errText;
    }

    @Override
    public void startCancel() {

    }

    @Override
    public void setFailed(String s) {
        this.isFailed = true;
        this.errText = s;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public void notifyOnCancel(RpcCallback<Object> rpcCallback) {

    }
}
