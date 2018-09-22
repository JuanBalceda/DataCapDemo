package com.balceda.itlict.service;

import com.balceda.itlict.controller.DataCapController;
import com.balceda.itlict.domain.DatacapLogonInfo;
import com.balceda.itlict.domain.TransactionProps;
import com.balceda.itlict.util.JSONUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class DatacapService {
    private final String baseurl = "http://169.57.15.56:6080/ServicewTM.svc/";
    private final String logonuri = "Session/Logon";
    private final String transtarturi = "Transaction/Start";
    private final String tranexecuteuri = "Transaction/Execute";
    private final String logouturi = "Session/Logoff";

    public HttpHeaders logon(RestTemplate client, DatacapLogonInfo datacapLogonInfo) {
        String url = baseurl + logonuri;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        JSONUtil jsonUtil = new JSONUtil();
        String jsonstr = jsonUtil.toJSONStringBy(datacapLogonInfo);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonstr, httpHeaders);

        System.out.println("url: " + url);
        System.out.println("jsonstr: " + jsonstr);

        ResponseEntity<String> response = client.postForEntity(url, requestEntity, String.class);
        return response.getHeaders();
    }

    public void logout(RestTemplate client, DatacapLogonInfo datacapLogonInfo, HttpHeaders headers) {
        String url = baseurl + logouturi;

        JSONUtil jsonUtil = new JSONUtil();
        String jsonstr = jsonUtil.toJSONStringBy(datacapLogonInfo);

        HttpEntity<String> requestEntity = new HttpEntity<String>(jsonstr, headers);
        System.out.println("url: " + url);
        System.out.println("jsonstr: " + jsonstr);

        client.postForEntity(url, requestEntity, String.class);
    }

    public String executeTran(RestTemplate client, TransactionProps props, HttpHeaders headers) {
        String url = baseurl + tranexecuteuri;
        System.out.println("url: " + url);

        JSONUtil jsonUtil = new JSONUtil();
        String jsonstr = jsonUtil.toJSONStringBy(props);

        jsonstr = jsonstr.replaceAll("transactionId", "TransactionId");
        jsonstr = jsonstr.replaceAll("application", "Application");
        jsonstr = jsonstr.replaceAll("workflow", "Workflow");
        jsonstr = jsonstr.replaceAll("PageFile", "PageFile");
        jsonstr = jsonstr.replaceAll("rulesets", "Rulesets");
        jsonstr = jsonstr.replaceAll("taskProfile", "TaskProfile");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.setAccept(mediaTypes);

        System.out.println("jsonstr: " + jsonstr);

        Iterator iterator = httpHeaders.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            System.out.println("Key: " + key);
            System.out.println("Value: " + httpHeaders.get(key));
        }

        HttpEntity<String> requestEntity = new HttpEntity<String>(jsonstr, httpHeaders);
        ResponseEntity<String> response = client.postForEntity(url, requestEntity, String.class);
        return response.toString();
    }

    public String startTran(RestTemplate client, HttpHeaders headers) {
        String url = baseurl + transtarturi;
        System.out.println("url: " + url);
        String jsonstr = "";

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonstr, headers);
        ResponseEntity<String> response = client.exchange(url, HttpMethod.GET, requestEntity, String.class);
        return response.getBody();
    }

    public String setFileTran(RestTemplate client, HttpHeaders headers, String tranid, String filetype, MultipartFile file) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String uri = "";
        String filepath = "";
        String specifiedfilename = "";
/*
        if (filetype.equals("xml")){
            uri="Transaction/SetFile/"+tranid+"/VScan/xml";
            filepath= param.getValuePer("datacappagexmlpath")+"_"+file.getExt()+".xml";
            specifiedfilename="VScan.xml";
        }else {
*/
        uri = "Transaction/SetFile/" + tranid + "/TM000001/";
        filepath = DataCapController.IMAGES_FOLDER + File.separator + file.getName();
        specifiedfilename = "TM000001" + "." + file.getName();
//        }

        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        String url = baseurl + uri;
        FileSystemResource fileSystemResource = new FileSystemResource(filepath);
        String temppath = DataCapController.IMAGES_FOLDER;

        FileOutputStream outputStream = new FileOutputStream(new File(temppath + File.separator + specifiedfilename));
        FileCopyUtils.copy(fileSystemResource.getInputStream(), outputStream);

        FileSystemResource tempfile = new FileSystemResource(temppath + File.separator + specifiedfilename);
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

        System.out.println("File path: " + filepath);
        parts.add("upload file", tempfile);

        System.out.println("url :" + url);


        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(headers);
        Iterator iterator = httpHeaders.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            System.out.println("Key: " + key);
            System.out.println("Value: " + httpHeaders.get(key));
        }

        ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, entity, String.class);
        return response.toString();
    }

    public String getFileTran(RestTemplate client, String tranid) throws Exception {
        String uri = "Transaction/GetFile/" + tranid + "/TM000001/html";
        String url = baseurl + uri;

        HttpHeaders httpHeaders = new HttpHeaders();

        System.out.println("url: " + url);
        String jsonstr = "";

        HttpEntity<String> entity = new HttpEntity<>(jsonstr, httpHeaders);

        ResponseEntity<String> response = client.exchange(url, HttpMethod.GET, entity, String.class);
        //clearTempFiles();
        return response.getBody();
    }
}
