package com.sumativa.a.usuario.controller;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.sumativa.a.usuario.model.Rol;
import com.sumativa.a.usuario.service.RolServiceImpl;
import com.sumativa.a.usuario.service.UsuarioService;

@WebMvcTest(RolController.class)
public class RolControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolServiceImpl rolServiceImpl;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    public void getAllRoles() throws Exception{
        //Arrange
        //Creacion de roles
        Rol rol1 = new Rol();
        rol1.setNombreRol("Prueba1");
        rol1.setDescripcion("Descripcion 1");

        Rol rol2 = new Rol();
        rol2.setNombreRol("Prueba2");
        rol2.setDescripcion("Descripcion 2");

        List<Rol> roles = Arrays.asList(rol1, rol2);
        when(rolServiceImpl.getAllRoles()).thenReturn(roles);

        //ACT & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/roles"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.aMapWithSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.rolList[0].nombreRol", Matchers.is("Prueba1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.rolList[0].descripcion", Matchers.is("Descripcion 1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.rolList[1].nombreRol", Matchers.is("Prueba2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.rolList[1].descripcion", Matchers.is("Descripcion 2")));


    }
}
