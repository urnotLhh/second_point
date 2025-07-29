package org.graduate.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
