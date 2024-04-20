package com.sumativa.a.usuario.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Rol> getAllRoles() {
        return rolService.getAllRoles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRolByID(@PathVariable Long id) {
        Optional<Rol> rol = rolService.getRolById(id);
        if(rol.isEmpty()){
            log.error("No se encontro ningun Rol con ese ID {} ", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No se encontro ningun rol con ese ID"));
        }
        return ResponseEntity.ok(rol);
    }

    @PostMapping
    public ResponseEntity<Object> crearRol(@RequestBody RolDTO rol){
        //Se valida si el id esta vacio:
        if(rol.getIdUsuario() == null){
            log.error("El ID Usuario esta vacio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Debe ingresar el ID usurio antes de crear un rol"));
        }
        //Se busca obtener el usuario si se proporciona un id
        Optional<Usuario> buscarUsuario = usuarioService.getUsuarioById(rol.getIdUsuario());
        if(buscarUsuario.isEmpty()){
            log.error("No se encontro un Usuario con el ID {}", rol.getIdUsuario());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No se encontro ningun Usuario con ese ID"));
           
        }
        if(rol.getNombreRol() == null){
            log.error("No se pueden definir un rol sin nombre");
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse("El rol debe tener un nombre"));
        }
        
        Rol rolcreado = new Rol();
        rolcreado.setNombreRol(rol.getNombreRol());
        rolcreado.setDescripcion(rol.getDescripcion());
        rolcreado.setUsuario(buscarUsuario.get());

        Rol r = rolService.crearRol(rolcreado);
        if(r == null){
            log.error("Error al crear el rol");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Error al crear el rol"));
        }
        return ResponseEntity.ok(r);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizarRol(@PathVariable Long id, @RequestBody RolDTO rol){
        //Se valida si el idUsuario esta vacio:
        if(rol.getIdUsuario() == null){
            log.error("El ID Usuario esta vacio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Debe ingresar el ID usurio antes de crear un rol"));
        }

        //Se busca obtener el usuario si se proporciona un id
        Optional<Usuario> buscarUsuario = usuarioService.getUsuarioById(rol.getIdUsuario());
        if(buscarUsuario.isEmpty()){
            log.error("No se encontro un Usuario con el ID {}", rol.getIdUsuario());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No se encontro ningun Usuario con ese ID"));
           
        }

        Optional<Rol> rolbuscado = rolService.getRolById(id);
        if(rolbuscado.isEmpty()){
            log.error("No se encontro ningun rol con ese ID {} ", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No se encontro ningun rol con ese ID"));
        }
        if(rol.getNombreRol() == null){
            log.error("No se pueden definir un rol sin nombre", id);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse("El rol debe tener un nombre"));
        }

        Rol r = new Rol();
        r.setNombreRol(rol.getNombreRol());
        r.setDescripcion(rol.getDescripcion());
        r.setUsuario(buscarUsuario.get());
                
        Rol retorno = rolService.actualizarRol(id, r);
        return ResponseEntity.ok(retorno);
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

