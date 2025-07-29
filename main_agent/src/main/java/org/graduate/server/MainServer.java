package org.graduate.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.graduate.server.RagAgentServer;  // 确保包路径与实际定义一致

@Slf4j
@Component
public class MainServer {
    @Autowired
    RagAgentServer ragAgentServerImpl;

    public String getRagAgentServer(String message) {
        try {
            return ragAgentServerImpl.getAnswerFromRagAgent(message);
        } catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
