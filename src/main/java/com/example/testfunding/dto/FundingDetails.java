package com.example.testfunding.dto;

public class FundingDetails {

    private String title;
    private String content;
    private Integer goalAmount;

    // 생성자, Getter, Setter 등 필요한 메서드를 추가할 수 있습니다.

    public FundingDetails() {
        // 기본 생성자
    }

    public FundingDetails(String title, String content, Integer goalAmount) {
        this.title = title;
        this.content = content;
        this.goalAmount = goalAmount;
    }

    // Getter, Setter 등 필요한 메서드를 추가하세요.

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(Integer goalAmount) {
        this.goalAmount = goalAmount;
    }
}