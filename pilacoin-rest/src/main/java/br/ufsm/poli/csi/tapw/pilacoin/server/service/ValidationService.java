package br.ufsm.poli.csi.tapw.pilacoin.server.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

@Service
public class ValidationService {

    public static void main(String[] args) throws JsonProcessingException, NoSuchAlgorithmException {
        ObjectMapper mapper = new ObjectMapper();
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        PilaCoin pilaCoin = new PilaCoin();
        pilaCoin.setDataCriacao(new Date());
        //pilaCoin.setIdCriador("professor");
        pilaCoin.setChaveCriador(kp.getPublic().getEncoded());
        pilaCoin.setNonce(new BigInteger(128, new SecureRandom()).toString());
        pilaCoin.setAssinaturaMaster("fdgfdsgfdsgfdsgdsfgdsf".getBytes(StandardCharsets.UTF_8));
        System.out.println(mapper.writeValueAsString(pilaCoin));
    }

}
