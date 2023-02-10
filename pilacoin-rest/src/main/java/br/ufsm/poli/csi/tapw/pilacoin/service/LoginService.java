package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.Log;
import br.ufsm.poli.csi.tapw.pilacoin.model.Usuario;
import br.ufsm.poli.csi.tapw.pilacoin.repository.UsuarioRepository;
import br.ufsm.poli.csi.tapw.pilacoin.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


@Service
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    private AuthenticationManager authenticationManager;
    private UsuarioRepository usuarioRepository;
    private LogService logService;

    @Autowired
    public LoginService(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository, LogService logService){
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.logService = logService;
    }

    public Usuario login(String email, String senha) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(email, senha);
            var authentication = authenticationManager.authenticate(authToken);

            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);

                var objetoUsuario = usuarioRepository.findUsuarioByEmail(email);
                var usuario = objetoUsuario;

                logger.info("Login efetuado! | Usuário: {}", usuario.getNome());
                return usuario;
            }
        } catch (AuthenticationException e) {
            logger.error("Falha de autenticação de usuário!");
            return null;
        }

        return null;
    }

}
