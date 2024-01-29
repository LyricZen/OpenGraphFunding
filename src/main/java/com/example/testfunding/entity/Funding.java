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
    private String productName;
    private String productImage;
    private String productLink;

    public Funding(String title,String content ,Integer goalAmount, String productName, String productImage) {
        this.title = title;
        this.content = content;
        this.goalAmount = goalAmount;
        this.productName = productName;
        this.productImage = productImage;
    }

    public Funding(String productLink, String productName, String productImage) {
        this.productLink = productLink;
        this.productName = productName;
        this.productImage = productImage;
    }
}
