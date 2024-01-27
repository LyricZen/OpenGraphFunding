package com.example.testfunding.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Funding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private Integer goalAmount;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public Funding(String title,String content ,Integer goalAmount, Product product) {
        this.title = title;
        this.content = content;
        this.goalAmount = goalAmount;
        this.product = product;
    }
}
