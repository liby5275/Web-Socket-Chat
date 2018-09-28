package com.example.eventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.model.ChatMessage;
import com.example.model.ChatMessage.MessageType;

@Component
public class WebSocketEventListener {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
	
	@Autowired
	private SimpMessageSendingOperations template;
	
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		logger.info("new session has been established");
	}
	
	
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		
		StompHeaderAccessor header =  StompHeaderAccessor.wrap(event.getMessage());
		
		String username = (String)header.getSessionAttributes().get("username");
		
		if(null != username) {
			logger.info("user disconnected" + username);
			
			ChatMessage message = new ChatMessage();
			message.setType(MessageType.LEAVE);
			message.setSender(username);
			
			template.convertAndSend("/topic/public", message);
		}
		
	}

}
