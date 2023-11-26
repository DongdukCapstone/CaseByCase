package com.example.demo;

public enum Category {
    ANIMATION("애니메이션", "Animation"),
    FASHION("패션", "Fashion"),
    MODERN("모던", "Modern"),
    GAME("게임", "Game"),
    ARTISTIC("아티스틱", "Art"),
    GRAPHIC("그래픽", "Graphic"),
    Y2K("Y2K", "Y2K");

    private final String koreanName;
    private final String fileName;

    Category(String koreanName, String fileName) {
        this.koreanName = koreanName;
        this.fileName = fileName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public String getFileName() {
        return fileName;
    }
}
