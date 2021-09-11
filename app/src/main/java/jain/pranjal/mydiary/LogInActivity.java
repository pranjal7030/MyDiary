package jain.pranjal.mydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

/**
 * Created by hp on 10/23/2019.
 */

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView GoToSignUp;
    private EditText Username,Password;
    private Button Login;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applogin);

        GoToSignUp =(TextView)findViewById(R.id.textViewGoToSignUp);
        Username =(EditText)findViewById(R.id.editTextUserNameLogin);
        Password =(EditText)findViewById(R.id.editTextPasswordLogin);
        Login=(Button) findViewById(R.id.buttonLogin);
        progressBar=(ProgressBar)findViewById(R.id.progressBar3);

        mAuth=FirebaseAuth.getInstance();

        GoToSignUp.setOnClickListener(this);
        Login.setOnClickListener(this);
    }

    public  void userLogin()
    {
        String username = Username.getText().toString().trim();
        String password = Password.getText().toString().trim();

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

        if(password.length()>6)
        {
            Password.setError("Minimum length of password should be 6");
            Password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {

                    Intent intent= new Intent(LogInActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

                else
                {
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() !=null)
        {

            startActivity(new Intent(LogInActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.buttonLogin:
                userLogin();
                break;


            case R.id.textViewGoToSignUp :

                startActivity(new Intent(this,SignUpActivity.class));
                finish();
                break;


        }

    }
}
