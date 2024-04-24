package com.sumativa.a.usuario.model;

public class RolDTO {
    private String nombreRol;
    private String descripcion;
    private Long idUsuario;

    public RolDTO() {
    }


    public RolDTO(String nombreRol, String descripcion, Long idUsuario) {
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
        this.idUsuario = idUsuario;
    }


    public String getNombreRol() {
        return nombreRol;
    }
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Long getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    
}
