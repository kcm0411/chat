package com.example.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker  // WebSocket 메시지 브로커 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 브로커 설정
        // 클라이언트가 구독할 경로 앞에 "/topic"을 붙임
        config.enableSimpleBroker("/topic");

        // 메시지를 보낼 때 "/app"로 시작하는 경로는 @MessageMapping으로 라우팅됨
        config.setApplicationDestinationPrefixes("/app");
    }

//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        // WebSocket 연결을 위한 엔드포인트를 "/ws"로 설정하고, SockJS 사용
////        registry.addEndpoint("/ws").withSockJS();
//        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();  // CORS 허용 및 SockJS 활성화
//    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // allowedOrigins 대신 allowedOriginPatterns 사용
                .withSockJS(); // WebSocket을 지원하지 않는 브라우저에서도 사용할 수 있도록 SockJS 폴백을 활성화
    }
}