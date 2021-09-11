package jain.pranjal.mydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView GoToLogIn;
    private EditText Username,Password,ConfirmPassword;
    private Button SingUp;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth= FirebaseAuth.getInstance();

        GoToLogIn =(TextView)findViewById(R.id.textViewGoToLogIn);
        Username =(EditText)findViewById(R.id.editTextUserNameSignUp);
        Password =(EditText)findViewById(R.id.editTextPasswordSignUp);
        ConfirmPassword =(EditText)findViewById(R.id.editTextConfirmPass);
        SingUp=(Button) findViewById(R.id.buttonSignUp);
        progressBar =(ProgressBar)findViewById(R.id.progressBar2);

        GoToLogIn.setOnClickListener(this);
        SingUp.setOnClickListener(this);
    }

    void registerUser()
    {



        String username = Username.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String confirmPass=ConfirmPassword.getText().toString().trim();
        if(username.isEmpty())
        {
            Username.setError("Email is required");
            Username.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches())
        {
            Username.setError("Please enter a valid email");
            Username.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            Password.setError("Password is required");
            Password.requestFocus();
            return;
        }

        if(password.length()< 6 )
        {
            Password.setError("Minimum length of password should be 6");
            Password.requestFocus();
            return;
        }

        if(password.length()> 8 )
        {
            Password.setError(" Maximum length of password should be 8");
            Password.requestFocus();
            return;
        }
        if(confirmPass.isEmpty())

            {
                ConfirmPassword.setError("Please Confirm your password");
                ConfirmPassword.requestFocus();
                return;
            }

            if(!confirmPass.equals(password) )
            {
                ConfirmPassword.setError("Password will not matched, please enter correct password");
                ConfirmPassword.requestFocus();
                return;
            }


        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
              if(task.isSuccessful())
              {
                  finish();
                  startActivity(new Intent(SignUpActivity.this,ProfileAddPage.class));
              }


              else
              {
                  if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                      Toast.makeText(getApplicationContext(), "This username is already registered", Toast.LENGTH_LONG).show();
                  }

                  else
                  {
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_LONG).show();
                  }
              }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonSignUp:
                registerUser();
                break;

            case R.id.textViewGoToLogIn:
                startActivity(new Intent(this,LogInActivity.class));
                finish();
                break;


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignUpActivity.this,LogInActivity.class));
        finish();
    }
}


