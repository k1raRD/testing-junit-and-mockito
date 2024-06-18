package com.k1rard.appmockito.repositories;

import java.util.List;

public interface PreguntaRepository {
    List<String> findPreguntasByIdExamen(Long id);
    void guardarVarias(List<String> preguntas);
}
