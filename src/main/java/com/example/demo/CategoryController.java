/*
package com.example.demo;
import com.example.demo.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/casebycase")
public class CategoryController{

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/submitCategory")
    public String submitCategory(@RequestParam("category") String selectedCategory, Model model) {

        // 선택된 카테고리에 대한 영문 이름(fileName)을 가져오기
        String fileName = categoryService.getFileNameByKoreanName(selectedCategory);

        // 파일 경로를 생성
        String filePath = "static/images/" + fileName + "/";

        // 랜덤으로 10개의 이미지 파일 선택
        List<String> randomImages = getRandomImages(filePath, 10);

        // 모델에 데이터 추가
        model.addAttribute("selectedCategory", selectedCategory);
        model.addAttribute("randomImages", randomImages);

        // 결과 페이지로 이동

        return "redirect:/casebycase/session1";

    }






    // 주어진 디렉토리에서 랜덤으로 파일을 선택하는 메서드
    private List<String> getRandomImages(String directoryPath, int count) {
        List<String> images = new ArrayList<>();
        File directory = new File(directoryPath);

        // 디렉토리에서 파일 목록 가져오기
        File[] fileList = directory.listFiles();

        if (fileList != null) {
            if (fileList.length <= count) {
                // 만약 파일이 count보다 적다면 모든 파일을 반환
                for (File file : fileList) {
                    images.add(file.getName());
                }
            } else {
                // 랜덤으로 count개의 파일 선택
                Random random = new Random();
                for (int i = 0; i < count; i++) {
                    int randomIndex = random.nextInt(fileList.length);
                    images.add(fileList[randomIndex].getName());
                }
            }
        }

        return images;
    }
}*/
