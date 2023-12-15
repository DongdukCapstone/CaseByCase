package com.example.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.util.*;

@Controller
@RequestMapping("/casebycase/mapping")
public class ImageController {

    private List<String> imgNumList;

    @PostMapping("/selectImages")
    public String selectImages(
            @RequestParam(required = true) String img_num,
            RedirectAttributes redirectAttributes, HttpSession session) {
        List<String> imgNumList = Arrays.asList(img_num.split("[,\\s]+"));

        // Session에 imgNumList를 저장
        session.setAttribute("imgNumList", imgNumList);

        System.out.println("imgnumlist 확인중: " + imgNumList);

        // Session에서 다른 속성들을 읽어와서 다시 저장

        String selectedCategory = (String) session.getAttribute("selectedCategory");
        List<String> randomImages = (List<String>) session.getAttribute("randomImages");
        String fileName = (String) session.getAttribute("fileName");

        System.out.println("selectedCategory 확인중: " + selectedCategory);
        System.out.println("randomImages 확인중: " + randomImages);
        System.out.println("fileName 확인중: " + fileName);
        System.out.println("Session 확인중: " + session);

        return "redirect:/casebycase/session1";
    }

    // 아래부분 필요없음, 파일타입은 세션에 담기에 너무 커서 업로드 없애고 request에 한번에 담아주는게 나음

    // // 이미지 업로드 폼에서 호출하는 엔드포인트
    // @PostMapping("/upload")
    // public String handleFileUpload(@RequestParam("user_file") MultipartFile file) {
    //     // 이미지 업로드를 처리하는 로직을 작성
    //     try {
    //         // 임시 파일로 저장하지 않고 MultipartFile을 직접 전송
    //         byte[] fileBytes = file.getBytes();

    //         System.out.println("파일 업로드 성공");
    //         //redirect to session1
    //         return "redirect:/casebycase/session1";
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         System.out.println("파일 업로드 실패");
    //         return "redirect:/casebycase/session1";
    //     }
    // }

    @PostMapping("/request")
    public ResponseEntity<String> toPython(
            Model model, HttpSession session, @RequestParam("user_file") MultipartFile file,
            UriComponentsBuilder uriComponentsBuilder) throws IOException, InterruptedException {
        try {

            List<String> imgNumList = (List<String>) session.getAttribute("imgNumList");
            String selectedCategory = (String) session.getAttribute("selectedCategory");

            System.out.println("imgNumList 확인중2: " + imgNumList);
            System.out.println("selectedCategory 확인중2: " + selectedCategory);

            // // Model을 사용하여 imgNumList 값을 가져옴
            // List<String> imgNumList = (List<String>) model.getAttribute("imgNumList");

            // get relative path of src/main/python/A_code.py from current file

            // Python 스크립트에 데이터 전달 및 실행
            String pythonScriptPath = "src/main/python/A_code.py"; //TODO: 상대경로로 수정
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python", pythonScriptPath, selectedCategory, String.join(",", imgNumList));

            // Python 스크립트에 이미지 데이터를 전송
            Process process = processBuilder.start();

            // 이미지 번호 목록을 전송
            /*
             * try (ObjectOutputStream objectOutputStream = new
             * ObjectOutputStream(process.getOutputStream())) {
             * objectOutputStream.writeObject(imgNumList);
             * }
             */

            // %
            // %

            // %
            // %
            // %
            // %
            // 여기 아래 부터 뭘 하려는지 모르겠음
            // %
            // %
            // %
            // %
            // %
            // %
            // %





            // 파일 데이터를 전송
            try (InputStream inputStream = file.getInputStream();
                 OutputStream outputStream = process.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                // IOException 처리: 파일 I/O 관련 예외
                e.printStackTrace();
            } catch (SecurityException e) {
                // SecurityException 처리: 보안 관련 예외
                e.printStackTrace();
            } catch (Exception e) {
                // 그 외의 예외 처리
                e.printStackTrace();
            }

            // try (InputStream inputStream = file.getInputStream();
            //         OutputStream outputStream = process.getOutputStream()) {
            //     byte[] buffer = new byte[1024];
            //     int bytesRead;
            //     while ((bytesRead = inputStream.read(buffer)) != -1) {
            //         outputStream.write(buffer, 0, bytesRead);
            //     }
            // }

            // 프로세스 실행 결과에 따라 응답 처리
            int exitCode = process.waitFor();

            System.out.println("exitCode 확인중: " + exitCode);

            if (exitCode == 0) {
                // Python 스크립트의 결과를 읽어옴
                List<String> pythonScriptOutput = readPythonScriptOutput(process);

                List<String> top10Images = new ArrayList<>();

                System.out.println("pythonScriptOutput 확인중: " + pythonScriptOutput);

                boolean foundTop10Header = false;

                // pythonScriptOutput을 이용하여 필요한 작업 수행
                for (String line : pythonScriptOutput) {
                    if (foundTop10Header) {
                        // "Top 10 유사도 값:" 이후의 라인은 이미지 정보로 간주
                        top10Images.add(line);

                        // 상위 10개 이미지 정보를 읽었다면 반복문 종료
                        if (top10Images.size() >= 10)
                            break;
                    }

                    if (line.startsWith("Top 10 유사도 값:")) {
                        // 해당 라인에서 필요한 작업을 수행 (예: 다음 라인부터 10개 이미지 정보를 읽음)
                        foundTop10Header = true;
                    }
                }

                System.out.println("top10Images 확인중: " + top10Images);
                return ResponseEntity.ok("ok");
            } else {
                return ResponseEntity.status(500).body("여기 error");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("this...error");
        }
    }

    private List<String> readPythonScriptOutput(Process process) throws IOException {
        List<String> outputLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                outputLines.add(line);
            }
        }

        return outputLines;
    }

}
