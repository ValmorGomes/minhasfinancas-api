package com.valmor.minhasfinancas.model.repository;

import java.util.Optional;

//import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.valmor.minhasfinancas.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository <Usuario, Long> {

	//Optional<Usuario> findByEmail(String email);
	
	boolean existsByEmail(String email);
	
	Optional<Usuario> findByEmail(String email);
}
