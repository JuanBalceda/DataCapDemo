package com.balceda.itlict.domain;

public class TransactionProps {
    private String application;
    private String pageFile;
    private String transactionId;
    private String workflow;
    private String rulesets;
    private String taskProfile;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getPageFile() {
        return pageFile;
    }

    public void setPageFile(String pageFile) {
        this.pageFile = pageFile;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public String getRulesets() {
        return rulesets;
    }

    public void setRulesets(String rulesets) {
        this.rulesets = rulesets;
    }

    public String getTaskProfile() {
        return taskProfile;
    }

    public void setTaskProfile(String taskProfile) {
        this.taskProfile = taskProfile;
    }
}
