package com.example.demo;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/casebycase/mapping")
public class ApiController{

    private List<String> imgNumList;

    // 이미지 선택 폼에서 호출하는 엔드포인트
    @PostMapping("/selectImages")
    public ResponseEntity<String> selectImages(
            @RequestParam(required = true) String img_num
    ) {

        // img_num을 빈칸이나 ,로 나누어 리스트 형태로 변환
        imgNumList = Arrays.asList(img_num.split("[,\\s]+"));

        return ResponseEntity.ok("Images selected successfully");
    }

    // 이미지 업로드 폼에서 호출하는 엔드포인트
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("user_file") MultipartFile file) {
        // 이미지 업로드를 처리하는 로직을 작성
        try {
            // 임시 파일로 저장하지 않고 MultipartFile을 직접 전송
            byte[] fileBytes = file.getBytes();

            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading file");
        }
    }


    @PostMapping("/request")
    public ResponseEntity<String> toPython(
            @RequestParam(required = true) String selectedCategory,
            @RequestParam(required = true) String img_num,
            @RequestParam("user_file") MultipartFile file,
            UriComponentsBuilder uriComponentsBuilder
    )throws IOException, InterruptedException {
            try {

                // Python 스크립트에 데이터 전달 및 실행
                String pythonScriptPath = "src/main/python/A_code.py";
                ProcessBuilder processBuilder = new ProcessBuilder(
                        "python", pythonScriptPath, selectedCategory, String.join(",",imgNumList));

                // Python 스크립트에 이미지 데이터를 전송
                Process process = processBuilder.start();

                // 이미지 번호 목록을 전송
                /*
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(process.getOutputStream())) {
                    objectOutputStream.writeObject(imgNumList);
                } */

                // 파일 데이터를 전송
                try (InputStream inputStream = file.getInputStream();
                     OutputStream outputStream = process.getOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }



                // 프로세스 실행 결과에 따라 응답 처리
                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    // Python 스크립트의 결과를 읽어옴
                    List<String> pythonScriptOutput = readPythonScriptOutput(process);

                    // pythonScriptOutput을 이용하여 필요한 작업 수행
                    for (String line : pythonScriptOutput) {
                        if (line.startsWith("Top 10 유사도 값:")) {
                            // 해당 라인에서 필요한 작업을 수행 (예: 다음 라인부터 10개 이미지 정보를 읽음)
                            // ...

                            break;  // 필요한 정보를 읽었다면 반복문 종료
                        }
                    }
                    return ResponseEntity.ok("ok");
                }else {
                    return ResponseEntity.status(500).body("error");
                }

            }catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("error");
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
