package com.sumativa.a.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sumativa.a.usuario.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    
}
