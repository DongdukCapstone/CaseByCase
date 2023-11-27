package com.example.demo;

import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    public String getFileNameByKoreanName(String koreanName) {
    for (Category category : Category.values()) {
        if (category.getKoreanName().equals(koreanName)) {
            return category.getFileName();
        }
    }

        // 일치하는 카테고리가 없는 경우 예외 처리
        System.out.println("DEBUG: Invalid category input - " + koreanName);
    throw new IllegalArgumentException("Invalid category: " + koreanName);
}





}
