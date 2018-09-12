package com.balceda.itlict.controller;

import com.balceda.itlict.util.CustomMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/v1")
public class DataCapController {

    public static final String IMAGES_FOLDER = "uploads/";

    @RequestMapping(value = "/loadImage",
            method = RequestMethod.POST,
            headers = ("content-type=multipart/form-data"))
    public ResponseEntity<byte[]> loadImage(@RequestParam("imageFile") MultipartFile file) {

        if (file.isEmpty() || file == null) {
            return new ResponseEntity(new CustomMessage("No valid file"), HttpStatus.CONFLICT);
        }

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        String dateName = dateFormat.format(date);
        try {
            String fileName = "itlict_v1_" + dateName + "." + file.getContentType().split("/")[1];
            byte[] bytes = file.getBytes();
            Path path = Paths.get(IMAGES_FOLDER + fileName);
            Files.write(path, bytes);
            return new ResponseEntity(new CustomMessage("Upload complete: " + path), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity(new CustomMessage("Error occurred during upload: " + e.getMessage()), HttpStatus.CONFLICT);
        }
    }
}
