package com.example.fullsite;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/video")
@RequiredArgsConstructor
public class VideoController {

    @PostMapping(
            value = "/transcode",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> transcode(@RequestParam("file") MultipartFile file) throws Exception {

        Path input = Files.createTempFile("input-", ".mp4");
        Path output = Files.createTempFile("output-", ".mp4");

        file.transferTo(input.toFile());

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y",
                "-i", input.toString(),
                "-vcodec", "libx264",
                "-acodec", "aac",
                output.toString()
        );

        pb.inheritIO().start().waitFor();

        return ResponseEntity.ok("Transcoding complete: " + output.toString());
    }
}
