package com.sumativa.a.usuario.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sumativa.a.usuario.model.Usuario;
import com.sumativa.a.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {
    @InjectMocks
    private UsuarioServiceImpl usuarioServiceImpl;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    public void crearUsuarioTest(){
        //Arrange
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto("Jorge Miranda");
        usuario.setCorreo("jorg.sanchezm@prueba.cl");
        usuario.setContrasena("1234");
        usuario.setDirecciones("La pinata 1234 Pudahuel");

        when(usuarioRepository.save(any())).thenReturn(usuario);

        //ACT
        Usuario resultado = usuarioServiceImpl.crearUsuario(usuario);

        //Assert
        assertEquals("Jorge Miranda", resultado.getNombreCompleto());
        assertEquals("jorg.sanchezm@prueba.cl", resultado.getCorreo());
        assertEquals("1234", resultado.getContrasena());
        assertEquals("La pinata 1234 Pudahuel", resultado.getDirecciones());

    }
}
