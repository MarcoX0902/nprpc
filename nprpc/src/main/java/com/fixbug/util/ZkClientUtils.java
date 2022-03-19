package com.fixbug.util;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:和zookeeper通信用的辅助工具类
 * @author: Mr.Bin
 * @time: 2022/3/18 21:03
 */
public class ZkClientUtils {
    private static String rootPath = "/nprpc";

    private ZkClient zkClient;
    private Map<String,String> ephemeralMap = new HashMap<>();

    /**
     * @Description: 通过zk server字符串信息连接zkServer
     * @Param: [severList]
     * @Author: Mr.Bin
     * @Date: 21:38 2022/3/18
     * @return:
     **/
    public ZkClientUtils(String severList){
        this.zkClient = new ZkClient(severList, 3000);

        //如果root节点不存在，创建
        if(!this.zkClient.exists(rootPath)){
            this.zkClient.createPersistent(rootPath, null);
        }
    }
     /**
      * @Description: 关闭和zkServer的连接
      * @Param: []
      * @Author: Mr.Bin
      * @Date: 21:40 2022/3/18
      * @return: void
      **/
    public void close(){
        this.zkClient.close();
    }

    /**
     * @Description: zk上创建临时性节点
     * @Param: [path, data]
     * @Author: Mr.Bin
     * @Date: 21:42 2022/3/18
     * @return: void
     **/
    public void createEphemeral(String path, String data){
        path = rootPath + path;
        ephemeralMap.put(path,data);
        if(!this.zkClient.exists(path)){//znode节点不存在，才创建
            this.zkClient.createEphemeral(path,data);

        }

    }

    /**
     * @Description: zk上创建永久性节点
     * @Param: [path, data]
     * @Author: Mr.Bin
     * @Date: 21:49 2022/3/18
     * @return: void
     **/
    public void createPersistent(String path, String data){
        path = rootPath + path;
        if(!this.zkClient.exists(path)){//znode节点不存在，才创建
            this.zkClient.createPersistent(path, data);
        }
    }

    /**
     * @Description: 读取znode节点的值
     * @Param: [path] 
     * @Author: Mr.Bin 
     * @Date: 21:52 2022/3/18
     * @return: java.lang.String
     **/
    public String readData(String path){
        return this.zkClient.readData(rootPath + path,null);
    }

    /**
     * @Description: 给zk上指定的znode添加watcher监听
     * @Param: [path]
     * @Author: Mr.Bin
     * @Date: 13:48 2022/3/19
     * @return: void
     **/
    public void addWatcher(String path){
        this.zkClient.subscribeDataChanges(rootPath + path, new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            /**
             * @Description: 一定要设置znode节点监听，因为如果zkclient断掉，由于zksever无法及时获知zkclient的关闭状态，
             * 所以zksever会等待session timeout时间以后，会把zkclient创建的临时节点全部删除掉，但是
             * 如果在session timeout时间内，又启动了同样的zkclient，那么等待session timeout时间超时以后
             * 原先创建的临时节点都没了
             * @Param: [path]
             * @Author: Mr.Bin
             * @Date: 13:49 2022/3/19
             * @return: void
             **/
            @Override
            public void handleDataDeleted(String path) throws Exception {
                System.out.println("watcher -> handleDataDeleted: " + path);

                //把删除掉的znode临时节点重新创建一下
                String str = ephemeralMap.get(path);
                if(str != null){
                    zkClient.createEphemeral(path,str);
                }
            }
        });
    }

    public static String getRootPath() {
        return rootPath;
    }

    public static void setRootPath(String rootPath) {
        ZkClientUtils.rootPath = rootPath;
    }

    /**
     * @Description: 测试zkclient工具类
     * @Param: [args]
     * @Author: Mr.Bin
     * @Date: 21:53 2022/3/18
     * @return: void
     **/
    public static void main(String[] args){
        ZkClientUtils zk = new ZkClientUtils("127.0.0.1:2181");
        zk.createPersistent("/ProductService","123456");
        System.out.println(zk.readData("/ProductService"));
        zk.close();
    }
}
