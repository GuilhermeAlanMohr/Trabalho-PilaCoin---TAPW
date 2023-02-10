package br.ufsm.poli.csi.tapw.pilacoin.util;

import br.ufsm.poli.csi.tapw.pilacoin.model.Usuario;
import br.ufsm.poli.csi.tapw.pilacoin.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class CadastrarUsuarioProfessor {

    //@Value("${endereco.server}")
    private String enderecoServer = "srv-ceesp.proj.ufsm.br:8097";
    //private String enderecoServer = "192.168.81.101:8080";
    private final UsuarioService usuarioService;

    @Autowired
    public CadastrarUsuarioProfessor(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @PostConstruct
    public void init() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        System.out.println("Registrado usuário: ");
        UsuarioRest usuarioRest = registraUsuario("guilherme");
        Usuario usuario = new Usuario();
        usuario.setAutoridade("ADMIN");
        usuario.setNome(usuarioRest.getNome());
        usuario.setId(usuarioRest.getId());
        usuario.setSenha(new BCryptPasswordEncoder().encode("1234"));
        usuario.setEmail("guilherme@gmail.com");
        usuario.setChavePublica(usuarioRest.getChavePublica());
        usuario = usuarioService.createUsuario(usuario);

    }

    public UsuarioRest registraUsuario(String nome) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPair keyPair = leKeyPair();
        UsuarioRest usuarioRest = new UsuarioRest();
        usuarioRest.setNome(nome);
        usuarioRest.setChavePublica(keyPair.getPublic().getEncoded());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<UsuarioRest> entity = new HttpEntity<>(usuarioRest, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            //System.out.println("http://" + enderecoServer + "/usuario/");
            ResponseEntity<UsuarioRest> resp = restTemplate.postForEntity("http://" + enderecoServer + "/usuario/", entity, UsuarioRest.class);
            return resp.getBody();
        } catch (Exception e) {
            System.out.println("usuario já cadastrado.");
            String strPubKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            ResponseEntity<UsuarioRest> resp = restTemplate.postForEntity("http://" + enderecoServer + "/usuario/findByChave", new HttpEntity<>(strPubKey, headers), UsuarioRest.class);
            return resp.getBody();
        }
    }

    private KeyPair leKeyPair() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File fpub = new File("src/main/resources/chavePublica.key");
        File fpriv = new File("src/main/resources/chavePrivada.key");
        if (fpub.exists() && fpriv.exists()) {

            //Testando se as chaves existem
            System.out.println("Achou os arquivos");

            FileInputStream pubIn = new FileInputStream(fpub);
            FileInputStream privIn = new FileInputStream(fpriv);

            //Lendo os arquivos
            System.out.println("Lendo os arquivos");

            byte[] barrPub = new byte[(int) pubIn.getChannel().size()];
            byte[] barrPriv = new byte[(int) privIn.getChannel().size()];
            pubIn.read(barrPub);
            privIn.read(barrPriv);

            //Leu os arquivos
            System.out.println("Leu os arquivos");

            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(barrPub));
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(barrPriv));
            return new KeyPair(publicKey, privateKey);
        } else {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair keyPair = kpg.generateKeyPair();
            FileOutputStream pubOut = new FileOutputStream("src/main/resources/chavePublica.key", false);
            FileOutputStream privOut = new FileOutputStream("src/main/resources/chavePrivada.key", false);
            pubOut.write(keyPair.getPublic().getEncoded());
            privOut.write(keyPair.getPrivate().getEncoded());
            pubOut.close();
            privOut.close();
            return keyPair;
        }
    }

    private static class UsuarioRest {
        private Long id;
        private byte[] chavePublica;
        private String nome;

        public UsuarioRest(){

        }

        public UsuarioRest(Long id, byte[] chavePublica, String nome) {
            this.id = id;
            this.chavePublica = chavePublica;
            this.nome = nome;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public byte[] getChavePublica() {
            return chavePublica;
        }

        public void setChavePublica(byte[] chavePublica) {
            this.chavePublica = chavePublica;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

}
