package com.sso.users.domain.port.out;

import java.util.Map;

public interface EventPublisherPort {
    void publish(String topic, String eventType, Map<String, Object> payload);
}
