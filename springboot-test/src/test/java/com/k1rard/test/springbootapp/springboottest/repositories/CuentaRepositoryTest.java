package com.k1rard.test.springbootapp.springboottest.repositories;

import com.k1rard.test.springbootapp.springboottest.models.Cuenta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CuentaRepositoryTest {
    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Alex", cuenta.orElseThrow().getPersona());
    }

    @Test
    void testFindByPersona() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Alex");
        assertTrue(cuenta.isPresent());
        assertEquals("Alex", cuenta.orElseThrow().getPersona());
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByPersonaThrowException() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("sdasdas");
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
    }

    @Test
    void testSave() {
        // Given
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        // When
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);
//        Cuenta cuenta = cuentaRepository.findByPersona(cuentaPepe.getPersona()).orElseThrow();
//        Cuenta cuenta = cuentaRepository.findById(cuentaSave.getId()).orElseThrow();

        // Then
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
//        assertEquals(3, cuenta.getId());
    }

    @Test
    void testUpdate() {
        // Given
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        // When
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);

        // Then
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());

        // When
        cuenta.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaUpdate = cuentaRepository.save(cuenta);

        // Then
        assertEquals("Pepe", cuentaUpdate.getPersona());
        assertEquals("3800", cuentaUpdate.getSaldo().toPlainString());
    }

    @Test
    void testDelete() {
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("Jose", cuenta.getPersona());

        cuentaRepository.delete(cuenta);

        assertThrows(NoSuchElementException.class, () -> {
            cuentaRepository.findByPersona("Jose").orElseThrow();
        });
        assertEquals(1, cuentaRepository.findAll().size());
    }
}