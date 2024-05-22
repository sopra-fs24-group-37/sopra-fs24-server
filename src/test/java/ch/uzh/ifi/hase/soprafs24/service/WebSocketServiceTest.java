package ch.uzh.ifi.hase.soprafs24.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WebSocketServiceTest {

    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private Logger log;

    @InjectMocks
    private WebSocketService webSocketService;

    @Captor
    private ArgumentCaptor<String> mappingCaptor;

    @Captor
    private ArgumentCaptor<Object> objectCaptor;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Inject mock Logger into WebSocketService using reflection
        Field logField = WebSocketService.class.getDeclaredField("log");
        logField.setAccessible(true);
        logField.set(webSocketService, log);
    }

    @Test
    public void sendMessageToSubscribers_success() {
        String mapping = "/topic/test";
        String message = "Test Message";

        webSocketService.sendMessageToSubscribers(mapping, message);

        verify(log, times(1)).info("Sending {} to {}", message, mapping);
        verify(simpMessagingTemplate, times(1)).convertAndSend(mappingCaptor.capture(), objectCaptor.capture());

        assertEquals(mapping, mappingCaptor.getValue());
        assertEquals(message, objectCaptor.getValue());
    }

    @Test
    public void sendMessageToSubscribers_withDifferentMessage() {
        String mapping = "/topic/anotherTest";
        String message = "Another Test Message";

        webSocketService.sendMessageToSubscribers(mapping, message);

        verify(log, times(1)).info("Sending {} to {}", message, mapping);
        verify(simpMessagingTemplate, times(1)).convertAndSend(mappingCaptor.capture(), objectCaptor.capture());

        assertEquals(mapping, mappingCaptor.getValue());
        assertEquals(message, objectCaptor.getValue());
    }

    @Test
    public void sendMessageToSubscriberswithoutLog_success() {
        String mapping = "/topic/test";
        String message = "Test Message";

        webSocketService.sendMessageToSubscriberswithoutLog(mapping, message);

        verify(simpMessagingTemplate, times(1)).convertAndSend(mappingCaptor.capture(), objectCaptor.capture());

        assertEquals(mapping, mappingCaptor.getValue());
        assertEquals(message, objectCaptor.getValue());
    }

    @Test
    public void sendMessageToSubscriberswithoutLog_withDifferentMessage() {
        String mapping = "/topic/anotherTest";
        String message = "Another Test Message";

        webSocketService.sendMessageToSubscriberswithoutLog(mapping, message);

        verify(simpMessagingTemplate, times(1)).convertAndSend(mappingCaptor.capture(), objectCaptor.capture());

        assertEquals(mapping, mappingCaptor.getValue());
        assertEquals(message, objectCaptor.getValue());
    }
}