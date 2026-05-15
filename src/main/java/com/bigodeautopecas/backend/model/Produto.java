package com.bigodeautopecas.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private Double preco;

    @Column(nullable = false)
    private Integer estoque;

    private String categoria;
    private String marca;

    /** Modelo de veículo compatível (ex.: "Gol G4", "Civic 2018-2022") */
    private String modelo;

    /** URL da imagem do produto exibida no catálogo e na página de detalhes */
    private String imagemUrl;
}
