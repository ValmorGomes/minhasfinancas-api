package com.valmor.minhasfinancas.service;


import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.valmor.minhasfinancas.exceptions.ErroAutenticacao;
import com.valmor.minhasfinancas.exceptions.RegraNegocioException;
import com.valmor.minhasfinancas.model.entity.Usuario;
import com.valmor.minhasfinancas.model.repository.UsuarioRepository;
import com.valmor.minhasfinancas.service.impl.UsuarioServiceImpl;

@RunWith( SpringRunner.class )
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	//@Autowired
	@SpyBean     //SPY idem MOCK, mas chama os métodos originais mas diz explicitamente comportamento do método
				//Vide aula 50
	UsuarioServiceImpl service;
	
	//@Autowired
	@MockBean
	UsuarioRepository repository;
	
	// Para fazer mock:
	@Before
	public void setUp() {
		//repository = Mockito.mock(UsuarioRepository.class); //substituído por @MockBean
		//service = new UsuarioServiceImpl(repository);
		
		//SPY idem MOCK, mas chama os métodos originais mas diz explicitamente comportamento do método
		//usarei em salvar usuário pois precisa mockar o próprio UsuarioService...		
	}

	
	
	@Test
	public void deveSalvarUmUsuario() {
		//VIDE AULA 50
		
		//cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		//não faz nada ; se não colocasse chamaria método real
		
		Usuario usuario = Usuario.builder()
				.id(1l)
				.nome("nome")
				.email("email@email.com")
				.senha("senha").build();
						
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		//verificacao
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	}

	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		
		//cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email("email@email.com").build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		//acao
		service.salvarUsuario(usuario);
		
		//verificacao
		//Espera que nunca tenha chamado o método save usuário:
		Mockito.verify( repository, Mockito.never() ).save(usuario);
		
		
	}
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		
		//cenario
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when( repository.findByEmail(email) ).thenReturn(Optional.of(usuario));
		
		//acao
		Usuario result = service.autenticar(email, senha);
		
		//verificacao
		Assertions.assertThat(result).isNotNull();
				
	}
	
	@Test		//(expected = ErroAutenticacao.class)
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		
		//cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//acao
		//service.autenticar("email@email.com","senha");
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com","senha") );
		
		//verificacao
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado!");
		
	}
	
	@Test		//(expected = ErroAutenticacao.class)
	public void deveLancarErroQuandoSenhaNaoBater() {
		
		//cenario
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//acao
		//service.autenticar("email@email.com", "123");
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com", "123") );
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida!");
		
		
	}
	
	@Test(expected = Test.None.class)   //espera que método não lance nenhuma exception
	public void deveValidarEmail() {
		
		// cenario
		//repository.deleteAll();  //com Mocks vai fazer rollback quando terminar
		//Quando eu chamar o existByEmail passando qq string ele sempre  vai retornar falso:
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//acao
		service.validarEmail("email@email.com");
	}
		
	@Test(expected = RegraNegocioException.class) 
	public void deveLancarErroAoValidarEmailQuandoExisteEmailCadastrado() {
		
		//cenario
		//Usuario usuario = Usuario.builder().nome("usuarioTest").email("email@email.com").build();
		//repository.save(usuario);
		//Por MOCK:
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//acao
		service.validarEmail("email@email.com");
		
	}
	

}
