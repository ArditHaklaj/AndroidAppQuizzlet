package com.example.quizzlet.ui.exam;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quizzlet.data.model.Question;
import com.example.quizzlet.ui.home.HomeViewModel;

import java.util.ArrayList;

public class ExamViewModel extends ViewModel {

    // Questions list
    ArrayList<Question> questions = HomeViewModel.questions;

    // Total user grade
    int grade = 0;

    // Store index of current question
    int currentQuestionIndex = 0;

    // Boolean to detect if questions are all solved
    private MutableLiveData<Boolean> isFinished = new MutableLiveData<>(false);

    // Store current question to show in UI
    private MutableLiveData<Question> currentQuestion = new MutableLiveData<>();

    // Store the value user selected
    private MutableLiveData<String> selectedValue = new MutableLiveData<>();

    // Store answers entered by the user
    private ArrayList<String> selectedAnswers = new ArrayList<>();

    // Getter for currentQuestion
    public LiveData<Question> getCurrentQuestion() {
        return currentQuestion;
    }

    // Getter for isFinished
    public LiveData<Boolean> getIsFinished() {
        return isFinished;
    }

    // Update selected value
    public void setSelectedValue(String value) {
        selectedValue.setValue(value);
    }

    // Calculate user grade
    public void getGrade(){
        grade = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String a = selectedAnswers.get(i);
            if (q.getAnswer().equals(a)){
                grade++;
            }
        }
    }

    // Add answer selected by the user to selected answers list
    public void submitSelectedAnswer(){
        String answer = selectedValue.getValue();
        if (answer != null) {
            selectedAnswers.add(answer);
        } else {
            selectedAnswers.add(""); // Handle no selection
        }
    }

    // Move to next question
    public void nextQuestion(){
        currentQuestionIndex++;
        // If there are no other questions, calculate user grade and finish the exam
        if (currentQuestionIndex > questions.size() - 1) {
            getGrade();
            isFinished.setValue(true);
            return;
        }
        currentQuestion.setValue(questions.get(currentQuestionIndex));
    }

    // Method to initialize the first question
    public void initializeQuiz() {
        if (questions != null && !questions.isEmpty()) {
            currentQuestion.setValue(questions.get(currentQuestionIndex));
        } else {
            // Handle empty question list
            isFinished.setValue(true);
        }
    }
}
