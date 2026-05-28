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

public class Login extends AppCompatActivity {
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    Button login;
    TextView registerLink;
    TextView failedLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        login = findViewById(R.id.loginBtn);
        registerLink = findViewById(R.id.registerLink);
        failedLogin = findViewById(R.id.textView3);


        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://smartcity-app-login-register-bmdrhdhvbjcwhndq.switzerlandnorth-01.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        login.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                return;
            }
            apiService.loginUser(new User(email, password)).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        AuthResponse authResponse = response.body();
                        if (authResponse.getMessage().equals("LogIn Successful")) {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            failedLogin.setText("Login failed");
                        }
                    } else {
                        failedLogin.setText("Login failed");
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    failedLogin.setText("Login failed");
                    Log.e("AuthError", "Network connection failed: " + t.getMessage());
                }
            });
        });
    }
}