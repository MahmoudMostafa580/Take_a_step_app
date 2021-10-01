package com.example.takeastep.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.takeastep.activities.user.AreYouReadyActivity;
import com.example.takeastep.activities.user.TakeAstepActivity;
import com.example.takeastep.activities.user.TogetherWeWinActivity;
import com.example.takeastep.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    FragmentHomeBinding homeBinding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding=FragmentHomeBinding.inflate(inflater,container,false);
        homeBinding.areYouReadyBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), AreYouReadyActivity.class)));
        homeBinding.togetherWeWinBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), TogetherWeWinActivity.class)));
        homeBinding.takeAStepBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), TakeAstepActivity.class)));
        return homeBinding.getRoot();
    }
}