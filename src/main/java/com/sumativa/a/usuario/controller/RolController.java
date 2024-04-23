package com.sumativa.a.usuario.controller;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sumativa.a.usuario.model.Rol;
import com.sumativa.a.usuario.model.RolDTO;
import com.sumativa.a.usuario.model.Usuario;
import com.sumativa.a.usuario.service.RolService;
import com.sumativa.a.usuario.service.UsuarioService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/roles")
public class RolController{

    private static final Logger log = LoggerFactory.getLogger(RolController.class);

    @Autowired
    private RolService rolService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public CollectionModel<EntityModel<Rol>> getAllRoles() {
        List<Rol> roles = rolService.getAllRoles();

        List<EntityModel<Rol>> rolResources = roles.stream()
                .map(rol -> EntityModel.of(rol,
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getRolByID(rol.getId())).withSelfRel()
                    ))
                .collect(Collectors.toList());
        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllRoles());
        CollectionModel<EntityModel<Rol>> resourses = CollectionModel.of(rolResources, linkTo.withRel("roles"));

        return resourses;
    }

    @GetMapping("/{id}")
    public EntityModel<Rol> getRolByID(@PathVariable Long id) {
        Optional<Rol> rol = rolService.getRolById(id);
        if(rol.isPresent()){
            return EntityModel.of(rol.get(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getRolByID(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllRoles()).withRel("all-roles"));
        }else{
            throw new NotFoundException("No se encontro ningun Rol con este id: " + id);
        }
        
    }

    @PostMapping
    public EntityModel<Rol> crearRol(@RequestBody RolDTO rol){
        //Se valida si el id esta vacio:
        if(rol.getIdUsuario() == null){
            log.error("El ID Usuario esta vacio");
            throw new BadRequestException("Debe ingresar el ID usurio antes de crear un rol ");
        }
        //Se busca obtener el usuario si se proporciona un id
        Optional<Usuario> buscarUsuario = usuarioService.getUsuarioById(rol.getIdUsuario());
        if(buscarUsuario.isEmpty()){
            log.error("No se encontro un Usuario con el ID {}", rol.getIdUsuario());
            throw new NotFoundException("No se encontro ningun Usuario con ese ID ");
           
        }
        if(rol.getNombreRol() == null || rol.getNombreRol().isEmpty()){
            log.error("No se pueden definir un rol sin nombre");
            throw new BadRequestException("El rol debe tener un nombre");
        }
        
        Rol rolcreado = new Rol();
        rolcreado.setNombreRol(rol.getNombreRol());
        rolcreado.setDescripcion(rol.getDescripcion());
        rolcreado.setUsuario(buscarUsuario.get());

        Rol r = rolService.crearRol(rolcreado);
        if(r == null){
            log.error("Error al crear el rol");
            throw new BadRequestException("Error al crear el rol");
        }
        return EntityModel.of(r,
        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getRolByID(r.getId())).withSelfRel(),
        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllRoles()).withRel("all-roles"));
    }

    @PutMapping("/{id}")
    public EntityModel<Rol> actualizarRol(@PathVariable Long id, @RequestBody RolDTO rol){
        //Se valida si el idUsuario esta vacio:
        if(rol.getIdUsuario() == null){
            log.error("El ID Usuario esta vacio");
            throw new BadRequestException("Debe ingresar el ID usurio antes de crear un rol");
        }

        //Se busca obtener el usuario si se proporciona un id
        Optional<Usuario> buscarUsuario = usuarioService.getUsuarioById(rol.getIdUsuario());
        if(buscarUsuario.isEmpty()){
            log.error("No se encontro un Usuario con el ID {}", rol.getIdUsuario());
            throw new BadRequestException("No se encontro ningun Usuario con ese ID");
           
        }

        Optional<Rol> rolbuscado = rolService.getRolById(id);
        if(rolbuscado.isEmpty()){
            log.error("No se encontro ningun rol con ese ID {} ", id);
            throw new BadRequestException("No se encontro ningun rol con ese ID");
        }
        if(rol.getNombreRol() == null){
            log.error("No se pueden definir un rol sin nombre", id);
            throw new BadRequestException("El rol debe tener un nombre");
        }

        Rol r = new Rol();
        r.setNombreRol(rol.getNombreRol());
        r.setDescripcion(rol.getDescripcion());
        r.setUsuario(buscarUsuario.get());
                
        Rol retorno = rolService.actualizarRol(id, r);
        return EntityModel.of(retorno,
        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getRolByID(id)).withSelfRel(),
        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllRoles()).withRel("all-roles"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarRol(@PathVariable Long id){
        Optional<Rol> rolbuscado = rolService.getRolById(id);
        if(rolbuscado.isEmpty()){
            log.error("No se encontro ningun rol con ese ID {} ", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No se encontro ningun rol con ese ID"));
        }

        rolService.eliminarRol(id);
        return ResponseEntity.ok("Rol Eliminado");
    }


    
    static class ErrorResponse {
        private final String message;
    
        public ErrorResponse(String message){
            this.message = message;
        }
    
        public String getMessage(){
            return message;
        }
        
    }
}

