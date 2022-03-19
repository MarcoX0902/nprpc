package com.fixbug.provider;

import com.fixbug.callback.INotifyProvider;
import com.fixbug.util.ZkClientUtils;
import com.google.protobuf.Descriptors;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @description: rpc方法发布的站点，只需要一个站点就可以发布当前主机上所有的rpc方法了  用单例模式设计RpcProvider
 * @author: Mr.Bin
 * @time: 2022/3/7 18:50
 */
public class RpcProvider implements INotifyProvider {
    private static final String SERVER_IP = "ip";
    private static final String SERVER_PORT = "port";
    private static final String ZK_SERVER = "zookeeper";
    private String serverIP;
    private int serverPort;
    private String zkServer;
//    private byte[] response;
    private ThreadLocal<byte[]> responsebuflocal;

    /**
     * @Description: 服务方法的类型信息
     * @Param:
     * @Author: Mr.Bin 
     * @Date: 20:45 2022/3/7
     * @return: 
     **/
    private class ServiceInfo{
        public ServiceInfo(){
            this.service = null;
            this.methodMap = new HashMap<>();
        }
        Service service;
        Map<String, Descriptors.MethodDescriptor> methodMap;
    }

    /**
     * @Description: 包含所有的rpc服务对象和服务方法
     * @Param:
     * @Author: Mr.Bin
     * @Date: 20:51 2022/3/7
     * @return:
     **/
    private Map<String,ServiceInfo> serviceMap;


    /**
     * @Description:启动Rpc站点提供服务
     * @Param: []
     * @Author: Mr.Bin
     * @Date: 21:11 2022/3/7
     * @return: void
     **/
    public void start() {
//        serviceMap.forEach((k,v)->{
//            System.out.println(k);
//            v.methodMap.forEach((a,b)-> System.out.println(a));
//        });

        //todo... 把service和method都往zookeeper上注册一下
        ZkClientUtils zk = new ZkClientUtils(zkServer);
        serviceMap.forEach((k,v)->{

            String path = "/" + k;
            zk.createPersistent(path,null);

            v.methodMap.forEach((a,b)->{
                String createPath = path + "/" +a;

                zk.createEphemeral(createPath,serverIP+":"+serverPort);
                //给临时性节点添加监听器watcher
                zk.addWatcher(createPath);
                System.out.println("reg zk -> "+(createPath));
            });
        });

        System.out.println("rpc server start as " + serverIP + ":" + serverPort);

        //启动rpc server网络服务
        RpcServer s = new RpcServer(this);
        s.start(serverIP,serverPort);

    }


    /**
     * @Description: 注册rpc服务方法   只要支持rpc方法的类，都实现了com.google.protobuf.Service这个接口
     * @Param: [userService] 
     * @Author: Mr.Bin 
     * @Date: 20:28 2022/3/7
     * @return: void
     **/
    public void registerRpcService(Service service) {
        Descriptors.ServiceDescriptor sd = service.getDescriptorForType();
        //获取服务对象的名称
        String serviceName = sd.getName();
        ServiceInfo si = new ServiceInfo();
        si.service = service;
        //获取服务对象的所有服务方法列表
        List<Descriptors.MethodDescriptor> methodList = sd.getMethods();
        methodList.forEach(method->{
            //获取服务方法名字
            String methodName = method.getName();
            si.methodMap.put(methodName,method);
        });
        serviceMap.put(serviceName,si);
    }


    /**
     * @Description: notify方法是在多线程环境中被调用到的
     * 接收RpcServer网络模块上报的rpc调用相关信息参数，执行具体的rpc方法调用
     * @Param: [serviceName, methodName, args]
     * @Author: Mr.Bin
     * @Date: 15:39 2022/3/8
     * @return: byte[]
     **/
    @Override
    public byte[] notify(String serviceName, String methodName, byte[] args) {
        ServiceInfo si = serviceMap.get(serviceName);
        Service service = si.service;//获取服务对象
        Descriptors.MethodDescriptor method = si.methodMap.get(methodName);//获取服务方法

        //从args反序列化出method方法的参数 loginRequest RegRequest
        Message request = service.getRequestPrototype(method).toBuilder().build();
        try {
            request = request.getParserForType().parseFrom(args);//反序列化操作
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        /**
         * rpc对象：service
         * rpc对象的方法：method
         * rpc方法的参数：request
         * 根据method.getName() => login
         **/
        service.callMethod(method,null, request,
                response -> responsebuflocal.set(response.toByteArray())
        );

        return responsebuflocal.get();
    }

    /**
     * @Description: 封装RpcProvider对象创建的细节
     * @Param:
     * @Author: Mr.Bin
     * @Date: 18:58 2022/3/7
     * @return:
     **/
    public static class Builder{
        private static RpcProvider INSTANCE = new RpcProvider();



        /**
         * @Description:从配置文件中读取rpc server的ip和port，给INSTANCE对象初始化数据
         * 通过builder创建一个RpcProvider对象
         * @Param: []
         * @Author: Mr.Bin
         * @Date: 19:00 2022/3/7
         * @return: com.fixbug.provider.RpcProvider
         **/
        public RpcProvider build(String file){
            Properties pro = new Properties();
            try {
                pro.load(Builder.class.getClassLoader().getResourceAsStream(file));
                INSTANCE.setServerIP(pro.getProperty(SERVER_IP));
                INSTANCE.setServerPort(Integer.parseInt(pro.getProperty(SERVER_PORT)));
                INSTANCE.setZkServer(pro.getProperty(ZK_SERVER));
                return INSTANCE;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    /**
     * @Description: 返回一个对象建造器
     * @Param:
     * @Author: Mr.Bin
     * @Date: 19:02 2022/3/7
     * @return: com.fixbug.provider.RpcProvider.Builder
     **/
    public static Builder newBuilder(){
        return new Builder();
    }

    private RpcProvider(){
        this.serviceMap = new HashMap<>();
        this.responsebuflocal = new ThreadLocal<>();
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setZkServer(String zkServer) {
        this.zkServer = zkServer;
    }
}
