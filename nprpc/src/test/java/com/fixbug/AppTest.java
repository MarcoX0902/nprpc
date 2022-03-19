package com.fixbug;

import static org.junit.Assert.assertTrue;

import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * 测试Protobuf的序列化和反序列化
     * @param args
     */
    @Test
    public void test(){
        UserServiceProto.LoginRequest.Builder login_builder = UserServiceProto.LoginRequest.newBuilder();
        login_builder.setName("marco");
        login_builder.setPwd("123456");

        UserServiceProto.LoginRequest request = login_builder.build();
        System.out.println(request.getName());
        System.out.println(request.getPwd());

        /*
         * 把LoginRequest对象序列化成字节流，通过网络发送出去
         * 此处的sendbuf就可以通过网络发送出去了
         */
        byte[] sendbuf = request.toByteArray();

        /*
         *Protobuf从byte数组字节流反序列化生成LoginRequest对象
         */
        try {
            UserServiceProto.LoginRequest r = UserServiceProto.LoginRequest.parseFrom(sendbuf);
            System.out.println(r.getName());
            System.out.println(r.getPwd());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


    }

    /**
     * @Description: 
     * @Param: []
     * @Author: Mr.Bin 
     * @Date: 14:47 2022/3/7
     * @return: void
     **/
    @Test
    public void test1(){
        Properties pro = new Properties();//key-value
        try {
            pro.load(AppTest.class.getClassLoader().getResourceAsStream("comfig.properties"));
            System.out.println(pro.getProperty("IP"));
            System.out.println(pro.getProperty("PORT"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
