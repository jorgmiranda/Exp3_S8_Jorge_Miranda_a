package com.sumativa.a.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sumativa.a.usuario.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long>{
    
}
