package com.valmor.minhasfinancas.model.repository;


import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.valmor.minhasfinancas.model.entity.Usuario;

@RunWith( SpringRunner.class )
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
		
	// Teste de Integração (precisa de recurso externo à aplicação)
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		//cenario
		Usuario usuario = CriarUsuario();	
		//repository.save(usuario);
		entityManager.persist(usuario);
				
		// ação / execução
		boolean result = repository.existsByEmail("usuario@email.com");
				
		//verificação
		Assertions.assertThat(result).isTrue();

	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		//cenario
		//repository.deleteAll(); ao encerrar dá rollback, não mais necessário
		
		//acao
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//verificação
		Assertions.assertThat(result).isFalse();		
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		
		//cenario
		Usuario usuario = CriarUsuario();	
		
		//acao
		Usuario usuarioSalvo = repository.save(usuario);
		//verificacao: retorna o ID do usuário; se não retornar é inválido		
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
		
	}
	
	@Test
	public void deveBuscarUsuarioPoremail() {
		
		//cenario
		Usuario usuario = CriarUsuario();
		entityManager.persist(usuario);
		
		//verificacao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		Assertions.assertThat(result.isPresent()).isTrue();
		
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUsuarioPoremailQuandoNaoExisteNaBase() {
		//cenario: nada (base limpa)
		
		//verificacao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		Assertions.assertThat(result.isPresent()).isFalse();
		
	}
	
	public static Usuario CriarUsuario() {
		return Usuario.builder().
			   nome("usuario").
			   email("usuario@email.com").senha("senha").
			   build();		
	}
	
	
}
