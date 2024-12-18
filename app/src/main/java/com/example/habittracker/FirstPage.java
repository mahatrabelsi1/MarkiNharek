package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FirstPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        // Initialize UI components
        ImageView logoImageView = findViewById(R.id.logoImageView);
        Button signUpButton = findViewById(R.id.signUpButton);
        Button loginButton = findViewById(R.id.loginButton);

        // Set up click listeners
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Sign Up activity
                Toast.makeText(FirstPage.this, "Sign Up button clicked", Toast.LENGTH_SHORT).show();
                // Uncomment the below line to navigate to a SignUpActivity
                startActivity(new Intent(FirstPage.this, SignupActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Login activity
                Toast.makeText(FirstPage.this, "Login button clicked", Toast.LENGTH_SHORT).show();
                // Uncomment the below line to navigate to a LoginActivity
                startActivity(new Intent(FirstPage.this, LoginActivity.class));
            }
        });
    }
}
