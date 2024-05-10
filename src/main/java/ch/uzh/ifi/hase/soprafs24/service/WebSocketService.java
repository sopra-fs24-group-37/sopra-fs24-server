package ch.uzh.ifi.hase.soprafs24.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WebSocketService {

    private final Logger log = LoggerFactory.getLogger(WebSocketService.class);

    @Autowired
    protected SimpMessagingTemplate simpMessagingTemplate;

    public void sendMessageToSubscribers(String mapping, Object o) {
        log.info("Sending {} to {}", o, mapping);
        this.simpMessagingTemplate.convertAndSend(mapping, o);
    }
    public void sendMessageToSubscriberswithoutLog(String mapping, Object o) {
        this.simpMessagingTemplate.convertAndSend(mapping, o);
    }
}