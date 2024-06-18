package com.k1rard.test.springbootapp.springboottest.models;

import com.k1rard.test.springbootapp.springboottest.exceptions.DineroInsificienteException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cuentas")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String persona;
    private BigDecimal saldo;
    public void debito(BigDecimal monto) {
        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        if(nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new DineroInsificienteException("Dinero insuficiente en la cuenta.");
        }
        this.saldo = nuevoSaldo;
    }
    public void credito(BigDecimal monto) {
        this.saldo = this.saldo.add(monto);
    }
}
