package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoinOutroUsuario;
import br.ufsm.poli.csi.tapw.pilacoin.util.WebSocketClient;
import br.ufsm.poli.csi.tapw.pilacoin.util.WebSocketClientBloco;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class PilaService {

    private static String enderecoServer = "srv-ceesp.proj.ufsm.br:8097";

    @PostConstruct
    void init() {


    }

    @SneakyThrows
    public String buscaPilaOutroUsuario (BigInteger dificuldade) {
        WebSocketClient webSocketClient = new WebSocketClient();
        PilaCoin pilaCoinValidar = webSocketClient.getPilaCoinOutroUsuario();
        if ( pilaCoinValidar != null ) {
            return validaPilaMinerado(pilaCoinValidar, dificuldade);
        } else {
            return "PILACOIN VAZIO";
        }
    }

    @SneakyThrows
    public static String validaPilaMinerado(PilaCoin pilaCoinValidar, BigInteger DIFICULDADE){
        BigInteger numHash = getHash(pilaCoinValidar);
        if (numHash.compareTo(DIFICULDADE) < 0) {
            System.out.println("PilaCoin de outro usuário válido");

            File f2 = new File("src/main/resources/chavePublica.key");
            FileInputStream fin2 = new FileInputStream(f2);
            byte[] barray2 = new byte[(int) fin2.getChannel().size()];
            fin2.read(barray2);
            fin2.close();

            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(barray2));

            File f3 = new File("src/main/resources/chavePrivada.key");
            FileInputStream fin3 = new FileInputStream(f3);
            byte[] barray3 = new byte[(int) fin3.getChannel().size()];
            fin3.read(barray3);
            fin3.close();

            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(barray3));

            PilaCoinOutroUsuario pilaValidado = PilaCoinOutroUsuario.builder()
                    .chavePublica(publicKey.getEncoded())
                    .nonce(pilaCoinValidar.getNonce())
                    .hashPilaBloco(numHash.toByteArray())
                    //.assinatura(assinatura)
                    .tipo("PILA").build();
            System.out.println("CHAVE PRIVADA = "+ Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            Cipher cipherRSA = Cipher.getInstance("RSA");
            cipherRSA.init(Cipher.ENCRYPT_MODE, privateKey);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String str = objectMapper.writeValueAsString(pilaValidado);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(str.getBytes(StandardCharsets.UTF_8));
            byte[] assinatura = cipherRSA.doFinal(hash);
            pilaValidado.setAssinatura(assinatura);
            return enviaValidacaoPilaBloco(pilaValidado);
        } else {
            return "PilaCoin de outro usuário INVÁLIDO";
        }
    }

    @SneakyThrows
    public static String enviaValidacaoPilaBloco(PilaCoinOutroUsuario pilaCoin) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PilaCoinOutroUsuario> entity = new HttpEntity<>(pilaCoin, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> resp = restTemplate.postForEntity("http://" + enderecoServer + "/pilacoin/validaPilaOutroUsuario/", entity, String.class);
            System.out.println("Código = "+resp.getStatusCode());
            System.out.println("Mensagem = "+resp.getBody());
            return "Código: "+ resp.getStatusCode()+", Mensagem: "+resp.getBody();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Mensagem: "+e.getMessage();
        }
    }

    @SneakyThrows
    private static BigInteger getHash(Object o) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String str = objectMapper.writeValueAsString(o);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(str.getBytes(StandardCharsets.UTF_8));
        return new BigInteger(hash).abs();
    }

    @SneakyThrows
    public static PilaCoin enviaValidacao(PilaCoin pilaCoin) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PilaCoin> entity = new HttpEntity<>(pilaCoin, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            //System.out.println("http://" + enderecoServer + "/pilacoin/");
            ResponseEntity<PilaCoin> resp = restTemplate.postForEntity("http://" + enderecoServer + "/pilacoin/", entity, PilaCoin.class);
            return resp.getBody();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
