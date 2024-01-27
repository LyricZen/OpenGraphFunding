package com.example.testfunding.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FundingProduct implements Serializable {

    private String productLink;
    private String productName;
    private String productImage;

    public FundingProduct() {
        // 기본 생성자
    }

    public FundingProduct(String productLink, String productName, String productImage) {
        this.productLink = productLink;
        this.productName = productName;
        this.productImage = productImage;
    }

}