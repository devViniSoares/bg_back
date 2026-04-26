package com.bigodeautopecas.backend.repository;

import com.bigodeautopecas.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {}
