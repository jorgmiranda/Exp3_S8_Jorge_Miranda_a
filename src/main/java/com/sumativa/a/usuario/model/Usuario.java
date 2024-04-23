package com.sumativa.a.usuario.model;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity(name = "usuario")
public class Usuario extends RepresentationModel<Usuario>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;
    @Column(name = "nombre_usuario")
    private String nombreCompleto;
    @Column(name = "contrasena")
    private String contrasena;
    @Column(name = "correo_usuario", unique = true)
    private String correo;
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rol> roles;
    @Column(name = "direcciones_usuario")
    private String direcciones;

    
    public Usuario(){
        
    }


    public Usuario(Long id, String nombreCompleto, String correo, List<Rol> roles, String direcciones, String contrasena) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.roles = roles;
        this.direcciones = direcciones;
        this.contrasena = contrasena;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getNombreCompleto() {
        return nombreCompleto;
    }


    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }


    public String getCorreo() {
        return correo;
    }


    public void setCorreo(String correo) {
        this.correo = correo;
    }


    public List<Rol> getRoles() {
        return roles;
    }


    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }


    public String getDirecciones() {
        return direcciones;
    }


    public void setDirecciones(String direcciones) {
        this.direcciones = direcciones;
    }


    public String[] getDireccionesArray() {
        return this.direcciones.split(",");
    }


    public void setDirecciones(String[] direcciones) {
        this.direcciones = String.join(",", direcciones);
    }


    public String getContrasena() {
        return contrasena;
    }


    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }


    

    

}
