package br.ufsm.poli.csi.tapw.pilacoin.util;

import br.ufsm.poli.csi.tapw.pilacoin.model.Bloco;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoinOutroUsuario;
import br.ufsm.poli.csi.tapw.pilacoin.repository.PilaCoinRepository;
import br.ufsm.poli.csi.tapw.pilacoin.service.BlocoService;
import br.ufsm.poli.csi.tapw.pilacoin.service.PilaService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

public class Mineracao {

    //private static String enderecoServer = "192.168.81.101:8080";
    private static String enderecoServer = "srv-ceesp.proj.ufsm.br:8097";

    @SneakyThrows
    private static void guardaPilaCoinMinerado(PilaCoin pilaCoin){
        File f = new File("src/main/resources/pilaCoinMinerados.txt");
        FileWriter fout = new FileWriter(f, true);
        String pilaCoinString = pilaCoin.toString();
        fout.write(pilaCoinString);
        fout.close();
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

    /*
    @EnableAsync
    @EnableTransactionManagement
    @EnableJpaRepositories
    */
    @SneakyThrows
    public static void main(String[] args){

        CadastrarUsuarioProfessor cadastrarUsuarioProfessor = new CadastrarUsuarioProfessor();
        cadastrarUsuarioProfessor.init();
        WebSocketClient webSocketClient = new WebSocketClient();
        WebSocketClientBloco webSocketClientBloco = new WebSocketClientBloco();
        webSocketClient.init();
        webSocketClientBloco.init();
        BigInteger DIFICULDADE;
        PilaCoin pilaCoinValidar;
        Bloco blocoMinerar;

        PilaCoinRepository pilaCoinRepository;

        File f = new File("src/main/resources/chavePublica.key");

        FileInputStream fin = new FileInputStream(f);
        byte[] barray = new byte[(int) fin.getChannel().size()];
        fin.read(barray);
        fin.close();
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(barray));
        long n = 0L;
        BigInteger numHash;
        PilaCoin pilaCoin;
        while(true) {
            do {
                DIFICULDADE = webSocketClient.getDificuldade();
                while (DIFICULDADE == null){
                    DIFICULDADE = webSocketClient.getDificuldade();
                }
                DIFICULDADE = DIFICULDADE.abs();

                blocoMinerar = webSocketClientBloco.getBloco();
                if ( blocoMinerar != null ) {
                    validaBlocoMinerar(blocoMinerar, DIFICULDADE);
                } else {
                    System.out.println("BLOCO VAZIO");
                }

                pilaCoinValidar = webSocketClient.getPilaCoinOutroUsuario();
                if ( pilaCoinValidar != null ) {
                    validaPilaMinerado(pilaCoinValidar, DIFICULDADE);
                } else {
                    System.out.println("PILACOIN VAZIO");
                }

                SecureRandom rnd = new SecureRandom();

                pilaCoin = new PilaCoin();
                pilaCoin.setDataCriacao(new Date());
                pilaCoin.setChaveCriador(publicKey.getEncoded());
                pilaCoin.setNonce(new BigInteger(128, rnd).toString());

                numHash = getHash(pilaCoin);
                n++;
            } while (numHash.compareTo(DIFICULDADE) > 0);
            System.out.println("Minerou em " + n + " tentativas!");

            //Enviar para validação o PilaCoin
            //Receber o PilaCoin retornado
            PilaCoin pilaRetornado = enviaValidacao(pilaCoin);
            String resultado = null;

            if (pilaRetornado != null) {
                System.out.println(pilaRetornado.toString());
                resultado = "Funcionou";
                guardaPilaCoinMinerado(pilaRetornado);
            } else {
                resultado = "Não Funcionou";
            }
            System.out.println(resultado);

            n = 0L;
            PilaService pilaService = new PilaService();
            System.out.println(pilaService.buscaPilaOutroUsuario(DIFICULDADE));
            BlocoService blocoService = new BlocoService();
            System.out.println(blocoService.buscaBloco(DIFICULDADE));
        }
    }

    @SneakyThrows
    private static PilaCoin enviaValidacao(PilaCoin pilaCoin) {
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

    @SneakyThrows
    private static Object enviaValidacaoPilaBloco(PilaCoinOutroUsuario pilaCoin) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PilaCoinOutroUsuario> entity = new HttpEntity<>(pilaCoin, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> resp = restTemplate.postForEntity("http://" + enderecoServer + "/pilacoin/validaPilaOutroUsuario/", entity, String.class);
            System.out.println("Código = "+resp.getStatusCode());
            System.out.println("Mensagem = "+resp.getBody());
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @SneakyThrows
    public static void validaPilaMinerado(PilaCoin pilaCoinValidar, BigInteger DIFICULDADE){
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
            System.out.println("CHAVE PRIVADA = "+Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            Cipher cipherRSA = Cipher.getInstance("RSA");
            cipherRSA.init(Cipher.ENCRYPT_MODE, privateKey);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String str = objectMapper.writeValueAsString(pilaValidado);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(str.getBytes(StandardCharsets.UTF_8));
            byte[] assinatura = cipherRSA.doFinal(hash);
            pilaValidado.setAssinatura(assinatura);
            enviaValidacaoPilaBloco(pilaValidado);
        } else {
            System.out.println("PilaCoin de outro usuário INVÁLIDO");
        }
    }

    @SneakyThrows
    public static void validaBlocoMinerar(Bloco bloco, BigInteger DIFICULDADE){
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
    }

    @SneakyThrows
    private static Bloco enviaValidacaoBlocoMinerado(Bloco bloco) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Bloco> entity = new HttpEntity<>(bloco, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Bloco> resp = restTemplate.postForEntity("http://" + enderecoServer + "/bloco/", entity, Bloco.class);
            System.out.println("Código = "+resp.getStatusCode());
            System.out.println("Mensagem = "+resp.getBody());
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
