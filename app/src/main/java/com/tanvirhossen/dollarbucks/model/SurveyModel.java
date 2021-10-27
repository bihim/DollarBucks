package com.tanvirhossen.dollarbucks.model;

public class SurveyModel {
    private int id;
    private String question;
    private String option;

    public SurveyModel(int id, String question, String option) {
        this.id = id;
        this.question = question;
        this.option = option;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
