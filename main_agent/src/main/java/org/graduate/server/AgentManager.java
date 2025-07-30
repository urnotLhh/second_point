package org.graduate.server;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class AgentManager {

    @Autowired
    BaiduLogoServer baiduLogoServer;
    
    private static final String LABEL_PATH = "src/main/resources/lable.txt";
    private static final String NORMAL_LABEL_PATH = "src/main/resources/lable_normal.txt";
    private static final String ABNORMAL_LABEL_PATH = "src/main/resources/lable_abnormal.txt";

    @Tool(description = "识别图片上的logo所属厂商")
    String getPictureType(String filePath) throws IOException {
        return baiduLogoServer.getLogoOwner(filePath);
    }

    @Tool(description = "获取lable.txt中的设备类型标签，包含类型，厂商和型号")
    String getIoTLables(int lineIndex) {
        try {
            File file = new File(LABEL_PATH);
            if (!file.exists()) {
                return "文件不存在: " + LABEL_PATH;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int currentLine = 0;
                
                while ((line = reader.readLine()) != null) {
                    currentLine++;
                    if (currentLine == lineIndex) {
                        return line;
                    }
                }
                
                return "行号超出范围，文件总行数: " + currentLine;
            }
        } catch (IOException e) {
            return "读取文件时出错: " + e.getMessage();
        }
    }

    @Tool(description = "将物联网设备类型的合理标签存储到本地")
    void storgeValidLables(List<String> lables) {
        try {
            File file = new File(NORMAL_LABEL_PATH);
            // 创建文件父目录(如果不存在)
            file.getParentFile().mkdirs();
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                for (String label : lables) {
                    writer.write(label);
                    writer.newLine();
                }
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Tool(description = "将不合理的物联网设备类型的标签存储到本地")
    void storgeInvalidLables(List<String> lables) {
        try {
            File file = new File(ABNORMAL_LABEL_PATH);
            // 创建文件父目录(如果不存在)
            file.getParentFile().mkdirs();
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                for (String label : lables) {
                    writer.write(label);
                    writer.newLine();
                }
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
