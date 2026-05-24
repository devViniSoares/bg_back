package com.bigodeautopecas.backend.config;

import com.bigodeautopecas.backend.model.Usuario;
import com.bigodeautopecas.backend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {



    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email:admin@bigodeautopecas.com}")
    private String adminEmail;

    @Value("${admin.senha:admin123}")
    private String adminSenha;

    @Value("${admin.nome:Administrador}")
    private String adminNome;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = new Usuario();
            admin.setNome(adminNome);
            admin.setEmail(adminEmail);
            admin.setSenha(passwordEncoder.encode(adminSenha));
            admin.setTipo("ADMIN");
            usuarioRepository.save(admin);
            log.info("Admin criado: {}", adminEmail);
        }
    }
}