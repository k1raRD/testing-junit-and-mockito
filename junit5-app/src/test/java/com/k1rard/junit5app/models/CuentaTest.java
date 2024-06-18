package com.k1rard.junit5app.models;

import com.k1rard.junit5app.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {
    Cuenta cuenta;
    private TestInfo testInfo;
    private TestReporter testReporter;
    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        this.cuenta = new Cuenta("Alex", new BigDecimal("1000.1234"));
        testReporter.publishEntry("Iniciando el metodo");
        testReporter.publishEntry("Ejecutando: " + testInfo.getTestMethod().get().getName() + " " + testInfo.getDisplayName()
                + " con la etiqueta " + testInfo.getTags());
    }
    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el metodo de prueba.");
    }
    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }
    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Tag("cuenta")
    @Nested
    @DisplayName("Probando atributos de cuenta corriente")
    class CuentaTestNombreSaldo {
        @Test
        @DisplayName("Probando el nombre de la cuenta corriente!")
        void testNombreCuenta() {
            System.out.println(testInfo.getTags());
            if(testInfo.getTags().contains("Cuenta")) {
                testReporter.publishEntry("Hacer algo con la etiqueta cuenta.");
            }
//        cuenta.setPersona("Alex");
            String esperado = "Alex";
            String actual = cuenta.getPersona();
            assertNotNull(actual, () -> "La cuenta no puede ser nula");
            assertEquals(esperado, actual, () -> "El nombre de la cuenta no es el que se esperaba: " + esperado + " sin embargo fue " + actual);
            assertTrue(actual.equals("Alex"), () -> "Nombre cuenta debe ser igual al actual");
        }
        @Test
        @DisplayName("Probando el saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperado.")
        void testSaldoCuenta() {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.1234, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
        @Test
        @DisplayName("Testeando referencias que sean iguales con el metodo equals.")
        void testReferenciaCuenta() {
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal(("8900.999")));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal(("8900.999")));
//        assertNotEquals(cuenta, cuenta2);
            assertEquals(cuenta, cuenta2);
        }
    }


    @Nested
    @DisplayName("Probando las operaciones de la cuenta.")
    class CuentaOperacionesTest {
        @Tag("Cuenta")
        @Test
        void testDebitoCuenta() {
            Cuenta cuenta = new Cuenta("Alex", new BigDecimal("1000.12345"));
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }
        @Tag("Cuenta")
        @Test
        void testCreditoCuenta() {
            Cuenta cuenta = new Cuenta("Alex", new BigDecimal("1000.12345"));
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }
        @Tag("Banco")
        @Tag("Cuenta")
        @Test
        void testTransferirDineroCuentas() {
            Cuenta cuenta = new Cuenta("Jhon Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Jose Perez", new BigDecimal("1500.9090"));
            Banco banco = new Banco();
            banco.setNombre("Banco del estado");
            banco.transferir(cuenta2, cuenta, new BigDecimal(500));
            assertEquals("1000.9090", cuenta2.getSaldo().toPlainString());
            assertEquals("3000", cuenta.getSaldo().toPlainString());
        }
    }

    @Tag("Cuenta")
    @Tag("Error")
    @Test
    void testDineroInsuficienteException() {
        Cuenta cuenta = new Cuenta("Alex", new BigDecimal("1000.12345"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1001));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }

    @Tag("Cuenta")
    @Tag("Banco")
    @Test
    @DisplayName("Probando relaciones entre las cuentas y el banco con assertAll.")
    @Disabled
    void testRelacionBancoCuentas() {
        Cuenta cuenta = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Jose Perez", new BigDecimal("1500.9090"));
        Banco banco = new Banco();
        banco.add(cuenta);
        banco.add(cuenta2);
        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta, new BigDecimal(500));
        assertAll(
                () -> assertEquals("1000.9090", cuenta2.getSaldo().toPlainString(), () -> "El valor del saldo de la cuenta2 no es el esperado"),
                () -> assertEquals("3000", cuenta.getSaldo().toPlainString(), () -> "El valor del saldo de la cuenta1 no es el esperado"),
                () -> assertEquals(2, banco.getCuentas().size(), () -> "El banco no tiene las cuentas esperadas"),
                () -> assertEquals("Banco del estado", cuenta.getBanco().getNombre()),
                () -> assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Jose Perez"))),
                () -> {
                    assertEquals("Jose Perez", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Jose Perez"))
                            .findFirst().get().getPersona());
                }
        );
    }

    @Nested
    class SystemaOperativoTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {

        }
        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSololinuxMac() {

        }
        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }
    }

    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJDK8() {
        }
        @Test
        @EnabledOnJre(JRE.JAVA_17)
        void soloJDK17() {
        }
        @Test
        @DisabledOnJre(JRE.JAVA_17)
        void noJDK17() {
        }
    }

    @Nested
    class SistemPropertiesTest {
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + " : " + v));
        }
        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "17")
        void testJavaVersiona() {
        }
        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {
        }
        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testNo64() {
        }
        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "k1rard")
        void testIfUsername() {
        }
        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testIfDev() {
        }
    }

    @Nested
    class VariableAmbienteTest {
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> getEnv = System.getenv();
            getEnv.forEach((k, v) -> System.out.println(k + " : " + v));
        }
        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-17.*")
        void testJavaHome() {
        }
        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "4")
        void testProcesadores() {
        }
        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "DEV")
        void testEnv() {
        }
        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "PROD")
        void testProdDisabled() {
        }
        @Test
        @DisplayName("Probando el saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperado.")
        void testSaldoCuentaDev() {
            boolean esDev = "dev".equals(System.getProperty("ENV"));
            assumeTrue(esDev);
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.1234, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }
    @Test
    @DisplayName("Probando el saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperado.")
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.1234, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Nested
    class PruebasRepetitivas {
        @DisplayName("Probando Debito Cuenta Repetir")
        @RepeatedTest(value = 5, name = "{displayName} = Repeticion numero {currentRepetition} de {totalRepetitions}")
        void testDebitoCuentaRepetir(RepetitionInfo info) {
            if(info.getCurrentRepetition() == 3) {
                System.out.println("Es la repeticion numero " + info.getCurrentRepetition());
            }
            Cuenta cuenta = new Cuenta("Alex", new BigDecimal("1000.12345"));
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }
    }

    @Tag("param")
    @Nested
    class PruebasParametrizadas {
        @ParameterizedTest(name = "Es la repeticion numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "500", "700", "1000"})
        void testDebitoCuenta(String monto) {
            Cuenta cuenta = new Cuenta("Alex", new BigDecimal("1000.12345"));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
        @ParameterizedTest(name = "Es la repeticion numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,500", "4,700", "5,1000"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            Cuenta cuenta = new Cuenta("Alex", new BigDecimal("1000.12345"));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
        @ParameterizedTest(name = "Es la repeticion numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,Jhon,Jhon", "300,200,Pedro,Pedro", "600,500,Mario,Mario", "800,700,Carlos,Carlos", "2000,1000,Manuel,Manuel"})
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + " -> " + monto);
            Cuenta cuenta = new Cuenta(esperado, new BigDecimal(saldo));

            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
        @ParameterizedTest(name = "Es la repeticion numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            System.out.println(monto);
            Cuenta cuenta = new Cuenta("Alex", new BigDecimal("1000.12345"));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
        @ParameterizedTest(name = "Es la repeticion numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(monto);
            Cuenta cuenta = new Cuenta(esperado, new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
        @ParameterizedTest(name = "Es la repeticion numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @MethodSource("montoList")
        void testDebitoCuentaMetodoSource(String monto) {
            System.out.println(monto);
            Cuenta cuenta = new Cuenta("Alex", new BigDecimal("1000.12345"));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
        static List<String> montoList() {
            return Arrays.asList("100", "200", "300","400", "500", "800");
        }
    }

    @Tag("Timeout")
    @Nested
    class PruebasTimeout {
        @Test
        @Timeout(5)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(4);
        }
        @Test
        @Timeout(value = 2000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.SECONDS.sleep(3);
            });
        }
    }
}