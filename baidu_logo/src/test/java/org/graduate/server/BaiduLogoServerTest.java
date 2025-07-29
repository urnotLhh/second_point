package org.graduate.server;
import java.io.IOException;

public class BaiduLogoServerTest {

    public static void main(String[] args) throws IOException {
        BaiduLogoServer baiduLogoServer = new BaiduLogoServer();

        // 使用当前类的类加载器获取资源路径
        String resourcePath2 = "src/main/resources/index_pic/hikvision/5.188.133.165.png";
        baiduLogoServer.getLogoOwner(resourcePath2);
    }
}
