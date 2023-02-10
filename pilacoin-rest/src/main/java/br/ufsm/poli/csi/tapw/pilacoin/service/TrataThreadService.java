package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.Bloco;
import br.ufsm.poli.csi.tapw.pilacoin.model.Log;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoinOutroUsuario;
import br.ufsm.poli.csi.tapw.pilacoin.util.WebSocketClient;
import br.ufsm.poli.csi.tapw.pilacoin.util.WebSocketClientBloco;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Service
public class TrataThreadService {

    private static final Logger logger = LoggerFactory.getLogger(TrataThreadService.class);
    private static final Thread[] threads = new Thread[5];
    private static final boolean[] isRunning = new boolean[5];
    static WebSocketClientBloco webSocketClientBloco;
    static WebSocketClient webSocketClient;
    private static PilaService pilaService;
    private static LogService logService;

    @Autowired
    public TrataThreadService(PilaService pilaService, LogService logService) {
        webSocketClient = new WebSocketClient();
        webSocketClientBloco = new WebSocketClientBloco();
        this.pilaService = pilaService;
        this.logService = logService;
    }

    public static BigInteger getHash(Object o) throws JsonProcessingException, NoSuchAlgorithmException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String str = objectMapper.writeValueAsString(o);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(str.getBytes(StandardCharsets.UTF_8));
        return new BigInteger(hash).abs();
    }

    @Service
    private static class MineradorPila implements Runnable{

        @Override
        public void run() {
            BigInteger numHash = null;
            PilaCoin pilaCoinMinerado;
            webSocketClient.init();
            BigInteger dificuldade = webSocketClient.getDificuldade();
            while (dificuldade == null) {
                dificuldade = webSocketClient.getDificuldade();
            }
            while (threads[0].isAlive()) {
                synchronized (this) {
                    if (!isRunning[0]) {
                        try {
                            wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
                File f = new File("src/main/resources/chavePublica.key");

                FileInputStream fin = null;
                PublicKey publicKey = null;
                try {
                    fin = new FileInputStream(f);
                    byte[] barray = new byte[0];
                    barray = new byte[(int) fin.getChannel().size()];
                    fin.read(barray);
                    fin.close();
                    publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(barray));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                do {
                    Random rnd = new SecureRandom();
                    BigInteger magicNumber = new BigInteger(128, rnd).abs();

                    pilaCoinMinerado = new PilaCoin();
                    pilaCoinMinerado.setDataCriacao(new Date());
                    pilaCoinMinerado.setNonce(magicNumber.toString());
                    pilaCoinMinerado.setChaveCriador(publicKey.getEncoded());

                    try {
                        numHash = TrataThreadService.getHash(pilaCoinMinerado);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                } while (Objects.requireNonNull(numHash).compareTo(dificuldade) > 0);

                logger.info("PilaCoin minerado: {}", pilaCoinMinerado.getNonce());
                logService.createLog(new Log("PilaCoin minerado: {"+pilaCoinMinerado.getNonce()+"}"));
                System.out.println("PilaCoin minerado: {"+pilaCoinMinerado.getNonce()+"}");
                PilaCoin pilaCoin = pilaService.enviaValidacao(pilaCoinMinerado);
                pilaService.createPila(pilaCoin);
            }
        }
    }

    @Service
    private static class MineradorBloco implements Runnable {

        @Override
        public void run() {
            BigInteger numHash = null;
            Bloco blocoMinerar;
            BigInteger dificuldade = webSocketClient.getDificuldade();
            while (dificuldade == null){
                dificuldade = webSocketClient.getDificuldade();
            }
            dificuldade = dificuldade.abs();
            while (threads[2].isAlive()) {
                synchronized (this) {
                    if (!isRunning[2]) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                File f = new File("src/main/resources/chavePublica.key");
                PublicKey publicKey = null;
                try {
                    FileInputStream fin = new FileInputStream(f);
                    byte[] barray = new byte[(int) fin.getChannel().size()];
                    fin.read(barray);
                    fin.close();
                    publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(barray));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                do {
                    Random rnd = new SecureRandom();
                    BigInteger nonce = new BigInteger(128, rnd).abs();

                    blocoMinerar = webSocketClientBloco.getBloco();
                    blocoMinerar.setNonce(nonce.toString());
                    blocoMinerar.setChaveUsuarioMinerador(publicKey.getEncoded());

                    try {
                        numHash = TrataThreadService.getHash(blocoMinerar);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                } while (Objects.requireNonNull(numHash).compareTo(dificuldade) > 0);

                logger.info("Bloco minerado: {}", blocoMinerar.getNonce());
                logService.createLog(new Log("Bloco minerado: {"+blocoMinerar.getNonce()+"}"));
                BlocoService.enviaValidacaoBlocoMinerado(blocoMinerar);
            }
        }
    }

    @Service
    private static class ValidadorPila implements Runnable {

        @Override
        public void run() {
            BigInteger numHash = null;
            BigInteger dificuldade = webSocketClient.getDificuldade();
            while (dificuldade == null){
                dificuldade = webSocketClient.getDificuldade();
            }
            dificuldade = dificuldade.abs();
            while (threads[1].isAlive()) {
                synchronized (this) {
                    if (!isRunning[1]) {
                        try {
                            wait();
                        } catch (InterruptedException ignored) { }
                    }
                }

                File f = new File("src/main/resources/chavePublica.key");
                PublicKey publicKey = null;
                PrivateKey privateKey = null;
                try {
                    FileInputStream fin = new FileInputStream(f);
                    byte[] barray = new byte[(int) fin.getChannel().size()];
                    fin.read(barray);
                    fin.close();
                    publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(barray));

                    File f3 = new File("src/main/resources/chavePrivada.key");
                    FileInputStream fin3 = new FileInputStream(f3);
                    byte[] barray3 = new byte[(int) fin3.getChannel().size()];
                    fin3.read(barray3);
                    fin3.close();
                    privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(barray3));
                } catch (Exception e){
                    e.printStackTrace();
                }
                PilaCoin pilaNaoValidado = webSocketClient.getPilaCoinOutroUsuario();

                if (pilaNaoValidado != null) {
                    try {
                        numHash = getHash(pilaNaoValidado);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    if (Objects.requireNonNull(numHash).compareTo(dificuldade) <= 0) {
                        logger.info("Pila válido!");
                        logService.createLog(new Log("PilaCoin de outro Usuário validado: {"+pilaNaoValidado.getNonce()+"}"));
                        PilaCoinOutroUsuario pilaValidado = new PilaCoinOutroUsuario();
                        pilaValidado.setChavePublica(publicKey.getEncoded());
                        pilaValidado.setNonce(pilaNaoValidado.getNonce());
                        pilaValidado.setTipo("PILA");
                        pilaValidado.setHashPilaBloco(null);
                        try {
                            Cipher cipherRSA = Cipher.getInstance("RSA");
                            cipherRSA.init(Cipher.ENCRYPT_MODE, privateKey);
                            byte[] assinatura = cipherRSA.doFinal(getHash(pilaValidado).toByteArray());
                            pilaValidado.setAssinatura(assinatura);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        PilaService.enviaValidacaoPilaBloco(pilaValidado);
                    } else {
                        logService.createLog(new Log("PilaCoin de outro Usuário inválido: {"+pilaNaoValidado.getNonce()+"}"));
                        logger.warn("Pila inválido!");
                    }
                }
            }
        }
    }

    @Service
    private static class ValidadorBloco implements Runnable {
        @Override
        public void run() {
            while (threads[3].isAlive()) {
                synchronized (this) {
                    if (!isRunning[3]) {
                        try {
                            wait();
                        } catch (InterruptedException ignored) { }
                    }
                }
            }

            // TODO
        }
    }

    public void iniciar(int numThread) {
        isRunning[numThread] = true;
        Runnable runnable = null;
        String mensagem = "";

        switch (numThread) {
            case 0 -> {
                runnable = new MineradorPila();
                mensagem = "Mineração de pilas iniciada!";
            }
            case 1 -> {
                runnable = new ValidadorPila();
                mensagem = "Validação de pilas iniciada";
            }
            case 2 -> {
                runnable = new MineradorBloco();
                mensagem = "Mineração de blocos iniciada!";
            }
            case 3 -> {
                runnable = new ValidadorBloco();
                mensagem = "Validação de blocos iniciada!";
            }
        }

        if (threads[numThread] == null) {
            threads[numThread] = new Thread(runnable);
            threads[numThread].start();
        } else {
            synchronized (this) {
                notify();
            }
        }

        log(mensagem);
    }

    public void pausar(int numThread) {
        isRunning[numThread] = false;
        String mensagem = "";

        switch (numThread) {
            case 0 -> mensagem = "Mineração de pilas pausada!";
            case 1 -> mensagem = "Validação de pilas pausada";
            case 2 -> mensagem = "Mineração de blocos pausada!";
            case 3 -> mensagem = "Validação de blocos pausada!";
        }

        log(mensagem);
    }

    private void log(String mensagem) {
        logger.info(mensagem);
    }

}
