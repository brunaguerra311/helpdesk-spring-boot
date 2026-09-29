package com.empresa.helpdesk.service;

import com.empresa.helpdesk.model.Perfil;
import com.empresa.helpdesk.model.Usuario;
import com.empresa.helpdesk.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Cadastro PÚBLICO (Criar Conta no Portal)
     * Força o perfil a ser CLIENTE para garantir a segurança do sistema.
     */
    public Usuario cadastrarCliente(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }

        usuario.setPerfil(Perfil.CLIENTE);

        // Criptografa a senha com BCrypt
        String senhaHash = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
        usuario.setSenha(senhaHash);

        return usuarioRepository.save(usuario);
    }

    /**
     * Cadastro ADMINISTRATIVO (Painel do Administrador)
     * Permite que o Admin cadastre TÉCNICOS ou outros ADMINISTRADORES.
     */

    public void alterarSenha(Long idUsuario, String novaSenhaPura) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        String senhaHash = BCrypt.hashpw(novaSenhaPura, BCrypt.gensalt());
        usuario.setSenha(senhaHash);
        usuarioRepository.save(usuario);
    }
    public Usuario cadastrarPorAdmin(Usuario usuario, Perfil perfil) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }

        usuario.setPerfil(perfil);

        String senhaHash = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
        usuario.setSenha(senhaHash);

        return usuarioRepository.save(usuario);
    }

    /**
     * Autenticação de Usuários
     */
    public Usuario autenticar(String email, String senhaPura) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("E-mail ou senha inválidos."));

        if (!BCrypt.checkpw(senhaPura, usuario.getSenha())) {
            throw new IllegalArgumentException("E-mail ou senha inválidos.");
        }

        return usuario;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Exclui um usuário impedindo que o Administrador exclua a sua própria conta
     */
    public void excluirUsuario(Long idParaExcluir, Long idAdminLogado) {
        if (idParaExcluir.equals(idAdminLogado)) {
            throw new IllegalArgumentException("Um administrador não pode excluir a sua própria conta.");
        }
        usuarioRepository.deleteById(idParaExcluir);
    }
}