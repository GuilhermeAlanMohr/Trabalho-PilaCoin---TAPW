package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.Log;
import br.ufsm.poli.csi.tapw.pilacoin.model.Usuario;
import br.ufsm.poli.csi.tapw.pilacoin.repository.LogRepository;
import br.ufsm.poli.csi.tapw.pilacoin.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    @Autowired
    public UsuarioService (UsuarioRepository userRepository) {
        this.repository = userRepository;
    }

    @Transactional
    public Usuario createUsuario(Usuario usuario) throws RuntimeException {
        Usuario newUser = repository.save(usuario);
        return newUser;
    }

}
