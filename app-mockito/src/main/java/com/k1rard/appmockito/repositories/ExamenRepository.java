package com.k1rard.appmockito.repositories;

import com.k1rard.appmockito.models.Examen;

import java.util.List;

public interface ExamenRepository {
    Examen guardar(Examen examen);
    List<Examen> findAll();
}
