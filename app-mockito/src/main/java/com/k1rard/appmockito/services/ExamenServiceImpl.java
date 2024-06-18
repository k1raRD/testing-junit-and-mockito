package com.k1rard.appmockito.services;

import com.k1rard.appmockito.models.Examen;
import com.k1rard.appmockito.repositories.ExamenRepository;
import com.k1rard.appmockito.repositories.PreguntaRepository;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService {
    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository, PreguntaRepository preguntaRepository) {
        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
    }
    @Override
    public Optional<Examen> findByNombre(String nombre) {
        return this.examenRepository.findAll().stream()
                .filter(e -> e.getNombre().equals(nombre))
                .findFirst();
    }
    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Examen examen = null;
        Optional<Examen> optionalExamen = this.findByNombre(nombre);
        if (optionalExamen.isPresent()) {
            examen = optionalExamen.orElseThrow();
            List<String> preguntas = this.preguntaRepository.findPreguntasByIdExamen(examen.getId());
            this.preguntaRepository.findPreguntasByIdExamen(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if(!examen.getPreguntas().isEmpty()) {
            preguntaRepository.guardarVarias(examen.getPreguntas());
        }
        return examenRepository.guardar(examen);
    }
}
