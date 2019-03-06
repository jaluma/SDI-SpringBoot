package com.uniovi.tests;

import com.uniovi.repositories.ChatRepository;
import com.uniovi.repositories.ItemsRepository;
import com.uniovi.repositories.MessageRepository;
import com.uniovi.repositories.UsersRepository;
import com.uniovi.services.InsertSampleDataService;
import com.uniovi.tests.pageobjects.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
public class MyWallapopTests {

	//En Windows (Debe ser la versión 65.0.1 y desactivar las actualizacioens automáticas)):
	private static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	private static String Geckdriver024 = "C:\\Path\\geckodriver024win64.exe";
	//En MACOSX (Debe ser la versión 65.0.1 y desactivar las actualizacioens automáticas):
	//static String PathFirefox65 = "/Applications/Firefox.app/Contents/MacOS/firefox-bin";
	//static String Geckdriver024 = "/Users/delacal/selenium/geckodriver024mac";
	//Común a Windows y a MACOSX
	private static WebDriver driver = getDriver(PathFirefox65, Geckdriver024);
	private static final String URL = "http://localhost:8090";

	@Autowired
	private InsertSampleDataService insertSampleDataService;
	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private ChatRepository chatRepository;
	@Autowired
	private ItemsRepository itemsRepository;
	@Autowired
	private MessageRepository messageRepository;

	private static WebDriver getDriver(String PathFirefox, String Geckdriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckdriver);
		return new FirefoxDriver();
	}

	@BeforeClass
	static public void begin() {
	}

	@AfterClass
	static public void end() {
		driver.quit();
	}

	@Before
	public void setUp() {
		initdb();
		driver.navigate().to(URL);
	}

	private void initdb() {
		messageRepository.deleteAll();

		chatRepository.deleteAll();

		itemsRepository.deleteAll();

		usersRepository.deleteAll();

		insertSampleDataService.init();
	}

	@After
	public void tearDown() {
		driver.manage().deleteAllCookies();
	}

	//PR01. Registro de Usuario con datos válidos
	@Test
	public void PR01() {
		PO_NavView.signup(driver);
		PO_RegisterView.fillForm(driver, PO_RegisterView.generateRandomEmail(), "Josefo", "Perez", "777777", "777777");
		PO_HomeView.checkWelcome(driver, PO_Properties.getSPANISH());
	}

	//PR02.  Registro de Usuario con datos inválidos (email vacío, nombre vacío, apellidos vacíos).
	@Test
	public void PR02() {
		PO_NavView.signup(driver);
		PO_RegisterView.fillForm(driver, "", "", "", "777777", "777777");
		assertEquals(1, PO_RegisterView.checkElement(driver, "class", "is-focused").size());
	}

	//PR03. Registro de Usuario con datos inválidos (repetición de contraseña inválida).
	@Test
	public void PR03() {
		PO_NavView.signup(driver);
		PO_RegisterView.fillForm(driver, PO_RegisterView.generateRandomEmail(), "Josefo", "Perez", "777777", "666666");
		PO_RegisterView.checkElement(driver, "id", "paswordConfirm-error");
	}

	//PR04.  Registro de Usuario con datos inválidos (email existente).
	@Test
	public void PR04() {
		String email = PO_RegisterView.generateRandomEmail();
		PO_NavView.signup(driver);
		PO_RegisterView.fillForm(driver, email, "Josefo", "Perez", "777777", "777777");
		PO_HomeView.checkWelcome(driver, PO_Properties.getSPANISH());
		PO_NavView.logout(driver);
		PO_NavView.signup(driver);
		PO_RegisterView.fillForm(driver, email, "Josefo", "Perez", "777777", "777777");
		PO_RegisterView.checkKey(driver, "Error.signup.email.duplicate", PO_Properties.getSPANISH());
	}

	//PR05.  Inicio de sesión con datos válidos (administrador).
	@Test
	public void PR05() {
		String email = "admin@email.com";
		String password = "admin";
		PO_NavView.login(driver);
		PO_LoginView.fillForm(driver, email, password);
		PO_NavView.checkElement(driver, "id", "adminListMenu");
	}

	//PR06.  Inicio de sesión con datos válidos (usuario estándar).
	@Test
	public void PR06() {
		String email = "javier@gmail.com";
		String password = "123456";
		PO_NavView.login(driver);
		PO_LoginView.fillForm(driver, email, password);
		PO_HomeView.checkWelcome(driver, PO_Properties.getSPANISH());
	}

	//PR07.  Inicio de sesión con datos inválidos (usuario estándar, campo email y contraseña vacíos).
	@Test
	public void PR07() {
		String email = "";
		String password = "";
		PO_NavView.login(driver);
		PO_LoginView.fillForm(driver, email, password);
		assertEquals(1, PO_RegisterView.checkElement(driver, "class", "is-focused").size());
	}

	//PR08.  Inicio de sesión con datos válidos (usuario estándar, email existente, pero contraseña incorrecta).
	@Test
	public void PR08() {
		String email = "javier@gmail.com";
		String password = "654321";
		PO_NavView.login(driver);
		PO_LoginView.fillForm(driver, email, password);
		PO_RegisterView.checkElement(driver, "id", "login-error");
	}

	//PR09.  Inicio de sesión con datos inválidos (usuario estándar, email no existente en la aplicación).
	@Test
	public void PR09() {
		String email = PO_RegisterView.generateRandomEmail();
		String password = "654321";
		PO_NavView.login(driver);
		PO_LoginView.fillForm(driver, email, password);
		PO_RegisterView.checkElement(driver, "id", "login-error");
	}

	//PR10.  Hacer click en la opción de salir de sesión y comprobar que se redirige a la página de inicio
	//de sesión (Login).
	@Test
	public void PR10() {
		String email = "javier@gmail.com";
		String password = "123456";
		PO_NavView.login(driver);
		PO_LoginView.fillForm(driver, email, password);
		PO_HomeView.checkWelcome(driver, PO_Properties.getSPANISH());
		PO_NavView.logout(driver);
		PO_RegisterView.checkElement(driver, "id", "login-button");
	}

	//PR11.   Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado
	@Test
	public void PR11() {
		assertFalse(PO_NavView.checkButtonLogout(driver));
	}

	//PR12. Mostrar el listado de usuarios y comprobar que se muestran todos los que existen en el
	//sistema.
	@Test
	public void PR12() {
		PO_AdminListView.goAdminList(driver);
		assertEquals(10, PO_AdminListView.checkNumberList(driver));
		PO_AdminListView.changePage(driver, 2);
		assertEquals(10, PO_AdminListView.checkNumberList(driver));
	}

	//PR13. Ir a la lista de usuarios, borrar el primer usuario de la lista, comprobar que la lista se actualiza
	//y dicho usuario desaparece.
	@Test
	public void PR13() {
		PO_AdminListView.goAdminList(driver);
		assertEquals(10, PO_AdminListView.checkNumberList(driver));
		PO_AdminListView.changePage(driver, 2);
		assertEquals(10, PO_AdminListView.checkNumberList(driver));
		PO_AdminListView.changePage(driver, 1);
		PO_AdminListView.deleteElements(driver, 3);//borramos el 1 debido a que el primero es el admin
		assertEquals(10, PO_AdminListView.checkNumberList(driver));
		PO_AdminListView.changePage(driver, 2);
		assertEquals(9, PO_AdminListView.checkNumberList(driver));
	}

	//PR14. Ir a la lista de usuarios, borrar el último usuario de la lista, comprobar que la lista se actualiza
	//y dicho usuario desaparece
	@Test
	public void PR14() {
		PO_AdminListView.goAdminList(driver);
		assertEquals(10, PO_AdminListView.checkNumberList(driver));
		PO_AdminListView.changePage(driver, 2);
		assertEquals(10, PO_AdminListView.checkNumberList(driver));
		PO_AdminListView.deleteElements(driver, 9);
		assertEquals(10, PO_AdminListView.checkNumberList(driver));
		PO_AdminListView.changePage(driver, 2);
		assertEquals(9, PO_AdminListView.checkNumberList(driver));
	}

	//PR14. Ir a la lista de usuarios, borrar el último usuario de la lista, comprobar que la lista se actualiza
	//y dicho usuario desaparece
	@Test
	public void PR15() {
		PO_AdminListView.goAdminList(driver);
		assertEquals(10, PO_AdminListView.checkNumberList(driver));
		PO_AdminListView.deleteElements(driver, 10);
		assertEquals(10, PO_AdminListView.checkNumberList(driver));
		PO_AdminListView.changePage(driver, 2);
		assertEquals(9, PO_AdminListView.checkNumberList(driver));
	}

	//PR16. Ir al formulario de alta de oferta, rellenarla con datos válidos y pulsar el botón Submit.
	//Comprobar que la oferta sale en el listado de ofertas de dicho usuario.
	@Test
	public void PR16() {
		String email = "javier@gmail.com";
		String password = "123456";
		PO_NavView.login(driver);
		PO_LoginView.fillForm(driver, email, password);
		PO_HomeView.checkWelcome(driver, PO_Properties.getSPANISH());
		PO_NavView.navbar(driver, "mySellsDropdownMenuLink", "add");
		PO_ItemView.addFillForm(driver, "Prueba 1", "Descripcion 1", "10", false);
		PO_ItemView.checkElement(driver, "id", "tableItems");
	}

	//PR17. Ir al formulario de alta de oferta, rellenarla con datos inválidos (campo título vacío) y pulsar
	//el botón Submit. Comprobar que se muestra el mensaje de campo obligatorio.
	@Test
	public void PR17() {
		String email = "javier@gmail.com";
		String password = "123456";
		PO_NavView.login(driver);
		PO_LoginView.fillForm(driver, email, password);
		PO_HomeView.checkWelcome(driver, PO_Properties.getSPANISH());
		PO_NavView.navbar(driver, "mySellsDropdownMenuLink", "add");
		PO_ItemView.addFillForm(driver, "", "Descripcion 1", "10", false);
		PO_ItemView.checkElement(driver, "class", "is-focused");
	}

	//PR18. Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas los que
	//existen para este usuario.
	@Test
	public void PR18() {
		String email = "javier@gmail.com";
		String password = "123456";
		PO_NavView.login(driver);
		PO_LoginView.fillForm(driver, email, password);
		PO_HomeView.checkWelcome(driver, PO_Properties.getSPANISH());
		PO_NavView.navbar(driver, "mySellsDropdownMenuLink", "myList");
		assertEquals(3, PO_ItemView.checkNumberList(driver));
	}

	//PR19. Ir a la lista de ofertas, borrar la primera oferta de la lista, comprobar que la lista se actualiza y
	//que la oferta desaparece.
	@Test
	public void PR19() {
		String email = "javier@gmail.com";
		String password = "123456";
		PO_NavView.login(driver);
		PO_LoginView.fillForm(driver, email, password);
		PO_HomeView.checkWelcome(driver, PO_Properties.getSPANISH());
		PO_NavView.navbar(driver, "mySellsDropdownMenuLink", "myList");
		assertEquals(3, PO_ItemView.checkNumberList(driver));
		PO_ItemView.remove(driver, 0);
		assertEquals(2, PO_ItemView.checkNumberList(driver));
	}

	//PR20. Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar que la lista se actualiza y
	//que la oferta desaparece.
	@Test
	public void PR20() {
		String email = "javier@gmail.com";
		String password = "123456";
		PO_NavView.login(driver);
		PO_LoginView.fillForm(driver, email, password);
		PO_HomeView.checkWelcome(driver, PO_Properties.getSPANISH());
		PO_NavView.navbar(driver, "mySellsDropdownMenuLink", "myList");
		assertEquals(3, PO_ItemView.checkNumberList(driver));
		PO_ItemView.remove(driver, 2);
		assertEquals(2, PO_ItemView.checkNumberList(driver));
	}

	//PR21. Hacer una búsqueda con el campo vacío y comprobar que se muestra la página que
	//corresponde con el listado de las ofertas existentes en el sistema

	//PR22. Hacer una búsqueda escribiendo en el campo un texto que no exista y comprobar que se
	//muestra la página que corresponde, con la lista de ofertas vacía

	//PR23. Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que deja
	//un saldo positivo en el contador del comprobador. Y comprobar que el contador se actualiza
	//correctamente en la vista del comprador.

	//PR24. Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que deja
	//un saldo 0 en el contador del comprobador. Y comprobar que el contador se actualiza correctamente en
	//la vista del comprador.

	//PR25. Sobre una búsqueda determinada (a elección de desarrollador), intentar comprar una oferta
	//que esté por encima de saldo disponible del comprador. Y comprobar que se muestra el mensaje de
	//saldo no suficiente

	//PR26. Ir a la opción de ofertas compradas del usuario y mostrar la lista. Comprobar que aparecen
	//las ofertas que deben aparecer.

	//PR27. Visualizar al menos cuatro páginas en Español/Inglés/Español (comprobando que algunas
	//de las etiquetas cambian al idioma correspondiente). Página principal/Opciones Principales de
	//Usuario/Listado de Usuarios de Admin/Vista de alta de Oferta.

	//PR28. Intentar acceder sin estar autenticado a la opción de listado de usuarios del administrador. Se
	//deberá volver al formulario de login.

	//PR29. Intentar acceder sin estar autenticado a la opción de listado de ofertas propias de un usuario
	//estándar. Se deberá volver al formulario de login.

	//PR30. Estando autenticado como usuario estándar intentar acceder a la opción de listado de
	//usuarios del administrador. Se deberá indicar un mensaje de acción prohibida.

	//PR31. Sobre una búsqueda determinada de ofertas (a elección de desarrollador), enviar un mensaje
	//a una oferta concreta. Se abriría dicha conversación por primera vez. Comprobar que el mensaje aparece
	//en el listado de mensajes.

	//PR32. Sobre el listado de conversaciones enviar un mensaje a una conversación ya abierta.
	//Comprobar que el mensaje aparece en la lista de mensajes.

	//PR33. Mostrar el listado de conversaciones ya abiertas. Comprobar que el listado contiene las
	//conversaciones que deben ser

	//PR34. Sobre el listado de conversaciones ya abiertas. Pinchar el enlace Eliminar de la primera y
	//comprobar que el listado se actualiza correctamente.

	//PR35. Sobre el listado de conversaciones ya abiertas. Pinchar el enlace Eliminar de la última y
	//comprobar que el listado se actualiza correctamente.

	//PR36. Al crear una oferta marcar dicha oferta como destacada y a continuación comprobar: i) que
	//aparece en el listado de ofertas destacadas para los usuarios y que el saldo del usuario se actualiza
	//adecuadamente en la vista del ofertante (-20).

	//PR37. Sobre el listado de ofertas de un usuario con menos de 20 euros de saldo, pinchar en el
	//enlace Destacada y a continuación comprobar: i) que aparece en el listado de ofertas destacadas para los
	//usuarios y que el saldo del usuario se actualiza adecuadamente en la vista del ofertante (-20).

	//PR38. Sobre el listado de ofertas de un usuario con menos de 20 euros de saldo, pinchar en el
	//enlace Destacada y a continuación comprobar que se muestra el mensaje de saldo no suficiente.

}