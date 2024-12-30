package com.example.quizzlet.data.model;

import java.util.ArrayList;

public class Question {
    private String question;
    private String answer;
    private ArrayList<String> mcq;

    // No-argument constructor required for Firestore deserialization
    public Question() {
    }

    public Question(String question, String answer, ArrayList<String> mcq) {
        this.question = question;
        this.answer = answer;
        this.mcq = mcq;
    }

    // Getter for question
    public String getQuestion() {
        return question;
    }

    // Getter for answer
    public String getAnswer() {
        return answer;
    }

    // Getter for mcq
    public ArrayList<String> getMcq() {
        return mcq;
    }

    // Optional: Setters if needed
    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setMcq(ArrayList<String> mcq) {
        this.mcq = mcq;
    }
}
