package com.k1rard.appmockito.services;

import com.k1rard.appmockito.models.Examen;

import java.util.List;
import java.util.Optional;

public interface ExamenService {
    Optional<Examen> findByNombre(String nombre);
    Examen findExamenPorNombreConPreguntas(String nombre);
    Examen guardar(Examen examen);
}
