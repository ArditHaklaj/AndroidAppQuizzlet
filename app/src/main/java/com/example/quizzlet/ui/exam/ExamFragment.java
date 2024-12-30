package com.example.quizzlet.ui.exam;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.quizzlet.R;
import com.example.quizzlet.databinding.FragmentExamBinding;
import com.example.quizzlet.ui.home.HomeViewModel;

public class ExamFragment extends Fragment {

    private ExamViewModel examViewModel;
    private HomeViewModel homeViewModel;
    private FragmentExamBinding binding;

    public static ExamFragment newInstance() {
        return new ExamFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExamBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        examViewModel = new ViewModelProvider(this).get(ExamViewModel.class);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Initially hide the 'Next' button
        binding.nextBtn.setVisibility(View.GONE);

        // Observe data connection
        homeViewModel.getConnected().observe(getViewLifecycleOwner(), isConnected -> {
            if (isConnected && !HomeViewModel.questions.isEmpty()) {
                examViewModel.initializeQuiz();
            } else if (!isConnected) {
                Toast.makeText(getContext(), "Failed to load quiz questions.", Toast.LENGTH_LONG).show();
            }
        });

        // Fade-in animation for the next button
        binding.radioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId != -1) {
                // Animate button appearance
                binding.nextBtn.setVisibility(View.VISIBLE);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(binding.nextBtn, View.ALPHA, 0f, 1f);
                fadeIn.setDuration(300);
                fadeIn.start();

                // Update selected answer
                RadioButton selectedRadioButton = radioGroup.findViewById(checkedId);
                if (selectedRadioButton != null) {
                    String selectedValue = selectedRadioButton.getText().toString();
                    examViewModel.setSelectedValue(selectedValue);
                }
            }
        });

        // On 'Next' button click
        binding.nextBtn.setOnClickListener(v -> {
            examViewModel.submitSelectedAnswer();
            examViewModel.nextQuestion();

            // Clear selections with a small scale animation
            binding.radioGroup.clearCheck();
            ObjectAnimator scaleOut = ObjectAnimator.ofPropertyValuesHolder(
                    binding.nextBtn,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f)
            );
            scaleOut.setDuration(300);
            scaleOut.start();
            scaleOut.addListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    binding.nextBtn.setVisibility(View.GONE);
                    binding.nextBtn.setScaleX(1f);
                    binding.nextBtn.setScaleY(1f);
                }
            });
        });

        // Observe current question
        examViewModel.getCurrentQuestion().observe(getViewLifecycleOwner(), question -> {
            if (question != null) {
                binding.questionText.setText(question.getQuestion());

                if (question.getMcq() != null && question.getMcq().size() >= 4) {
                    binding.radioButton0.setText(question.getMcq().get(0));
                    binding.radioButton1.setText(question.getMcq().get(1));
                    binding.radioButton2.setText(question.getMcq().get(2));
                    binding.radioButton3.setText(question.getMcq().get(3));
                    binding.radioButton3.setVisibility(View.VISIBLE);
                } else if (question.getMcq() != null) {
                    // Fewer than 4 options
                    binding.radioButton0.setText(question.getMcq().get(0));
                    binding.radioButton1.setText(question.getMcq().get(1));
                    if (question.getMcq().size() > 2) {
                        binding.radioButton2.setText(question.getMcq().get(2));
                        binding.radioButton3.setVisibility(View.VISIBLE);
                        if (question.getMcq().size() > 3) {
                            binding.radioButton3.setText(question.getMcq().get(3));
                        } else {
                            binding.radioButton3.setVisibility(View.GONE);
                        }
                    } else {
                        binding.radioButton2.setVisibility(View.GONE);
                        binding.radioButton3.setVisibility(View.GONE);
                    }
                }
            }
        });

        // Observe if exam is finished
        examViewModel.getIsFinished().observe(getViewLifecycleOwner(), isDone -> {
            if (isDone) {
                binding.radioGroup.setVisibility(View.GONE);
                binding.nextBtn.setVisibility(View.GONE);

                String finalGrade = String.format("Your final grade is %d / %d",
                        examViewModel.grade, HomeViewModel.questions.size());
                binding.questionText.setText(finalGrade);

                // Animate grading icon scale up
                binding.imageView4.setImageResource(R.drawable.baseline_grading_24);
                ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                        binding.imageView4,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f)
                );
                scaleUp.setDuration(500);
                scaleUp.start();

                Toast.makeText(getContext(), finalGrade, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Simple listener to handle animation end events
    private static class SimpleAnimationListener implements android.animation.Animator.AnimatorListener {
        @Override public void onAnimationStart(android.animation.Animator animator) {}
        @Override public void onAnimationEnd(android.animation.Animator animator) {}
        @Override public void onAnimationCancel(android.animation.Animator animator) {}
        @Override public void onAnimationRepeat(android.animation.Animator animator) {}
    }
}
