package com.k1rard.test.springbootapp.springboottest.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bancos")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Banco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(name = "total_transferencias")
    private int totalTransferencias;
}
