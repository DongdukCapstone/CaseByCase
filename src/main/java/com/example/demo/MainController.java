package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/casebycase")
public class MainController {

    @GetMapping("/session1")
    public String session1() {
        return "content/session1";
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



    /*  -----------------------------------    */
    private final CategoryService categoryService;

    @Autowired
    public MainController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/submitCategory")
    public String submitCategory(@RequestParam("category") String selectedCategory, RedirectAttributes redirectAttributes) {

        // 선택된 카테고리에 대한 영문 이름(fileName)을 가져오기
        String fileName = categoryService.getFileNameByKoreanName(selectedCategory);

        // 파일 경로를 생성
        String filePath = "/images/" + fileName + "/";

        // 랜덤으로 10개의 이미지 파일 선택
        List<String> randomImages = getRandomImages(filePath, 10);

        // 로깅 추가
        System.out.println("Selected Category: " + selectedCategory);
        System.out.println("Path: " + filePath);
        System.out.println("Random Images: " + randomImages);

        // 모델에 데이터 추가
        redirectAttributes.addFlashAttribute("selectedCategory", selectedCategory);
        redirectAttributes.addFlashAttribute("randomImages", randomImages);

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
        }else {
            System.err.println("File list is null for directory: " + directoryPath);
        }

        String[] names = directory.list();
        if(directory.isDirectory()){
            for(String name: names){
                System.out.println(name);
            }
        }


        System.out.println("Directory Path: " + directoryPath);
        System.out.println("File List: " + Arrays.toString(fileList));
        System.out.println("Selected Random Images: " + images);


        return images;
    }

}




/*


        System.out.println("Directory Path: " + directoryPath);
        System.out.println("File List: " + Arrays.toString(fileList));
        System.out.println("Selected Random Images: " + images);


        if (fileList != null) {
            // 이미지 파일이라면 리스트에 추가
            for (File file : fileList) {
                if (file.isFile() && isImageFile(file)) {
                    images.add(file.getName());
                }
            }

            // 랜덤으로 count개의 이미지 선택
            if (images.size() <= count) {
                return images;
            } else {
                List<String> randomImages = new ArrayList<>();
                Random random = new Random();
                for (int i = 0; i < count; i++) {
                    int randomIndex = random.nextInt(images.size());
                    randomImages.add(images.get(randomIndex));
                }
                return randomImages;
            }
        } else {
            // 파일 리스트가 null인 경우 로깅
            System.err.println("File list is null for directory: " + directoryPath);
            return images;
        }
    }


    // 파일이 이미지 파일인지 확인하는 메서드
    private boolean isImageFile(File file) {
        String[] allowedExtensions = {"jpg", "jpeg", "png", "gif"}; // 확장자 추가 가능
        String fileName = file.getName().toLowerCase();
        for (String extension : allowedExtensions) {
            if (fileName.endsWith("." + extension)) {
                return true;
            }
        }

        return false;
    }

* */