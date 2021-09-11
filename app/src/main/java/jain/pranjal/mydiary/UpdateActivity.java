package jain.pranjal.mydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by hp on 11/18/2019.
 */

public class UpdateActivity extends AppCompatActivity{

    private TextView date, salutation, error;
    private EditText body;
    private String  Year,Date,DiaryEntry;
    private Button save;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary_entry);

        Intent intent=getIntent();
        Year =intent.getStringExtra("year");
        Date=intent.getStringExtra("date");
        DiaryEntry =intent.getStringExtra("diaryentry");

        salutation = (TextView) findViewById(R.id.textViewSalutation);
        date = (TextView) findViewById(R.id.textViewDate);
        date.setText(Date);
        error = (TextView) findViewById(R.id.textViewError);
        body = (EditText) findViewById(R.id.editTextBody);
        body.setText(DiaryEntry);
        save = (Button) findViewById(R.id.buttonSave);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String salu= salutation.getText().toString().trim();
                String date = Date;
                String diaryEntry= body.getText().toString().trim();

                if (diaryEntry.isEmpty()) {
                    body.setError("Field can't be empty");

                }

                else if(body.length() !=0) {
                    updateData(date, salu,diaryEntry);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(UpdateActivity.this, LogInActivity.class));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(UpdateActivity.this, DiaryEntryList.class);
        i.putExtra("year",Year);
        startActivity(i);
        finish();
    }

    public void updateData(String date,String salutation,String dataEntry)
    {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child(Year);

        Data data = new Data(date,salutation,dataEntry);
        databaseReference.child(date).setValue(data);

        Intent i=new Intent(UpdateActivity.this,DiaryEntryList.class);
        i.putExtra("year",Year);
        startActivity(i);
        Toast.makeText(this,"Updated Successfully",Toast.LENGTH_SHORT).show();
        finish();

    }
}
