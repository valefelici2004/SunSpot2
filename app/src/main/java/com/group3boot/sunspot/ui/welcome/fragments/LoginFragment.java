package com.group3boot.sunspot.ui.welcome.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.group3boot.sunspot.R;
import com.group3boot.sunspot.ui.home.HomeActivity;

public class LoginFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //BOTTONE ACCESSO
        //TextInputLayout emailLayout = view.findViewById(R.id.emailLayout);
        TextInputEditText email = view.findViewById(R.id.email);
        //TextInputLayout pwLayout = view.findViewById(R.id.passwordLayout);
        TextInputEditText pw = view.findViewById(R.id.password);
        Button bottoneAccesso = view.findViewById(R.id.accesso);
        bottoneAccesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //controllo email valida
                String emailStringa = email.getText().toString().trim();
                //controllo password corretta
                String pwStringa = pw.getText().toString().trim();
                //vado alla home

                //intent esplicito login-->home CAMBIARE MAIN CON HOME!!!
                Intent intent = new Intent();
                intent.setClass(getContext(), HomeActivity.class);
                startActivity(intent);

                //Modo con NAVIGATION
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeActivity);

            }
        });

        //BOTTONE REGISTRAZIONE
        Button bottoneRegistrazione = view.findViewById(R.id.registrazione);
        bottoneRegistrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vado alla registrazione
            }
        });
    }
}