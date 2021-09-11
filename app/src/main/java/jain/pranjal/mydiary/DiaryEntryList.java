package jain.pranjal.mydiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 11/10/2019.
 */

public class DiaryEntryList extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton floatingActionButtonadd;
    private ListView ListViewDataEntry;
    private FirebaseUser user;
    private List<Data> dataList;
    private Data data;
    private DataList adapter;
    private String Year;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diaryentrylist_layout);
       final Intent intent = getIntent();
        Year = intent.getStringExtra("year");

        firebaseAuth = FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        floatingActionButtonadd = (FloatingActionButton)findViewById(R.id.floatingActionButtonAdd);

        progressBar =(ProgressBar)findViewById(R.id.progressBar6);
        ListViewDataEntry =(ListView)findViewById(R.id.listViewEntryData);
        dataList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child(Year);
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataList.clear();

                for(DataSnapshot dateSnapshot: dataSnapshot.getChildren())
                {
                    if(dataSnapshot != null)
                    {
                        data = dateSnapshot.getValue(Data.class);
                        dataList.add(data);

                    }

                    else
                    {
                        Toast.makeText(DiaryEntryList.this,"Nothing to show...Please add data",Toast.LENGTH_LONG).show();
                    }

                }

                adapter = new DataList(DiaryEntryList.this,dataList);
                ListViewDataEntry.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        floatingActionButtonadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intentsend=new Intent(DiaryEntryList.this,AddDiaryEntry.class);
                intentsend.putExtra("year",Year);
                startActivity(intentsend);
                finish();

            }
        });



        ListViewDataEntry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                data = dataList.get(position);
                sendData(data.getDate());


                }

        });

        ListViewDataEntry.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                data = dataList.get(position);
               AlertDialog.Builder alt = new AlertDialog.Builder(DiaryEntryList.this);
                alt.setMessage("Please Select")
                        .setCancelable(true)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteData(data.getDate());
                                dialog.cancel();


                            }

                        })

                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                                Intent sendData= new Intent(DiaryEntryList.this,UpdateActivity.class);
                                sendData.putExtra("year",Year);
                                sendData.putExtra("date",data.getDate());
                                sendData.putExtra("diaryentry",data.getDiaryEntry());
                                startActivity(sendData);



                            }
                        });

                AlertDialog alert = alt.create();
                alert.show();
                return false;
            }

        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(DiaryEntryList.this, LogInActivity.class));
        }

    }

public void deleteData(String date)
{

    DatabaseReference deleteDate=FirebaseDatabase.getInstance().getReference().child(user.getUid()).child(Year).child(date);

    deleteDate.removeValue();

    Toast.makeText(this,"Deleted Succfully",Toast.LENGTH_SHORT).show();
}

public void sendData(final String Date)
{

    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseUser user=auth.getCurrentUser();

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child(Year).child(Date);

    databaseReference.addValueEventListener(new ValueEventListener() {



        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {


            data = dataSnapshot.getValue(Data.class);



          /*  date = dataSnapshot.child("date").getValue(String.class);
            Salutation = dataSnapshot.child("salutation").getValue(String.class);
            DiaryEntry = dataSnapshot.child("diaryEntry").getValue(String.class);*/

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {


        }



    });

    final String diaryEntryData = data.getDate() + "\n" + data.getSalutation() + "\n\n" + data.getDiaryEntry();
    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DiaryEntryList.this);
    LayoutInflater inflater = getLayoutInflater();
    final View dialogView = inflater.inflate(R.layout.activity_show, null);
    dialogBuilder.setView(dialogView);
    final TextView textViewShow = (TextView) dialogView.findViewById(R.id.textViewShow);
    dialogBuilder.setTitle("");
    final AlertDialog  alertDialog = dialogBuilder.create();
    textViewShow.setText(diaryEntryData);
    alertDialog.show();


}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) menuItem.getActionView();
        return super.onCreateOptionsMenu(menu);
    }
}
