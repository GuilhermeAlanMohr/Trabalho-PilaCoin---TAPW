package br.ufsm.poli.csi.tapw.pilacoin.util;

import br.ufsm.poli.csi.tapw.pilacoin.model.Bloco;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import lombok.*;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Objects;

@Service
public class WebSocketClientBloco {

    private WebSocketClientBloco.MyStompSessionHandlerBloco sessionHandler =
            new WebSocketClientBloco.MyStompSessionHandlerBloco();
    private String enderecoServer = "srv-ceesp.proj.ufsm.br:8097";

    @PostConstruct
    public void init() {
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.connect("ws://" + enderecoServer + "/websocket/websocket", sessionHandler);
    }

    @Scheduled(fixedRate = 8000)
    private void printBloco() {
        if (sessionHandler.bloco != null) {
            System.out.println("Bloco Atual: " + sessionHandler.bloco);
        }
    }


    public Bloco getBloco() {
        //System.out.println("Chamou");
        while (sessionHandler.getBloco() == null) {
            Thread.onSpinWait();
        }
        //System.out.println("BLOCO RECEBIDO");
        return sessionHandler.getBloco();
    }

    @Data
    public static class MyStompSessionHandlerBloco implements StompSessionHandler {

        @Getter
        private volatile Bloco bloco;

        @Override
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            stompSession.subscribe("/topic/descobrirNovoBloco", this);
        }

        @Override
        public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
        }

        @Override
        public void handleTransportError(StompSession stompSession, Throwable throwable) {
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            if (Objects.equals(stompHeaders.getDestination(), "/topic/descobrirNovoBloco")) {
                return Bloco.class;
            }
            return null;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            //System.out.println("Received : " + o);
            assert o != null;
            bloco = (Bloco) o;

        }
    }

}
