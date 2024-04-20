package com.sumativa.a.usuario.service;

import java.util.List;
import java.util.Optional;

import com.sumativa.a.usuario.model.Usuario;

public interface UsuarioService {
    List<Usuario> getAllUsuario();
    Optional<Usuario> getUsuarioById(Long id);
    Usuario crearUsuario(Usuario usuario);
    Usuario actualizarUsuario(Long id, Usuario usuario);
    void eliminarUsuario(Long id);
}
