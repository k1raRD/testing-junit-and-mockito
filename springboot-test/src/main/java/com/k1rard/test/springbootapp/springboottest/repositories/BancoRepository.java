package com.k1rard.test.springbootapp.springboottest.repositories;

import com.k1rard.test.springbootapp.springboottest.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BancoRepository extends JpaRepository<Banco, Long> {
}
