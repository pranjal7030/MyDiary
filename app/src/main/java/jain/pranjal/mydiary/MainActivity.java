    package jain.pranjal.mydiary;

    import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

    public class MainActivity extends AppCompatActivity
            implements NavigationView.OnNavigationItemSelectedListener {


        private ListView ListViewYears;
        private CircleImageView ImageViewProfileImage;
        private TextView authUserName,authUserEmail;
        private ArrayList<Years> yearsList;
        private ProgressBar progressBar;
        private FirebaseAuth firebaseAuth;
        private DatabaseYear databaseYear;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            firebaseAuth = FirebaseAuth.getInstance();
            ListViewYears = (ListView) findViewById(R.id.listViewYear);
            progressBar=(ProgressBar)findViewById(R.id.progressBarYear);
            yearsList = new ArrayList<>();



            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addYear();
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

            View view=navigationView.getHeaderView(0);
            ImageViewProfileImage = (CircleImageView) view.findViewById(R.id.imageViewProfileImage);
            authUserName =(TextView)view.findViewById(R.id.AuthuserName);
            authUserEmail =(TextView)view.findViewById(R.id.AuthUserEmail);



            if(firebaseAuth.getCurrentUser() != null) {

                if (firebaseAuth.getCurrentUser().getPhotoUrl() != null) {

                Glide.with(this)
                        .load(firebaseAuth.getCurrentUser().getPhotoUrl().toString())
                        .into(ImageViewProfileImage);
                 }

                if (firebaseAuth.getCurrentUser().getDisplayName() != null) {
                authUserName.setText(firebaseAuth.getCurrentUser().getDisplayName());
                }

                if (firebaseAuth.getCurrentUser().getEmail() != null) {
                authUserEmail.setText(firebaseAuth.getCurrentUser().getEmail());
                 }
            }

            ImageViewProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,ProfileAddPage.class));
                    finish();
                }
            });

        progressBar.setVisibility(View.VISIBLE);
        databaseYear=new DatabaseYear(this);
        Cursor data=databaseYear.getData();
        if(data.getCount()==0)
        {
            Toast.makeText(this,"Please Add Year",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }

        while (data.moveToNext())
        {
            String year=data.getString(1);
            yearsList.add(new Years(year));
        }
        CustomAdapterMain adapter=new CustomAdapterMain(this, R.layout.custom_activity, yearsList);
        ListViewYears.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

            navigationView.setNavigationItemSelectedListener(this);
    }


        @Override
        protected void onStart() {
            super.onStart();
            firebaseAuth = FirebaseAuth.getInstance();
            if(firebaseAuth.getCurrentUser() == null)
            {
                finish();
                startActivity(new Intent(MainActivity.this,LogInActivity.class));
            }

        }

        @Override
        public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

       @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_search) {

                finish();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this,LogInActivity.class));
            }


                // Handle the camera action

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        private void addYear() {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);


            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.activity_enteryear, null);
            dialogBuilder.setView(dialogView);

            final EditText editTextYear = (EditText) dialogView.findViewById(R.id.editTextYear);
            final Button add = (Button) dialogView.findViewById(R.id.buttonAdd);

            dialogBuilder.setTitle("Add Year");
            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   databaseYear=new DatabaseYear(MainActivity.this);
                    String Year=editTextYear.getText().toString().trim();
                    long l= databaseYear.insertMain(Year);
                    if(l == -1)
                    {
                        Toast.makeText(MainActivity.this,"Something is wrong !! Plese try again",Toast.LENGTH_LONG).show();

                    }
                    else
                    {

                        alertDialog.dismiss();
                        startActivity(new Intent(MainActivity.this,MainActivity.class));
                        finish();
                    }
                }
            });
        }


        public class CustomAdapterMain extends BaseAdapter {
            private Context context;
            private int layout;
            AlertDialog.Builder builder;
            ArrayList<Years> textList;

            public CustomAdapterMain(Context context, int layout, ArrayList<Years> textList) {
                this.context = context;
                this.layout = layout;
                this.textList = textList;
            }

            @Override
            public int getCount() {
                return textList.size();
            }

            @Override
            public Object getItem(int position) {
                return textList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            public class ViewHolderMain {
                ImageView imageView1;
                TextView textName;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                View row = view;

                ViewHolderMain holder;

                if (row == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(layout, null);
                    holder = new ViewHolderMain();
                    holder.textName = (TextView) row.findViewById(R.id.textViewList);
                    holder.imageView1 = (ImageView) row.findViewById(R.id.imageViewList);
                    row.setTag(holder);


                } else {
                    holder = (ViewHolderMain) row.getTag();

                }
                final Years student = textList.get(position);
                holder.textName.setText(student.getYear());




                row.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Please Select")
                                .setCancelable(true)
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        DatabaseYear dba=new DatabaseYear(MainActivity.this);
                                        String nam[] = {student.getYear()};

                                        boolean data = dba.deleteIt(nam);

                                        if (data == true) {
                                            startActivity(new Intent(MainActivity.this,MainActivity.class));
                                            finish();




                                        } else {
                                            Toast.makeText(MainActivity.this, "OOpss !! Something is Wrong.", Toast.LENGTH_LONG).show();


                                        }

                                    }

                                })

                                .setNegativeButton("Back", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alertt = builder.create();
                        alertt.show();
                        return false;

                    }
                });
                row.setOnClickListener(new AdapterView.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {


                        Intent showIntentMainActivity=new Intent(MainActivity.this,DiaryEntryList.class);
                        showIntentMainActivity.putExtra("year",student.getYear());
                        startActivity(showIntentMainActivity);




                    }
                });
                return row;
            }
        }
    }
