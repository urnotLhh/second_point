package org.graduate.controller;

import org.graduate.server.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;
    
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String response = chatService.processMessage(request.getMessage());
        return new ChatResponse(response);
    }
    
    // 请求和响应的数据类
    public static class ChatRequest {
        private String message;
        
        public ChatRequest() {}
        
        public ChatRequest(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    public static class ChatResponse {
        private String response;
        
        public ChatResponse() {}
        
        public ChatResponse(String response) {
            this.response = response;
        }
        
        public String getResponse() {
            return response;
        }
        
        public void setResponse(String response) {
            this.response = response;
        }
    }
} 