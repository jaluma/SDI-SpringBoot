package com.uniovi.tests;

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
		String email = "javiermartinezalvarez98@gmail.com";
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
		String email = "javiermartinezalvarez98@gmail.com";
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
		String email = "javiermartinezalvarez98@gmail.com";
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
		PO_AdminListView.deleteElements(driver, 2);//borramos el 1 debido a que el primero es el admin
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
		assertEquals(8, PO_AdminListView.checkNumberList(driver));
	}

	//PR14. Ir a la lista de usuarios, borrar el último usuario de la lista, comprobar que la lista se actualiza
	//y dicho usuario desaparece
	@Test
	public void PR15() {
		PO_AdminListView.goAdminList(driver);
		assertEquals(10, PO_AdminListView.checkNumberList(driver));
		PO_AdminListView.changePage(driver, 2);
		assertEquals(10, PO_AdminListView.checkNumberList(driver));
		PO_AdminListView.deleteElements(driver, 9);
		assertEquals(8, PO_AdminListView.checkNumberList(driver));
		PO_AdminListView.deleteElements(driver, 8);
		assertEquals(7, PO_AdminListView.checkNumberList(driver));
		PO_AdminListView.deleteElements(driver, 7);
		assertEquals(6, PO_AdminListView.checkNumberList(driver));
	}
}