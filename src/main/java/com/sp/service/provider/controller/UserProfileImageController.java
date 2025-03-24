package com.sp.service.provider.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

@RestController
@RequestMapping("/api/user")
public class UserProfileImageController {

    @Value("${profile.images.path}")
    private String profileImagesPath;

    @GetMapping("/profileImage/{imageName}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable("imageName") String imageName) throws IOException {
        byte[] imageBytes = Files.readAllBytes(Paths.get(profileImagesPath + imageName));
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)  // Assuming the images are JPEG. You can customize the content type.
                .body(imageBytes);
    }
}

