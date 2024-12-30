package com.example.quizzlet.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quizzlet.data.model.Question;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    // Static questions list accessible by ExamViewModel
    public static ArrayList<Question> questions = new ArrayList<>();

    // LiveData to indicate connection status
    private MutableLiveData<Boolean> connected = new MutableLiveData<>(false);

    // Getter for connected LiveData
    public LiveData<Boolean> getConnected() {
        return connected;
    }

    public HomeViewModel() {
        fetchQuestions();
    }

    private void fetchQuestions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("questions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        questions.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Question question = document.toObject(Question.class);
                            questions.add(question);
                        }
                        connected.setValue(true);
                    } else {
                        connected.setValue(false);
                    }
                });
    }
}
