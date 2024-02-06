package com.abc.notesy;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private Button logoutButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();
                Toast.makeText(getActivity(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);

                getActivity().finish();
            }
        });

        return view;
    }
}