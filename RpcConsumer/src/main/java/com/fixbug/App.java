package com.fixbug;

import com.fixbug.consumer.RpcConsumer;
import com.fixbug.controller.NrpcController;

import javax.xml.ws.Response;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        /**
         * @Description:模拟Rpc方法调用者
         **/
        UserServiceProto.UserServiceRpc.Stub stub
                = UserServiceProto.UserServiceRpc.newStub(new RpcConsumer("config.properties"));
        UserServiceProto.LoginRequest.Builder login_builder = UserServiceProto.LoginRequest.newBuilder();
        login_builder.setName("Mr.Bin");
        login_builder.setPwd("123456");
        NrpcController con = new NrpcController();
        stub.login(con, login_builder.build(), response ->{
            /**
             * @Description:这里就是rpc方法调用完成以后的返回值
             **/
            if(con.failed()){//rpc方法没有调用成功
                System.out.println(con.errorText());
            }else {
                System.out.println("receive rpc call response!");
                if (response.getErrno() == 0){//调用正常
                    System.out.println(response.getResult());
                }else{//调用出错
                    System.out.println(response.getErrinfo());
                }
            }

    });
    }
}
