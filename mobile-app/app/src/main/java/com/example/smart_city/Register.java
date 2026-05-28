package com.example.smart_city;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Register extends AppCompatActivity {

    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    TextInputEditText confirmPasswordInput;

    TextView failedRegister;
    Button registerBtn;
    TextView loginLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        emailInput = findViewById(R.id.regEmailInput);
        passwordInput = findViewById(R.id.regPasswordInput);
        confirmPasswordInput = findViewById(R.id.regConfirmPasswordInput);
        failedRegister = findViewById(R.id.textView2);
        registerBtn = findViewById(R.id.registerBtn);
        loginLink = findViewById(R.id.loginLink);

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://smartcity-app-login-register-bmdrhdhvbjcwhndq.switzerlandnorth-01.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        registerBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (!password.equals(confirmPassword)) {
                failedRegister.setText("Passwords do not match");
                return;
            }

            if (email.isEmpty() || password.isEmpty()) {
                failedRegister.setText("Please fill all fields");
                return;
            }
            User user = new User(email, password);
            apiService.registerUser(user).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        AuthResponse authResponse = response.body();
                        if (authResponse.getMessage().equals("User Created")) {
                            Intent intent = new Intent(Register.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else failedRegister.setText("Registration Failed");
                    } else failedRegister.setText("Registration Failed");
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Log.e("AuthError", "Network connection failed: " + t.getMessage());
                    failedRegister.setText("Registration Failed");
                }
            });
        });
    }
}