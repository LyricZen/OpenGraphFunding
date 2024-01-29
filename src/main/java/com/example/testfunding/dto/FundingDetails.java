package com.example.testfunding.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FundingDetails {

    private String title;
    private String content;
    private int goalAmount;

    // 생성자, Getter, Setter 등 필요한 메서드를 추가할 수 있습니다.

    public FundingDetails() {
        // 기본 생성자
    }
}