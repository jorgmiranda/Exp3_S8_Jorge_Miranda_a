package com.sumativa.a.usuario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sumativa.a.usuario.model.Rol;
import com.sumativa.a.usuario.repository.RolRepository;

@Service
public class RolServiceImpl implements RolService{
    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<Rol> getAllRoles() {
        return rolRepository.findAll();
    }

    @Override
    public Optional<Rol> getRolById(Long id) {
        return rolRepository.findById(id);
    }

    @Override
    public Rol crearRol(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public Rol actualizarRol(Long id, Rol rol) {
        if(rolRepository.existsById(id)){
            rol.setId(id);
            return rolRepository.save(rol);
        }else{
            return null;
        }
    }

    @Override
    public void eliminarRol(Long id) {
        rolRepository.deleteById(id);
    }
    
}
