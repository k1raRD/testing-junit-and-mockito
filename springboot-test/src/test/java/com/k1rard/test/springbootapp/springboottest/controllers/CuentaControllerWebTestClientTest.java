package com.k1rard.test.springbootapp.springboottest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.k1rard.test.springbootapp.springboottest.dto.TransaccionDTO;
import com.k1rard.test.springbootapp.springboottest.models.Cuenta;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integracion_wc")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerWebTestClientTest {

    @Autowired
    private WebTestClient client;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testDetalle() {
        // Given
        client.get().uri("/api/cuentas/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Alex")
                .jsonPath("$.saldo").isEqualTo(1000);

    }

    @Test
    @Order(2)
    void testDetalle2() {
        // Given
        client.get().uri("/api/cuentas/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(resp -> {
                    Cuenta cuenta = resp.getResponseBody();
                    assertEquals("Jose", cuenta.getPersona());
                    assertEquals("2000.00", cuenta.getSaldo().toPlainString());
                });

    }

    @Test
    @Order(3)
    void testTransferir() throws JsonProcessingException {
        // Given
        TransaccionDTO dto = new TransaccionDTO();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);
        dto.setMonto(new BigDecimal("100"));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con exito!");
        response.put("transaccion", dto);

        // When
        client.post().uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange().expectHeader().contentType(MediaType.APPLICATION_JSON)
                // Then
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(respuesta -> {
                    try {
                        JsonNode json = objectMapper.readTree(respuesta.getResponseBody());
                        assertEquals("Transferencia realizada con exito!", json.path("mensaje").asText());
                        assertEquals(1L, json.path("transaccion").path("cuentaOrigenId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals("100", json.path("transaccion").path("monto").asText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .jsonPath("$.mensaje").isNotEmpty()
                .jsonPath("$.mensaje").value(is("Transferencia realizada con exito!"))
                .jsonPath("$.mensaje").value(value -> assertEquals("Transferencia realizada con exito!", value))
                .jsonPath("$.mensaje").isEqualTo("Transferencia realizada con exito!")
                .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));
    }

    @Test
    @Order(4)
    void testDetalle3() throws JsonProcessingException {
        // Given
        Cuenta cuenta = new Cuenta(1L, "Alex", new BigDecimal("900"));
        client.get().uri("/api/cuentas/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Alex")
                .jsonPath("$.saldo").isEqualTo(900)
                .json(objectMapper.writeValueAsString(cuenta));

    }

    @Test
    @Order(5)
    void testDetalle4() {
        // Given
        client.get().uri("/api/cuentas/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(resp -> {
                    Cuenta cuenta = resp.getResponseBody();
                    assertEquals("Jose", cuenta.getPersona());
                    assertEquals("2100.00", cuenta.getSaldo().toPlainString());
                });

    }

    @Test
    @Order(6)
    void testListar() {
        client.get().uri("/api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].persona").isEqualTo("Alex")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].saldo").isEqualTo(900)
                .jsonPath("$[1].persona").isEqualTo("Jose")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].saldo").isEqualTo(2100)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));

    }

    @Test
    @Order(7)
    void testListar2() {
        client.get().uri("/api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(resp -> {
                    List<Cuenta> cuentas = resp.getResponseBody();
                    assertNotNull(cuentas);
                    assertFalse(cuentas.isEmpty());
                    assertEquals(2, cuentas.size());
                    assertEquals("Alex", cuentas.get(0).getPersona());
                    assertEquals(1L, cuentas.get(0).getId());
                    assertEquals("900.00", cuentas.get(0).getSaldo().toPlainString());
                    assertEquals("Jose", cuentas.get(1).getPersona());
                    assertEquals(2L, cuentas.get(1).getId());
                    assertEquals("2100.00", cuentas.get(1).getSaldo().toPlainString());
                })
                .hasSize(2)
                .value(hasSize(2));
    }

    @Test
    @Order(8)
    void testGuardar() {
        // Given
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        // When
        client.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
        // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.persona").isEqualTo("Pepe")
                .jsonPath("$.persona").value(is("Pepe"))
                .jsonPath("$.saldo").isEqualTo(3000);

    }

    @Test
    @Order(9)
    void testGuardar2() {
        // Given
        Cuenta cuenta = new Cuenta(null, "Pepa", new BigDecimal("4000"));
        // When
        client.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(resp -> {
                    Cuenta c = resp.getResponseBody();
                    assertNotNull(c);
                    assertEquals(4L, c.getId());
                    assertEquals("Pepa", c.getPersona());
                    assertEquals("4000", c.getSaldo().toPlainString());
                });
    }

    @Test
    @Order(10)
    void testEliminar() {
        client.get().uri("/api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(4);

        client.delete().uri("/api/cuentas/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        client.get().uri("/api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(3);

        client.get().uri("/api/cuentas/3")
//                .exchange().expectStatus().is5xxServerError();
                .exchange().expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}