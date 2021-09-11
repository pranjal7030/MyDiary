package jain.pranjal.mydiary;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileAddPage extends AppCompatActivity {

    final int REQUEST_CODE_GALLERY=101;
    private CircleImageView ImageViewProfile;
    private EditText name;
    private FirebaseAuth mAuth;
    private Button done;
    private ProgressBar progressBar2;
    private Uri uri;
    private TextView skip;
    private Bitmap bitmap;
    String  profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_display_page);

        mAuth=FirebaseAuth.getInstance();

        ImageViewProfile=(CircleImageView) findViewById(R.id.imageViewprofileimage);
        name=(EditText)findViewById(R.id.editTextName);
        done=(Button)findViewById(R.id.buttonDone);
        skip=(TextView)findViewById(R.id.textViewSkip);
        progressBar2=(ProgressBar)findViewById(R.id.progressBar4);

      ImageViewProfile.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              ActivityCompat.requestPermissions(ProfileAddPage.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_GALLERY);

          }
      });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }


        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileAddPage.this,MainActivity.class));
                finish();
            }


        });

    }

    private void saveUser() {
        final String displayName = name.getText().toString().trim();


        if(displayName.isEmpty())
        {
            name.setError("Name required");
            name.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if(user !=null && profileImageUrl != null)
        {
            UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ProfileAddPage.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                finish();
                                startActivity(new Intent(ProfileAddPage.this,LogInActivity.class));


                            }
                        }
                    });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(ProfileAddPage.this,LogInActivity.class));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_GALLERY)
        {
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_GALLERY);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"you donn't have the permissions to access files",Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
      if(data == null) {
          Toast.makeText(ProfileAddPage.this,"No photo is selected",Toast.LENGTH_LONG).show();
      }

      else {
          try {
              uri = data.getData();
              InputStream inputStrem = getContentResolver().openInputStream(uri);
              bitmap = BitmapFactory.decodeStream(inputStrem);
              ImageViewProfile.setImageBitmap(bitmap);
              uploadImage();
          } catch (FileNotFoundException e) {
              e.printStackTrace();
          }

      }
        super.onActivityResult(requestCode,resultCode,data);


    }


    private  void uploadImage()
    {
        final StorageReference profileImageRef= FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".png");

        if(uri != null)
        {
            progressBar2.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar2.setVisibility(View.GONE);
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar2.setVisibility(View.GONE);
                            Toast.makeText(ProfileAddPage.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }
}
