package br.ufsm.poli.csi.tapw.pilacoin.util;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoinOutroUsuario;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
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
public class WebSocketClient {

    private MyStompSessionHandler sessionHandler = new MyStompSessionHandler();
    //@Value("${endereco.server}")
    private String enderecoServer = "srv-ceesp.proj.ufsm.br:8097";
    //private String enderecoServer = "192.168.81.101:8080";

    @PostConstruct
    public void init() {
        System.out.println("iniciou");
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.connect("ws://" + enderecoServer + "/websocket/websocket", sessionHandler);
        System.out.println("conectou");
    }

    @Scheduled(fixedRate = 3000)
    private void printDificuldade() {
        if (sessionHandler.dificuldade != null) {
            System.out.println("Dificuldade Atual: " + sessionHandler.dificuldade);
        }
    }

    @Scheduled(fixedRate = 3000)
    private void printPilaCoinOutroUsuario() {
        if (sessionHandler.pilaCoinOutroUsuario != null) {
            System.out.println("PilaCoin de outro usuário para validar: " + sessionHandler.pilaCoinOutroUsuario);
        }
    }

    public BigInteger getDificuldade() {
        if (sessionHandler.dificuldade == null){
            System.out.println("Dificuldade Nula");
        }
        return sessionHandler.getDificuldade();
    }

    public PilaCoin getPilaCoinOutroUsuario() {
        while (sessionHandler.getPilaCoinOutroUsuario() == null) {
            Thread.onSpinWait();
        }
        return sessionHandler.getPilaCoinOutroUsuario();
    }

    @Data
    public static class MyStompSessionHandler implements StompSessionHandler {

        @Getter
        private BigInteger dificuldade;

        @Getter
        private volatile PilaCoin pilaCoinOutroUsuario;

        /*
        @Getter
        private volatile Bloco bloco;
        */

        @Override
        public void afterConnected(StompSession stompSession,
                                   StompHeaders stompHeaders)
        {
            stompSession.subscribe("/topic/dificuldade", this);
            stompSession.subscribe("/topic/validaMineracao", this);
        }

        @Override
        public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
        }

        @Override
        public void handleTransportError(StompSession stompSession, Throwable throwable) {
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            //System.out.println("DESTINO = "+stompHeaders.getDestination());
            if (Objects.equals(stompHeaders.getDestination(), "/topic/dificuldade")) {
                return DificuldadeRet.class;
            } else if (Objects.equals(stompHeaders.getDestination(), "/topic/validaMineracao")) {
                return PilaCoin.class;
            }
            return null;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            //System.out.println("Received : " + o);
            assert o != null;
            if (o.getClass().getSimpleName().equals("PilaCoin")) {
                pilaCoinOutroUsuario = (PilaCoin) o;
                //System.out.println("RECEBEU PILACOIN");
            } else {
                dificuldade = new BigInteger(((DificuldadeRet) o).getDificuldade(), 16);
            }
        }
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DificuldadeRet {
        private String dificuldade;
    }

}
