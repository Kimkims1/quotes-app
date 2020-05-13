package lexfy.hdstudios.thoughtsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import lexfy.hdstudios.thoughtsapp.model.QuoteModel;
import lexfy.hdstudios.thoughtsapp.utils.JournalApi;

public class PostQuotesActivity extends AppCompatActivity {

    public static final int GALLERY_CODE = 1;

    private Button saveBtn;
    private ImageView addPhotoBtn, backGroundIv;
    private EditText titleEt, descriptionEt;
    private TextView postTv, post_dateTv;


    private String currentUserId, currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;

    private CollectionReference collectionReference = firestoreDb.collection("Quotes");
    private StorageReference storageReference;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_quotes);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressBar = findViewById(R.id.progressBar);
        saveBtn = findViewById(R.id.btnSave);
        addPhotoBtn = findViewById(R.id.postBtn);
        backGroundIv = findViewById(R.id.backGroundIv);
        titleEt = findViewById(R.id.titleEt);
        post_dateTv = findViewById(R.id.post_dateTv);
        postTv = findViewById(R.id.post_dateTv);
        descriptionEt = findViewById(R.id.descriptionEt);

        progressBar.setVisibility(View.INVISIBLE);

        if (JournalApi.getInstance() != null) {
            currentUserId = JournalApi.getInstance().getUserId();
            currentUserName = JournalApi.getInstance().getUsername();

            postTv.setText(currentUserName);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {

                } else {

                }
            }
        };

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveQuote();
            }
        });

        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

    }

    private void saveQuote() {

        final String title = titleEt.getText().toString().trim();
        final String description = descriptionEt.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && imageUri != null) {

            final StorageReference filePath = storageReference
                    .child("quotes_images")
                    .child("my_image_" + Timestamp.now().getSeconds());

            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            filePath.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            String imageUrl = uri.toString();
                                            /* Creating a QuoteModel Object*/
                                            QuoteModel model = new QuoteModel();
                                            model.setTitle(title);
                                            model.setDescription(description);
                                            model.setImage_url(imageUrl);
                                            model.setTimeAdded(new Timestamp(new Date()));
                                            model.setUserName(currentUserName);
                                            model.setUserId(currentUserId);

                                            collectionReference.add(model)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {

                                                            progressBar.setVisibility(View.INVISIBLE);

                                                            startActivity(new Intent(PostQuotesActivity.this,QuotesListActivity.class));
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            Toast.makeText(PostQuotesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    });

        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {

            if (data != null) {
                imageUri = data.getData();
                backGroundIv.setImageURI(imageUri);


            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
