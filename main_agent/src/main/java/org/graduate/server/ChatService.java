package org.graduate.server;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.tool.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private AgentManager agentManager;
    
    @Autowired
    private ToolService toolService;
    
    /**
     * 初始化聊天服务，注册工具
     */
    public void init() {
        // 注册AgentManager中的工具方法
        toolService.registerTool(agentManager);
    }
    
    /**
     * 处理用户消息，支持工具调用
     * @param userMessage 用户输入的消息
     * @return AI的回复
     */
    public String processMessage(String userMessage) {
        // 系统提示，指导AI使用工具
        String systemPrompt = """
            你是一个物联网设备识别助手，可以帮助用户处理设备标签。
            你可以使用以下工具：
            1. 读取标签文件中的特定行
            2. 将合理的标签存储到正常标签文件
            3. 将不合理的标签存储到异常标签文件
            4. 识别图片中的logo所属厂商
            
            当用户要求你处理标签时，请使用适当的工具来完成任务。
            """;
        
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPrompt);
        Message systemMessage = systemPromptTemplate.createMessage();
        
        // 创建用户消息
        UserMessage userMsg = new UserMessage(userMessage);
        
        // 创建提示，包含系统消息和用户消息
        Prompt prompt = new Prompt(List.of(systemMessage, userMsg));
        
        // 发送到AI并获取响应
        ChatResponse response = chatClient.call(prompt);
        
        // 返回AI的回复文本
        return response.getResult().getOutput().getContent();
    }
} 