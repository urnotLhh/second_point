package org.graduate.server;
import java.io.IOException;

public class BaiduLogoServerTest {

    public static void main(String[] args) throws IOException {
        BaiduLogoServer baiduLogoServer = new BaiduLogoServer();

        // 使用当前类的类加载器获取资源路径
        String resourcePath = "index_pic/index_pic1.png";
        String resourcePath2 = "baidu_logo/src/main/resources/index_pic/tp_link.png";
        baiduLogoServer.getLogoOwner(resourcePath2);
    }
}
