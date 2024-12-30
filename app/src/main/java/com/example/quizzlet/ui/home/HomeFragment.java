package com.example.quizzlet.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.quizzlet.R;
import com.example.quizzlet.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Observe Firebase connectivity
        homeViewModel.getConnected().observe(getViewLifecycleOwner(), isConnected -> {
            binding.radioButton.setChecked(isConnected);
        });

        // Inflate layout
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Animate the main quiz image slightly on load
        binding.imageView3.setScaleX(0.8f);
        binding.imageView3.setScaleY(0.8f);
        binding.imageView3.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .start();

        // Start button click
        binding.button.setOnClickListener(view -> {
            if (HomeViewModel.questions.isEmpty()) {
                Toast.makeText(requireContext(), "Connection error, please try again", Toast.LENGTH_SHORT).show();
                return;
            }
            NavDirections action = new ActionOnlyNavDirections(R.id.action_nav_home_to_examFragment);
            Navigation.findNavController(view).navigate(action);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
