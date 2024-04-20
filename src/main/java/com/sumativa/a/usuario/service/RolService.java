package com.sumativa.a.usuario.service;

import java.util.Optional;

import java.util.List;

import com.sumativa.a.usuario.model.Rol;

public interface RolService {
    List<Rol> getAllRoles();
    Optional<Rol> getRolById(Long id);
    Rol crearRol(Rol rol);
    Rol actualizarRol(Long id, Rol rol);
    void eliminarRol(Long id);
}
