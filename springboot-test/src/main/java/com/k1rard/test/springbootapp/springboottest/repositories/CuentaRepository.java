package com.k1rard.test.springbootapp.springboottest.repositories;

import com.k1rard.test.springbootapp.springboottest.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
   @Query("SELECT c FROM Cuenta c WHERE c.persona = :persona")
   Optional<Cuenta> findByPersona(String persona);
}
