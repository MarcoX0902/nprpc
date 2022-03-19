package com.fixbug.callback;

/**
 * @description:
 * @author: Mr.Bin
 * @time: 2022/3/8 15:20
 */
public interface INotifyProvider {
    /**
     * @Description: 回调操作，RpcServer给RpcProvider上报接收到的rpc服务调用相关参数信息
     * @Param: [serviceName, methodName, args]
     * @Author: Mr.Bin
     * @Date: 15:23 2022/3/8
     * @return: byte[]
     **/
    byte[] notify(String serviceName, String methodName, byte[] args);
}
