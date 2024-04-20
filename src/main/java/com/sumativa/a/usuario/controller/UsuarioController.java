package com.sumativa.a.usuario.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.RestController;

import com.sumativa.a.usuario.model.Rol;
import com.sumativa.a.usuario.model.Usuario;
import com.sumativa.a.usuario.service.RolService;
import com.sumativa.a.usuario.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Usuario> getUsuarios() {
        return usuarioService.getAllUsuario();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUsuarioByID(@PathVariable Long id) {
        Optional<Usuario> usr = usuarioService.getUsuarioById(id);
        if(usr.isEmpty()){
            log.error("No se encontro ningun Usuario con ese ID {} ", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No se encontro ningun Usuario con ese ID"));
        }
        return ResponseEntity.ok(usr);
    }
    
    @GetMapping("/{id}/direcciones")
    public ResponseEntity<Object> getUsuarioDirecciones(@PathVariable Long id) {
        Optional<Usuario> usr = usuarioService.getUsuarioById(id);
        if(usr.isEmpty()){
            log.error("No se encontro ningun Usuario con ese ID {} ", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No se encontro ningun Usuario con ese ID"));
        }
        
        return ResponseEntity.ok(usr.get().getDireccionesArray());
    
    }
    
    @GetMapping("/{id}/roles")
    public ResponseEntity<Object> getUsuariosRoles(@PathVariable Long id) {
        Optional<Usuario> usr = usuarioService.getUsuarioById(id);
        if(usr.isEmpty()){
            log.error("No se encontro ningun Usuario con ese ID {} ", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No se encontro ningun Usuario con ese ID"));
        }
        
        return ResponseEntity.ok(usr.get().getRoles());
        
    }

    @GetMapping("/contar")
    public int getCantidadUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuario();
        return usuarios.size();
    }

    @PostMapping("/login")
    public ResponseEntity<Object> pruebaLogin(@RequestBody LoginControl login) {
        List<Usuario> usuarios = usuarioService.getAllUsuario();
        for (Usuario u : usuarios){
            if(u.getCorreo().equals(login.getUsuario()) && u.getContrasena().equals(login.getContrasena())){
                return ResponseEntity.ok(u);
            }
        }
        log.error("Credenciales incorrectas ");
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse("Credenciales incorrectas"));
    }

    @PostMapping
    public ResponseEntity<Object> crearUsuario(@RequestBody Usuario usr){
        // Validaciones de campo
        if(usr.getNombreCompleto() == null){
            log.error("El nombre del usuario es obligatorio." );
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse("El nombre del usuario es obligatorio "));
        }

        if(usr.getContrasena() == null){
            log.error("La constraseña es obligatorio." );
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse("La constraseña es obligatoria "));
        }

        if(usr.getCorreo() == null){
            log.error("El correo es obligatorio." );
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse("El correo es obligatorio "));
        }
        
        
        //Valida que el correo sea unico
        List<Usuario> listaUsuarios = usuarioService.getAllUsuario();
        for(Usuario u : listaUsuarios){
            log.info("Verifica si el correo ya existe en la bd");
            if(usr.getCorreo().equals(u.getCorreo())){
                log.error("Ya existe un usuario con el correo {} ", u.getCorreo());
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse("Ya existe un usuario con el correo "+ u.getCorreo()));
            }
        }
        log.info("Completa validación de correo");
        Usuario usrCreado = usuarioService.crearUsuario(usr);
        if(usrCreado == null){
            log.error("Error al crear el Usuario");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Error al crear el Usuario"));
        }
        if(usrCreado.getRoles()!= null){
            for(Rol r : usrCreado.getRoles()){
                r.setUsuario(usrCreado);
                rolService.actualizarRol(r.getId(), r);
            }
        }


        return ResponseEntity.ok(usrCreado);
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
    public ResponseEntity<Object> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usr){
       // Valida que el usuario exista
        Optional<Usuario> usrBuscado = usuarioService.getUsuarioById(id);
        if(usrBuscado.isEmpty()){
            log.error("No se encontro ningun Usuario con ese ID {} ", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No se encontro ningun Usuario con ese ID"));
        }
        if(usr.getNombreCompleto() == null){
            log.error("El nombre del usuario es obligatorio." );
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse("El nombre del usuario es obligatorio "));
        }

        if(usr.getContrasena() == null){
            log.error("La constraseña es obligatorio." );
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse("La constraseña es obligatoria "));
        }

        if(usr.getCorreo() == null){
            log.error("El correo es obligatorio." );
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse("El correo es obligatorio "));
        }

        //Valida que el correo sea unico
        List<Usuario> listaUsuarios = usuarioService.getAllUsuario();
        for(Usuario u : listaUsuarios){
            if(usr.getCorreo().equals(u.getCorreo())){
                log.error("Ya existe un usuario con el correo {} ", u.getCorreo());
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse("Ya existe un usuario con el correo "+ u.getCorreo()));
            }
        }
        //Preservar roles
        Usuario usuarioActual = usrBuscado.get();
        usuarioActual.setNombreCompleto(usr.getNombreCompleto());
        usuarioActual.setContrasena(usr.getContrasena());
        usuarioActual.setCorreo(usr.getCorreo());
        usuarioActual.setDirecciones(usr.getDirecciones());

        usuarioService.actualizarUsuario(id, usuarioActual);
        usr.setId(id);
        return ResponseEntity.ok(usr);
    
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






