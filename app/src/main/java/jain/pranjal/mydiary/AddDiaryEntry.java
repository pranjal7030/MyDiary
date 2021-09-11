package jain.pranjal.mydiary;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class AddDiaryEntry extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ImageView cal;
    private TextView date, salutation, error;
    private EditText body;
    private String  Year;
    private Button save;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary_entry);

        Intent i = getIntent();
        Year = i.getStringExtra("year");


        firebaseAuth = FirebaseAuth.getInstance();

        cal = (ImageView) findViewById(R.id.imageViewCal);
        salutation = (TextView) findViewById(R.id.textViewSalutation);
        date = (TextView) findViewById(R.id.textViewDate);
        error = (TextView) findViewById(R.id.textViewError);
        body = (EditText) findViewById(R.id.editTextBody);
        save = (Button) findViewById(R.id.buttonSave);

        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(AddDiaryEntry.this, LogInActivity.class));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(AddDiaryEntry.this, DiaryEntryList.class);
        back.putExtra("year",Year);
        startActivity(back);
        finish();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        date.setText(currentDateString);
    }

    private void saveData() {
        String d = date.getText().toString().trim();
        String salu = "Dear Diary ,";
        String b = body.getText().toString().trim();

        if (b.isEmpty()) {
            body.setError("Field can't be empty");

        }
        else if (d.equals("Select Date")) {
            error.setText("*Please Select date");

        }
        else if (body.length() != 0) {
            FirebaseUser user=firebaseAuth.getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child(Year).child(d);
            Data data=new Data(d,salu,b);
            databaseReference.setValue(data);
            Toast.makeText(AddDiaryEntry.this, "Data Added", Toast.LENGTH_SHORT).show();
            Intent back = new Intent(AddDiaryEntry.this, DiaryEntryList.class);
            back.putExtra("year",Year);
            startActivity(back);
            finish();


        }
        else
        {
            Toast.makeText(AddDiaryEntry.this, "Data not added", Toast.LENGTH_SHORT).show();

        }

    }
}



