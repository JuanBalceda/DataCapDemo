package com.balceda.itlict.util;

import com.balceda.itlict.domain.DatacapLogonInfo;

public class TestApp {

    public static void main(String[] args) {
        DatacapLogonInfo logon = new DatacapLogonInfo();
        logon.setApplication("Transaction");
        logon.setStation("1");
        logon.setUser("admin");
        logon.setPassword("admin");
        JSONUtil jsonUtil = new JSONUtil();
        String jsonstr = jsonUtil.toJSONStringBy(logon);
        System.out.println(jsonstr);
    }
}
