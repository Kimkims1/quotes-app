package lexfy.hdstudios.thoughtsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lexfy.hdstudios.thoughtsapp.utils.JournalApi;

public class SignUpActivity extends AppCompatActivity {

    private Button btnSignUp;
    private TextView email_signUp;
    private EditText password_signUp, username_signUp;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;

    private CollectionReference collectionReference = firestoreDb.collection("Thoughts_Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        firebaseAuth = FirebaseAuth.getInstance();

        btnSignUp = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.progressBar);
        email_signUp = findViewById(R.id.email_signUp);
        password_signUp = findViewById(R.id.password_signUp);
        username_signUp = findViewById(R.id.username_signUp);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    /* User is logged in*/
                } else {
                    /* No user yet*/

                }
            }
        };

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(email_signUp.getText().toString()) &&
                        !TextUtils.isEmpty(password_signUp.getText().toString()) &&
                        !TextUtils.isEmpty(username_signUp.getText().toString())) {

                    String email = email_signUp.getText().toString().trim();
                    String password = password_signUp.getText().toString().trim();
                    String username = username_signUp.getText().toString().trim();

                    createUserAccount(email, password, username);

                } else {
                    Toast.makeText(SignUpActivity.this, "Empty fields not allowed", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void createUserAccount(String email, String password, final String username) {

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)) {

            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(SignUpActivity.this, "Account created successful", Toast.LENGTH_SHORT).show();
                                /* Take user to Dashboard*/
                                currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                final String currentUserId = currentUser.getUid();

                                /* Create a user Map*/
                                Map<String, String> userObject = new HashMap<>();
                                userObject.put("userId", currentUserId);
                                userObject.put("username", username);

                                collectionReference.add(userObject)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                        if (Objects.requireNonNull(task.getResult()).exists()) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            String name = task.getResult()
                                                                    .getString("username");

                                                            JournalApi journalApi = JournalApi.getInstance();
                                                            journalApi.setUserId(currentUserId);
                                                            journalApi.setUsername(username);

                                                            Intent intent = new Intent(SignUpActivity.this, PostQuotesActivity.class);
                                                            intent.putExtra("username", username);
                                                            intent.putExtra("userId", currentUserId);
                                                            startActivity(intent);
                                                        } else {

                                                        }
                                                    }
                                                });


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(SignUpActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
