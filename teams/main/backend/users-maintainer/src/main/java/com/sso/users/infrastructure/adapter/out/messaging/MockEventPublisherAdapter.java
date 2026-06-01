package com.sso.users.infrastructure.adapter.out.messaging;

import com.sso.users.domain.port.out.EventPublisherPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "aws.sns.enabled", havingValue = "false", matchIfMissing = true)
public class MockEventPublisherAdapter implements EventPublisherPort {

    @Override
    public void publish(String topic, String eventType, Map<String, Object> payload) {
        System.out.println("---------------------------------------------------------");
        System.out.println("[MOCK EVENT PUBLISHER]");
        System.out.println("Topic: " + topic);
        System.out.println("Event: " + eventType);
        System.out.println("Payload: " + payload);
        System.out.println("---------------------------------------------------------");
    }
}
