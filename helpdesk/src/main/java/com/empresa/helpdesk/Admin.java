package com.empresa.helpdesk;

import com.empresa.helpdesk.model.Perfil;
import com.empresa.helpdesk.model.Usuario;
import com.empresa.helpdesk.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Admin implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se o Administrador padrão já existe no MySQL
        if (usuarioRepository.findByEmail("admin@empresa.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNome("admin");
            admin.setEmail("admin@empresa.com");

            // Criptografa a senha "admin123" usando o BCrypt
            String senhaHash = BCrypt.hashpw("admin123", BCrypt.gensalt());
            admin.setSenha(senhaHash);

            admin.setDepartamento("TI");
            admin.setPerfil(Perfil.ADMIN);

            usuarioRepository.save(admin);
            System.out.println(">>> Usuário Administrador criado com sucesso: admin@empresa.com / admin123 <<<");
        }
    }
}
