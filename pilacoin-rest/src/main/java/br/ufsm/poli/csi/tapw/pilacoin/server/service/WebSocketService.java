package br.ufsm.poli.csi.tapw.pilacoin.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Scheduled(fixedRate = 30000)
    private void enviaMsgDificuldade() {
        //    simpMessagingTemplate.convertAndSend("/topic/dificuldade", );
    }


    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        System.out.println("-------- WS Conectado -------------");
        //System.out.println(event);
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        //System.out.println("-------- Topico Conectado -------------");
        Message<byte[]> message = event.getMessage();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        if (command.equals(StompCommand.SUBSCRIBE)) {
            System.out.println("------------- Topico Inscrito ----------------");
            String sessionId = accessor.getSessionId();
            String stompSubscriptionId = accessor.getSubscriptionId();
            String destination = accessor.getDestination();
            System.out.println("sessionid= " + sessionId + ", stomSubsID= " + stompSubscriptionId + ", destination= " + destination);
        }
    }

}
