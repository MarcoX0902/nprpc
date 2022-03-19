package com.fixbug;

import com.fixbug.provider.RpcProvider;

import java.security.Provider;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        /**
         * @Description:启动一个可以提供rpc远程方法调用的Server
         * 1. 需要一个RpcProvider（nprpc提供的）对象
         * 2. 向RpcProvider上面注册rpc方法 UserServiceImpl.login  UserServiceImpl.reg
         * 3. 启动RpcProvider这个Server站点  阻塞等待远程rpc方法调用请求
         **/
        RpcProvider.Builder builder = RpcProvider.newBuilder();
        RpcProvider provider = builder.build("config.properties");

        /**
         * @Description: UserServiceImpl: 服务对象名称
         *               Login、reg: 服务方法的名称
         * @Param: [args]
         * @Author: Mr.Bin
         * @Date: 20:13 2022/3/7
         * @return: void
         **/
        provider.registerRpcService(new UserServiceImpl());

        /**
         * @Description: 启动rpc server站点，阻塞等待远程rpc调用请求
         * @Param: [args]
         * @Author: Mr.Bin
         * @Date: 21:10 2022/3/7
         * @return: void
         **/
        provider.start();
    }
}
