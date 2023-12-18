package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Controller
@RequestMapping("/casebycase/mapping")
public class ImageController {

    private List<String> imgNumList;

    @PostMapping("/selectImages")
    public String selectImages(
            @RequestParam(required = true) String img_num,
            RedirectAttributes redirectAttributes, HttpSession session
            , Model model) {
        List<String> imgNumList = Arrays.asList(img_num.split("[,\\s]+"));
        session.setAttribute("imgNumList", imgNumList);
        System.out.println("imgnumlist 확인중: " + imgNumList);

        String selectedCategory = (String) session.getAttribute("selectedCategory");
        List<String> randomImages = (List<String>) session.getAttribute("randomImages");
        String fileName = (String) session.getAttribute("fileName");

        // 리다이렉트 시 데이터 전달
        redirectAttributes.addFlashAttribute("selectedCategory", selectedCategory);
        redirectAttributes.addFlashAttribute("randomImages", randomImages);
        redirectAttributes.addFlashAttribute("fileName", fileName);
        redirectAttributes.addFlashAttribute("imgNumList", imgNumList);

        System.out.println("selectedCategory 확인중: " + selectedCategory);
        // ex) selectedCategory 확인 중: 애니메이션
        System.out.println("randomImages 확인중: " + randomImages);
        // randomImages 확인중: [699.jpg, 2117.jpg, 2455.jpg, 1053.jpg, 581.jpg, 1144.jpg, 82.jpg, 286.jpg, 192.jpg, 225.jpg]
        System.out.println("fileName 확인중: " + fileName);
        // fileName 확인중: Animation
        System.out.println("Session 확인중: " + session);
        return "redirect:/casebycase/session1";
    }

    private final ObjectMapper objectMapper;

    @PostMapping("/request")
    public String toPython(Model model, HttpSession session,
                           RedirectAttributes redirectAttributes,
                           @RequestParam("user_file") MultipartFile file)
            throws IOException, InterruptedException, JsonProcessingException {
        List<String> randomImages = (List<String>) session.getAttribute("randomImages");
        List<String> imgNumList = (List<String>) session.getAttribute("imgNumList");
        String selectedCategory = (String) session.getAttribute("selectedCategory");
        String fileName = (String) session.getAttribute("fileName");

        System.out.println("randomImages 확인중2: " + randomImages);
        System.out.println("fileName 확인중2: " + fileName);
        System.out.println("imgNumList 확인중2: " + imgNumList);
        System.out.println("selectedCategory 확인중2: " + selectedCategory);
        // 리다이렉트 시 데이터 전달
        redirectAttributes.addFlashAttribute("selectedCategory", selectedCategory);
        redirectAttributes.addFlashAttribute("randomImages", randomImages);
        redirectAttributes.addFlashAttribute("fileName", fileName);
        redirectAttributes.addFlashAttribute("imgNumList", imgNumList);

        /*
         *
         *
         * 파이썬 보내기
         *
         *
         * */

        try {


            //헤더를 JSON으로 설정함
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 파일 저장 디렉토리 지정 (원하는 경로로 변경)
            String uploadDir = "src/main/resources/static/images/uploadDir/";
            // 원본 파일 이름 추출
            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            // 저장할 파일 경로 설정 및 저장
            String filePath = uploadDir + originalFileName;
            FileCopyUtils.copy(file.getBytes(), new File(filePath));

            // C:\Users\82105\CaseFolder\Case\src\main\python\A_code.py
            // Python 스크립트에 데이터 전달 및 실행 src/main/python/A_code.py
            String pythonExecutable = "C:\\Users\\82105\\anaconda3\\python.exe";
            String pythonScriptPath = "src/main/python/A_code.py";
            String selected_imgs = String.join(",", imgNumList);
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pythonExecutable, "-u", pythonScriptPath, selectedCategory, selected_imgs, filePath); //String.join(",", imgNumList), filePath);


            System.out.println("processBuilder.start() starts!!!");

            // Python 스크립트에 이미지 데이터를 전송
            Process process = processBuilder.start();


            // Python 스크립트 실행이 완료될 때까지 대기
// 출력 및 에러 스트림을 비동기로 처리
            StringBuilder resultBuilder = new StringBuilder();
// CompletableFuture가 완료될 때까지 대기
            String[] pythonScriptResult = {null};
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while (!reader.readLine().equals("Top10_SELECT")) {
                    }
                    // "Top10_SELECT" 이후의 출력을 읽어오기
                    while ((line = reader.readLine()) != null) {
                        resultBuilder.append(line).append("\n");
                    }

                    // 이제 resultBuilder.toString()에는 Python 스크립트의 결과 문자열이 저장되어 있습니다.
                    pythonScriptResult[0] = resultBuilder.toString();
                    System.out.println("최종");
                    System.out.println(pythonScriptResult[0]);
// 파싱하여 10개의 값을 추출
                    List<String> top10 = parseTop10(pythonScriptResult[0]);
/*
                    List<Double> imageCosList = extractImageCosList(pythonScriptResult[0]);
*/

                    // 모델에 top10을 추가
                    model.addAttribute("top10", top10);

                    redirectAttributes.addFlashAttribute("top10", top10);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("Waiting...");
            // 프로세스의 종료를 기다림
            int exitCode = process.waitFor();
            System.out.println("exitCode");
            System.out.println(exitCode);
    /*
    *
*/
        // CompletableFuture의 완료를 기다림
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
            redirectAttributes.addFlashAttribute("top10", model.getAttribute("top10"));
            return "redirect:/casebycase/session1";


        } catch (IOException | InterruptedException e) {
        }
        return "redirect:/error";
    }

    private List<String> parseTop10(String pythonScriptResult) {
        List<String> result = new ArrayList<>();

        // 정규표현식을 사용하여 작은 따옴표 안에 있는 값을 추출
        String regex = "'(.*?)'";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pythonScriptResult);

        while (matcher.find()) {
            String value = matcher.group(1);
            result.add(value);
        }

        return result;
    }

/*    private static List<Double> extractImageCosList(String pythonScriptResult) {
        List<Double> imageCosList = new ArrayList<>();

        // 정규표현식을 사용하여 숫자 부분 추출
        Pattern pattern = Pattern.compile("\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(pythonScriptResult);

        while (matcher.find()) {
            String match = matcher.group();
            double value = Double.parseDouble(match);
            imageCosList.add(value);
        }

        return imageCosList;
    }*/
}

