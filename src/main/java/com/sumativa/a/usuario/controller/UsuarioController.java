package com.sumativa.a.usuario.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RestController;

import com.sumativa.a.usuario.model.Rol;
import com.sumativa.a.usuario.model.Usuario;
import com.sumativa.a.usuario.service.RolService;
import com.sumativa.a.usuario.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> getUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuario();

        List<EntityModel<Usuario>> usuarioResources = usuarios.stream()
                .map(usuario -> EntityModel.of(usuario,
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioByID(usuario.getId())).withSelfRel()
                    ))
                .collect(Collectors.toList());
        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarios());
        CollectionModel<EntityModel<Usuario>> resourses = CollectionModel.of(usuarioResources, linkTo.withRel("usuarios"));

        return resourses;
    }

    @GetMapping("/{id}")
    public EntityModel<Usuario>  getUsuarioByID(@PathVariable Long id) {
        Optional<Usuario> usr = usuarioService.getUsuarioById(id);
        if (usr.isPresent()) {
            return EntityModel.of(usr.get(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioByID(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarios()).withRel("all-users"));
        } else {
            throw new NotFoundException("No se encontro ningun usuario con este id: " + id);
        }
    }
    
    @GetMapping("/{id}/direcciones")
    public CollectionModel<List<String>> getUsuarioDirecciones(@PathVariable Long id) {
        Optional<Usuario> usr = usuarioService.getUsuarioById(id);
        if (usr.isPresent()) {
            String[] direccionesArray = usr.get().getDireccionesArray();

            List<String> direcciones = Arrays.asList(direccionesArray);

            Link selfLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioDirecciones(id)
            ).withSelfRel();

            Link usuariosLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarios()
            ).withRel("all-users");

        // Crear EntityModel con las direcciones del usuario y los enlaces
        return CollectionModel.of(Arrays.asList(direcciones), selfLink, usuariosLink);
        } else {
            throw new NotFoundException("No se encontro ningun usuario con este id: " + id);
        }
    
    }
    
    @GetMapping("/{id}/roles")
    public CollectionModel<EntityModel<Rol>> getUsuariosRoles(@PathVariable Long id) {
        Optional<Usuario> usr = usuarioService.getUsuarioById(id);
        if (usr.isPresent()) {
            List<Rol> roles = usr.get().getRoles();

            List<EntityModel<Rol>> rolesModel = roles.stream()
            .map(rol -> EntityModel.of(rol,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RolController.class).getRolByID(rol.getId())).withSelfRel()
            ))
            .collect(Collectors.toList());
            
            Link selfLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(this.getClass()).getUsuariosRoles(id)
            ).withSelfRel();

            Link usuariosLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarios()
            ).withRel("all-users");

            return CollectionModel.of(rolesModel, selfLink, usuariosLink);

        }else {
            throw new NotFoundException("No se encontro ningun usuario con este id: " + id);
        }
        
    }

    @PostMapping("/login")
    public EntityModel<Usuario>pruebaLogin(@RequestBody LoginControl login) {
        List<Usuario> usuarios = usuarioService.getAllUsuario();
        for (Usuario u : usuarios){
            if(u.getCorreo().equals(login.getUsuario()) && u.getContrasena().equals(login.getContrasena())){
                return EntityModel.of(u,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioByID(u.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarios()).withRel("all-users"));
            }
        }
        log.error("Credenciales incorrectas ");
        throw new NotFoundException("Credenciales incorrectas");
    }

    @PostMapping
    public EntityModel<Usuario> crearUsuario(@RequestBody Usuario usr){
        // Validaciones de campo
        if(usr.getNombreCompleto() == null || usr.getNombreCompleto().isEmpty()){
            log.error("El nombre del usuario es obligatorio." );
            throw new BadRequestException("El nombre del usuario es obligatorio. ");
        }

        if(usr.getContrasena() == null || usr.getContrasena().isEmpty()){
            log.error("La constraseña es obligatorio." );
            throw new BadRequestException("La constraseña es obligatoria.");
        }

        if(usr.getCorreo() == null || usr.getCorreo().isEmpty()){
            log.error("El correo es obligatorio." );
            throw new BadRequestException("El correo es obligatorio.");
        }
        
        
        //Valida que el correo sea unico
        List<Usuario> listaUsuarios = usuarioService.getAllUsuario();
        for(Usuario u : listaUsuarios){
            log.info("Verifica si el correo ya existe en la bd");
            if(usr.getCorreo().equals(u.getCorreo())){
                log.error("Ya existe un usuario con el correo {} ", u.getCorreo());
                throw new BadRequestException("Ya existe un usuario con el correo "+ u.getCorreo());
                
            }
        }
        log.info("Completa validación de correo");
        // Creacion de usuarios
        Usuario usrCreado = usuarioService.crearUsuario(usr);

        if(usrCreado == null){
            log.error("Error al crear el Usuario");
            throw new BadRequestException("Error al crear el Usuario");
        }
        if(usrCreado.getRoles()!= null){
            for(Rol r : usrCreado.getRoles()){
                r.setUsuario(usrCreado);
                rolService.actualizarRol(r.getId(), r);
            }
        }


        return EntityModel.of(usrCreado,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioByID(usrCreado.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarios()).withRel("all-users"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarUsuario(@PathVariable Long id){
        Optional<Usuario> usr = usuarioService.getUsuarioById(id);
        if(usr.isEmpty()){
            log.error("No se encontro ningun Usuario con ese ID {} ", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No se encontro ningun Usuario con ese ID"));
        }
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok("Usuario Eliminado");
    }

    @PutMapping("/{id}")
    public EntityModel<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usr){
       // Valida que el usuario exista
        Optional<Usuario> usrBuscado = usuarioService.getUsuarioById(id);
        if(usrBuscado.isEmpty()){
            log.error("No se encontro ningun Usuario con ese ID {} ", id);
            throw new NotFoundException("No se encontro ningun Usuario con ese ID");
        }
        if(usr.getNombreCompleto() == null || usr.getNombreCompleto().isEmpty()){
            log.error("El nombre del usuario es obligatorio." );
            throw new BadRequestException("El nombre del usuario es obligatorio");
        }

        if(usr.getContrasena() == null ||usr.getContrasena().isEmpty()){
            log.error("La constraseña es obligatorio." );
            throw new BadRequestException("La constraseña es obligatoria");
        }

        if(usr.getCorreo() == null || usr.getCorreo().isEmpty()){
            log.error("El correo es obligatorio." );
            throw new BadRequestException("El correo es obligatorio.");
        }

        //Valida que el correo sea unico
        List<Usuario> listaUsuarios = usuarioService.getAllUsuario();
        for(Usuario u : listaUsuarios){
            if(usr.getCorreo().equals(u.getCorreo())){
                log.error("Ya existe un usuario con el correo {} ", u.getCorreo());
                throw new BadRequestException("Ya existe un usuario con el correo "+u.getCorreo());
            }
        }
        //Preservar roles
        Usuario usuarioActual = usrBuscado.get();
        usuarioActual.setNombreCompleto(usr.getNombreCompleto());
        usuarioActual.setContrasena(usr.getContrasena());
        usuarioActual.setCorreo(usr.getCorreo());
        usuarioActual.setDirecciones(usr.getDirecciones());

        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioActual);
        
        return EntityModel.of(usuarioActualizado,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioByID(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarios()).withRel("all-users"));
    
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

    static class LoginControl {
        public String usuario;
        public String contrasena;
        
        public String getUsuario() {
            return usuario;
        }
        public void setUsuario(String usuario) {
            this.usuario = usuario;
        }
        public String getContrasena() {
            return contrasena;
        }
        public void setContrasena(String contrasena) {
            this.contrasena = contrasena;
        }

        
        
    }

}






