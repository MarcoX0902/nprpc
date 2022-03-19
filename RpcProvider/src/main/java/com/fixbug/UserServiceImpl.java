package com.fixbug;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

/**
 * @description:
 * @author: Mr.Bin
 * @time: 2022/3/7 15:05
 */
public class UserServiceImpl extends UserServiceProto.UserServiceRpc{
    /**
     * @Description: 登陆业务
     * @Param: [name, pwd]
     * @Author: Mr.Bin
     * @Date: 15:06 2022/3/7
     * @return: boolean
     **/
    public boolean login(String name, String pwd){
        System.out.println("call UserServiceImpl -> login");
        System.out.println("name:" + name);
        System.out.println("pwd:" + pwd);
        return true;
    }

    /**
     * @Description: 注册业务
     * @Param: [name, pwd, age, sex, phone]
     * @Author: Mr.Bin
     * @Date: 15:10 2022/3/7
     * @return: boolean
     **/
    public boolean reg(String name, String pwd, int age, UserServiceProto.RegRequest.SEX sex, String phone){
        System.out.println("call UserServiceImpl -> reg");
        System.out.println("name:" + name);
        System.out.println("pwd:" + pwd);
        System.out.println("age:" +age);
        System.out.println("sex:" +sex);
        System.out.println("phone:" +phone);
        return true;
    }

    /**
     * @Description: login的rpc代理方法
     * @Param: [controller, request, done]
     * @Author: Mr.Bin
     * @Date: 16:04 2022/3/7
     * @return: void
     **/
    @Override
    public void login(RpcController controller, UserServiceProto.LoginRequest request,
                      RpcCallback<UserServiceProto.Response> done) {
        //1. 从request里面读取远程rpc调用请求的参数
        String name = request.getName();
        String pwd = request.getPwd();

        //2. 根据解析的参数，做本地业务
        boolean result = login(name,pwd);

        //3. 填写方法的响应值
        UserServiceProto.Response.Builder response_builder = UserServiceProto.Response.newBuilder();
        response_builder.setErrno(0);
        response_builder.setErrinfo("");
        response_builder.setResult(result);

        //4. 把Response对象给到nprpc框架，由框架负责处理发送rpc调用响应值
        done.run(response_builder.build());

    }

    /**
     * @Description: reg的rpc代理方法
     * @Param: [controller, request, done]
     * @Author: Mr.Bin
     * @Date: 16:04 2022/3/7
     * @return: void
     **/
    @Override
    public void reg(RpcController controller, UserServiceProto.RegRequest request,
                    RpcCallback<UserServiceProto.Response> done) {
        //1. 从request里面读取远程rpc调用请求的参数
        String name = request.getName();
        String pwd = request.getPwd();
        int age = request.getAge();
        UserServiceProto.RegRequest.SEX sex = request.getSex();
        String phone = request.getPhone();

        //2. 根据解析的参数，做本地业务
        boolean result = reg(name, pwd, age, sex, phone);

        //3. 填写方法的响应值
        UserServiceProto.Response.Builder response_builder = UserServiceProto.Response.newBuilder();
        response_builder.setErrno(0);
        response_builder.setErrinfo("");
        response_builder.setResult(result);

        //4. 把Response对象给到nprpc框架，由框架负责处理发送rpc调用响应值
        done.run(response_builder.build());
    }
}
