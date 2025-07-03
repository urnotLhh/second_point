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

}
