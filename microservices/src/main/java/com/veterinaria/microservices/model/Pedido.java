package com.veterinaria.microservices.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "calle_envio", nullable = false, length = 100)
    private String calleEnvio;

    @Column(name = "numero_envio", nullable = false, length = 20)
    private String numeroEnvio;

    @Column(name = "depto_casa_envio", length = 50)
    private String deptoCasaEnvio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getCalleEnvio() {
        return calleEnvio;
    }

    public void setCalleEnvio(String calleEnvio) {
        this.calleEnvio = calleEnvio;
    }

    public String getNumeroEnvio() {
        return numeroEnvio;
    }

    public void setNumeroEnvio(String numeroEnvio) {
        this.numeroEnvio = numeroEnvio;
    }

    public String getDeptoCasaEnvio() {
        return deptoCasaEnvio;
    }

    public void setDeptoCasaEnvio(String deptoCasaEnvio) {
        this.deptoCasaEnvio = deptoCasaEnvio;
    }

}
