package com.balceda.itlict.controller;

import com.balceda.itlict.domain.DatacapLogonInfo;
import com.balceda.itlict.domain.TransactionProps;
import com.balceda.itlict.service.DatacapService;
import com.balceda.itlict.util.CustomMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

@Controller
@RequestMapping("/v1")
public class DataCapController {

    public static final String IMAGES_FOLDER = "uploads/";

    @Autowired
    private DatacapService datacapservice;

    @RequestMapping(value = "/loadImage",
            method = RequestMethod.POST,
            headers = ("content-type=multipart/form-data"))
    public ResponseEntity loadImage(@RequestParam("imageFile") MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>(new CustomMessage("No valid file"), HttpStatus.CONFLICT);
        }

        try {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            String dateName = dateFormat.format(date);

            String fileName = "itlict_v1_" + dateName + "." + file.getContentType().split("/")[1];
            byte[] bytes = file.getBytes();
            Path path = Paths.get(IMAGES_FOLDER + fileName);
            Files.write(path, bytes);

            return new ResponseEntity<>(new CustomMessage("Upload complete: " + path), HttpStatus.OK);
            //return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
        } catch (Exception e) {
            return new ResponseEntity<>(new CustomMessage("Error occurred during upload: " + e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/datacap",
            method = RequestMethod.POST,
            headers = ("content-type=multipart/form-data"))
    public String datacapService(@RequestParam("imageFile") MultipartFile file) {

        if (file.isEmpty()) {
            return new CustomMessage("No valid file").getMessage();
        }

        RestTemplate client = new RestTemplate();
        DatacapLogonInfo logon = new DatacapLogonInfo();
        logon.setApplication("Transaction");
        logon.setStation("1");
        logon.setUser("admin");
        logon.setPassword("admin");

        HttpHeaders httpHeaders = datacapservice.logon(client, logon);
        /*
        Iterator iterator = httpHeaders.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            System.out.println("Key: " + key);
            System.out.println("Value: " + httpHeaders.get(key));
        }
        */

        String tranid = datacapservice.startTran(client, httpHeaders);
        System.out.println("tranid=[" + tranid + "]");
        tranid = tranid.replace("\"", "");
        System.out.println("now tranid=[" + tranid + "]");

        String tranrst1;
        String tranrst2;

        try {
            tranrst1 = datacapservice.setFileTran(client, httpHeaders, tranid, "xml", file);
            tranrst2 = datacapservice.setFileTran(client, httpHeaders, tranid, "imageFile", file);
        } catch (Exception e) {
            System.out.println("Error setting file...");
        }
        TransactionProps transactionProps = new TransactionProps();
        transactionProps.setApplication("Transaction");
        transactionProps.setPageFile("VScan.xml");
        transactionProps.setTransactionId(tranid);
        transactionProps.setWorkflow("Transaction");
        transactionProps.setRulesets("RecognizePage");
        transactionProps.setTaskProfile("Recognize");

        long ticker1 = System.currentTimeMillis();

        String result = datacapservice.executeTran(client, transactionProps, httpHeaders);

        long ticker2 = System.currentTimeMillis();

        System.out.println("Completed, it took " + (ticker2 - ticker1) / 1000 + " s");
        System.out.println(result);
        String outputhtml = "";
        try {
            outputhtml = datacapservice.getFileTran(client, tranid);
        } catch (Exception e) {
            System.out.println("Error getting file...");
        }
        /*
        OCRResult ocrResult = new OCRResult();
        ocrResult.setOutputhtml(outputhtml);
        JSONUtil jsonUtil = new JSONUtil();
        */
        datacapservice.logout(client, logon, httpHeaders);

        return outputhtml;
    }


    @RequestMapping(value = "/test",
            method = RequestMethod.POST)
    public String datacapTest() {

        RestTemplate client = new RestTemplate();
        DatacapLogonInfo logon = new DatacapLogonInfo();
        logon.setApplication("Transaction");
        logon.setStation("1");
        logon.setUser("admin");
        logon.setPassword("admin");

        HttpHeaders httpHeaders = datacapservice.logon(client, logon);

        datacapservice.logout(client, logon, httpHeaders);

        return new CustomMessage("Test OK").getMessage();
    }
}
