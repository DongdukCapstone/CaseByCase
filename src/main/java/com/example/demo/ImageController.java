package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/casebycase/mapping")
public class ImageController {

    private List<String> imgNumList;

    @PostMapping("/selectImages")
    public String selectImages(
            @RequestParam(required = true) String img_num,
            RedirectAttributes redirectAttributes, HttpSession session) {
        List<String> imgNumList = Arrays.asList(img_num.split("[,\\s]+"));

        session.setAttribute("imgNumList", imgNumList);

        System.out.println("imgnumlist 확인중: " + imgNumList);

        String selectedCategory = (String) session.getAttribute("selectedCategory");
        List<String> randomImages = (List<String>) session.getAttribute("randomImages");
        String fileName = (String) session.getAttribute("fileName");

        System.out.println("selectedCategory 확인중: " + selectedCategory);
        // ex) selectedCategory 확인 중: 애니메이션
        System.out.println("randomImages 확인중: " + randomImages);
        // randomImages 확인중: [699.jpg, 2117.jpg, 2455.jpg, 1053.jpg, 581.jpg, 1144.jpg, 82.jpg, 286.jpg, 192.jpg, 225.jpg]
        System.out.println("fileName 확인중: " + fileName);
        // fileName 확인중: Animation
        System.out.println("Session 확인중: " + session);
        return "forward:/casebycase/session1";
    }

    private final ObjectMapper objectMapper;

    @PostMapping("/request")
    public String toPython(Model model, HttpSession session,
            @RequestParam("user_file") MultipartFile file)
            throws IOException , InterruptedException, JsonProcessingException {


        try {
            //헤더를 JSON으로 설정함
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 파일 저장 디렉토리 지정 (원하는 경로로 변경)
            String uploadDir = "src/main/resources/static/images/uploadDir/";
            // 원본 파일 이름 추출
            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            // 저장할 파일 경로 설정
            String filePath = uploadDir + originalFileName;
            // 파일 저장
            FileCopyUtils.copy(file.getBytes(), new File(filePath));

            List<String> imgNumList = (List<String>) session.getAttribute("imgNumList");
            String selectedCategory = (String) session.getAttribute("selectedCategory");

            System.out.println("imgNumList 확인중2: " + imgNumList);
            System.out.println("selectedCategory 확인중2: " + selectedCategory);

            // Python 스크립트에 데이터 전달 및 실행
            String pythonScriptPath = "C:\\Users\\82105\\CaseFolder\\Case\\src\\main\\python\\A_code.py";
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python", pythonScriptPath, selectedCategory, String.join(",", imgNumList), filePath);

            // Python 스크립트에 이미지 데이터를 전송
            Process process = processBuilder.start();


            // Python 스크립트 실행이 완료될 때까지 대기
            int exitCode = process.waitFor();

            System.out.println("exitCode 확인중: " + exitCode);

            if (exitCode == 0) {
                // 성공적으로 실행된 경우
                // Python 스크립트의 출력을 읽어옴
                String result = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

                // 결과 처리 로직 추가 (result 변수에 결과 값이 저장됨)
                System.out.println("Python script result: " + result);

                // 성공 페이지로 리다이렉트 또는 결과 반환
                model.addAttribute("top10", result);

                // 성공 페이지로 리다이렉트 또는 결과 반환
                return "redirect:/casebycase/session1";
            } else {
                // 실패한 경우
                // 에러 메시지를 읽어옴
                String errorMessage = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

                // 에러 메시지 처리 로직 추가 (errorMessage 변수에 에러 메시지가 저장됨)
                System.out.println("Python script error: " + errorMessage);

                // 실패 페이지로 리다이렉트 또는 에러 메시지 반환
                return "redirect:/error";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            // 실패 페이지로 리다이렉트 또는 에러 메시지 반환
            return "redirect:/error";
        }
    }
}

