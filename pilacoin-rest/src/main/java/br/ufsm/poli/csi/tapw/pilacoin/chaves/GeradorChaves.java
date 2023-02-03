package br.ufsm.poli.csi.tapw.pilacoin.chaves;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class GeradorChaves {

    @SneakyThrows
    public static void main(String[] args) throws IOException {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        File f = new File("src/main/resources/chavePublica.key");
        FileInputStream fin = new FileInputStream(f);
        byte[] barray = new byte[(int) fin.getChannel().size()];
        fin.read(barray);

        FileOutputStream fout = new FileOutputStream(f);
        System.out.println(Base64.getEncoder().encodeToString(kp.getPublic().getEncoded()));
        fout.write(kp.getPublic().getEncoded());
        fin.close();
        fout.close();

        File f2 = new File("src/main/resources/chavePrivada.key");
        FileInputStream fin2 = new FileInputStream(f2);
        byte[] barray2 = new byte[(int) fin2.getChannel().size()];
        fin2.read(barray2);

        FileOutputStream fout2 = new FileOutputStream(f2);
        System.out.println(Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded()));
        fout2.write(kp.getPrivate().getEncoded());
        fin2.close();
        fout2.close();

    }

}
