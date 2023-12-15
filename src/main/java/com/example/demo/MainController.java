package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpSession;

import java.io.File;

import java.util.*;

@Controller
@RequestMapping("/casebycase")
public class MainController {

    @GetMapping("/session1")
    public String session1(HttpSession session, Model model) {
        // 세션에서 데이터 읽기
        String selectedCategory = (String) session.getAttribute("selectedCategory");
        List<String> randomImages = (List<String>) session.getAttribute("randomImages");
        String fileName = (String) session.getAttribute("fileName");
        List<String> imgNumList = (List<String>) session.getAttribute("imgNumList");
        // 모델에 데이터 추가
        model.addAttribute("selectedCategory", selectedCategory);
        model.addAttribute("randomImages", randomImages);
        model.addAttribute("fileName", fileName);
        model.addAttribute("imgNumList", imgNumList);

        return "content/session1";
    }

    @PostMapping("/session1")
    public String handlePostRequest() {
        // Handle the POST request here
        return "redirect:/casebycase/session1";
    }

    @GetMapping("/session2")
    public String session2() {
        return "content/session2";
    }

    @RequestMapping("/error")
    public String handleError() {
        // Provide a custom error view or redirect to a different page.
        return "error";
    }





    private final CategoryService categoryService;


    @Autowired
    public MainController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }




    @PostMapping("/submitCategory")
    public String submitCategory(@RequestParam("category") String selectedCategory, RedirectAttributes redirectAttributes, HttpSession session) {
        // 선택된 카테고리에 대한 영문 이름(fileName)을 가져오기
        String fileName = categoryService.getFileNameByKoreanName(selectedCategory);

        System.out.println("fileName 확인하자: " + fileName);

        // 파일 경로를 생성
        String filePath = "src/main/resources/static/images/" + fileName + "/";
        // 랜덤으로 10개의 이미지 파일 선택
        List<String> randomImages = getRandomImages(filePath, 10);

        // 로깅 추가
        System.out.println("Selected Category: " + selectedCategory);
        System.out.println("Path: " + filePath);
        System.out.println("Random Images: " + randomImages);

        session.setAttribute("filePath", filePath);
        session.setAttribute("selectedCategory", selectedCategory);
        session.setAttribute("randomImages", randomImages);
        session.setAttribute("fileName", fileName);


        // 모델에 데이터 추가
        redirectAttributes.addFlashAttribute("selectedCategory", selectedCategory);
        redirectAttributes.addFlashAttribute("randomImages", randomImages);
        redirectAttributes.addFlashAttribute("fileName", fileName);

        // 결과 페이지로 이동
        return "redirect:/casebycase/session1";

    }


    // 주어진 디렉토리에서 랜덤으로 파일을 선택하는 메서드
    private List<String> getRandomImages(String directoryPath, int count) {
        List<String> images = new ArrayList<>();
        File directory = new File(directoryPath);

        System.out.println("directory: " + directory);

        // 디렉토리에서 파일 목록 가져오기
        // File[] fileList = directory.listFiles();


        // 디렉토리가 존재하는지 확인
        File[] fileList = new File[0];
        if (directory.exists() && directory.isDirectory()) {
            // 디렉토리에서 파일 목록 가져오기
            fileList = directory.listFiles();

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
            } else {
                System.err.println("File list is null for directory: " + directoryPath);
            }
        } else {
            System.err.println("Directory does not exist: " + directoryPath);
        }

        System.out.println("Directory Path: " + directoryPath);
        System.out.println("File List: " + Arrays.toString(fileList));
        System.out.println("Selected Random Images: " + images);

        return images;

    }

    @PostMapping("/reloadImages")
    public String reloadImages(@ModelAttribute("selectedCategory") String selectedCategory,
                               RedirectAttributes redirectAttributes) {
        // 기존에 선택된 카테고리와 동일한 방식으로 랜덤 이미지 생성
        String fileName = categoryService.getFileNameByKoreanName(selectedCategory);
        String filePath = "src/main/resources/static/images/" + fileName + "/";
        List<String> randomImages = getRandomImages(filePath, 10);

        // 로깅 추가
        System.out.println("Reloaded Random Images: " + randomImages);

        // 모델에 데이터 추가

        redirectAttributes.addFlashAttribute("selectedCategory", selectedCategory);
        redirectAttributes.addFlashAttribute("randomImages", randomImages);
        redirectAttributes.addFlashAttribute("fileName", fileName);
        // 결과 페이지로 이동
        return "redirect:/casebycase/session1";
    }







}




