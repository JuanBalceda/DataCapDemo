package com.balceda.itlict.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {

    ObjectMapper mapper;

    public JSONUtil() {
        this.mapper = new ObjectMapper();;
    }

    public String toJSONStringBy(Object o){
        String jsonstr="";
        try {
            jsonstr = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonstr;
    }
}
