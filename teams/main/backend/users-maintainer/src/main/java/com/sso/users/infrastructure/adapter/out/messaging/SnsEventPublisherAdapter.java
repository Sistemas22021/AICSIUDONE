package com.sso.users.infrastructure.adapter.out.messaging;

import com.sso.users.domain.port.out.EventPublisherPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "aws.sns.enabled", havingValue = "true")
public class SnsEventPublisherAdapter implements EventPublisherPort {

    private final SnsClient snsClient;
    private final String topicArn;

    public SnsEventPublisherAdapter(@Value("${aws.sns.topic.user-events-arn}") String topicArn,
                                    @Value("${aws.sns.region}") String region) {
        this.topicArn = topicArn;
        this.snsClient = SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Override
    public void publish(String topic, String eventType, Map<String, Object> payload) {
        // En una implementación real, convertiríamos payload a JSON con Jackson.
        // Aquí hacemos un mock string simple para el ejemplo.
        String messageBody = "{" +
                "\"eventType\":\"" + eventType + "\"," +
                "\"payload\":\"" + payload.toString() + "\"" +
                "}";

        PublishRequest request = PublishRequest.builder()
                .message(messageBody)
                .topicArn(this.topicArn)
                .build();

        try {
            snsClient.publish(request);
            System.out.println("Message published onto SNS: " + eventType);
        } catch (Exception e) {
            System.err.println("Warning: Failure to publish SNS event (Check local AWS config): " + e.getMessage());
        }
    }
}
