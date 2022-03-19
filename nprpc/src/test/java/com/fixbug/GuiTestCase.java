package com.fixbug;

/**事件回调操作
 * @description:模拟界面类   接收用户发起的事件， 事件处理完成，显示结果
 * 需求：
 * 1. 下载完成后，需要显示信息
 * 2.下载过程中，需要显示下载进度
 * @author: Mr.Bin
 * @time: 2022/3/7 13:31
 */
public class GuiTestCase {

    private DownLoad downLoad;

    public GuiTestCase(DownLoad downLoad) {
        this.downLoad = downLoad;
    }

    /**
     * @description: 下载文件
     * @return: void
     * @author: Mr.Bin
     * @time: 2022/3/7 13:36
     * @param file
     */
    public void downLoadFile(String file){
        System.out.println("Begin start download file:" + file);
        downLoad.start(file);

    }

}

/**
 * @description: 负责下载内容的类
 * @return:  
 * @author: Mr.Bin
 * @time: 2022/3/7 13:40
 */
class DownLoad{
    
    /**
     * @Description: 底层执行下载任务的方法
     * @Param: [file]
     * @Author: Mr.Bin
     * @Date: 14:00 2022/3/7
     * @return: void
     **/
    public void start(String file){

    }

}