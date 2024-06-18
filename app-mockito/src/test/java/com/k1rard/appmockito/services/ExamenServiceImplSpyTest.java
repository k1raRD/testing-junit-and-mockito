package com.k1rard.appmockito.services;

import com.k1rard.appmockito.Datos;
import com.k1rard.appmockito.models.Examen;
import com.k1rard.appmockito.repositories.ExamenRepository;
import com.k1rard.appmockito.repositories.ExamenRepositoryImpl;
import com.k1rard.appmockito.repositories.PreguntaRepository;
import com.k1rard.appmockito.repositories.PreguntaRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {

    @Spy
    ExamenRepositoryImpl examenRepository;
    @Spy
    PreguntaRepositoryImpl preguntaRepository;
    @InjectMocks
    ExamenServiceImpl examenService;

    @Test
    void testSpy() {
//        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
//        PreguntaRepository preguntaRepository = spy(PreguntaRepositoryImpl.class);
//        ExamenService examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);

        List<String> preguntas = Arrays.asList("Aritmetica");
//        when(preguntaRepository.findPreguntasByIdExamen(anyLong())).thenReturn(preguntas);
        doReturn(Datos.PREGUNTAS).when(preguntaRepository).findPreguntasByIdExamen(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Geometria"));

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasByIdExamen(anyLong());
    }
}