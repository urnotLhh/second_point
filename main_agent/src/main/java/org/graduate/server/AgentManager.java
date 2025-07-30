package org.graduate.server;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class AgentManager {

    @Autowired
    BaiduLogoServer  baiduLogoServer;

    @Tool(description = "识别图片上的logo所属厂商")
    String getPictureType(String filePath) throws IOException {
        return baiduLogoServer.getLogoOwner(filePath);
    }

    @Tool(description = "获取lable.txt中的设备类型标签，包含类型，厂商和型号")
    String getIoTLables(int lineIndex){

    }

    @Tool(description = "将物联网设备类型的合理标签存储到本地")
    void storgeValidLables(List<String> lables){


    }

    @Tool(description = "将不合理的物联网设备类型的签存储到本地")
    void storgeValidLables(List<String> lables){


    }


}
