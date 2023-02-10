package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.Bloco;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.poli.csi.tapw.pilacoin.util.WebSocketClient;
import br.ufsm.poli.csi.tapw.pilacoin.util.WebSocketClientBloco;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

//@Service
public class BlocoService {

    private static String enderecoServer = "srv-ceesp.proj.ufsm.br:8097";

    @PostConstruct
    void init() {

    }

    @SneakyThrows
    public String buscaBloco(BigInteger dificuldade) {

        System.out.println("Minerando Bloco..............................");
        WebSocketClientBloco webSocketClient = new WebSocketClientBloco();
        Bloco bloco = webSocketClient.getBloco();
        if ( bloco != null ) {
            return validaBlocoMinerar(bloco, dificuldade);
        } else {
            return "BLOCO VAZIO";
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
    public static String validaBlocoMinerar(Bloco bloco, BigInteger DIFICULDADE){
        BigInteger numHash;
        System.out.println("VALIDANDO BLOCO");
        File f2 = new File("src/main/resources/chavePublica.key");
        FileInputStream fin2 = new FileInputStream(f2);
        byte[] barray2 = new byte[(int) fin2.getChannel().size()];
        fin2.read(barray2);
        fin2.close();
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(barray2));
        do {
            SecureRandom rnd = new SecureRandom();
            bloco.setChaveUsuarioMinerador(publicKey.getEncoded());
            bloco.setNonce(new BigInteger(128, rnd).toString());
            numHash = getHash(bloco);
        } while (numHash.compareTo(DIFICULDADE) > 0);
        Bloco blocoValidado = enviaValidacaoBlocoMinerado(bloco);
        if (blocoValidado != null){
            return blocoValidado.toString();
        } else {
            return "Erro ao validar Bloco";
        }
    }

    @SneakyThrows
    public static Bloco enviaValidacaoBlocoMinerado(Bloco bloco) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Bloco> entity = new HttpEntity<>(bloco, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Bloco> resp = restTemplate.postForEntity("http://" + enderecoServer + "/bloco/", entity, Bloco.class);
            System.out.println("Código = "+resp.getStatusCode());
            System.out.println("Mensagem = "+resp.getBody());
            return resp.getBody();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
