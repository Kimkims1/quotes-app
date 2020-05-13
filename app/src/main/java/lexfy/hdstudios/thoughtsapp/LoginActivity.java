package lexfy.hdstudios.thoughtsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import lexfy.hdstudios.thoughtsapp.utils.JournalApi;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn, signUpBtn;
    private AutoCompleteTextView email_login;
    private EditText password_login;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;

    private CollectionReference collectionReference = firestoreDb.collection("Thoughts_Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login Now");

        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        signUpBtn = findViewById(R.id.btnSignUp);
        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(email_login.getText().toString().trim(), password_login.getText().toString().trim());
            }
        });

    }

    private void loginUser(String email, String password) {

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String currentUserId = user.getUid();

                            collectionReference
                                    .whereEqualTo("userId",currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                            progressBar.setVisibility(View.INVISIBLE);
                                            if (e != null){
                                                if (!queryDocumentSnapshots.isEmpty()){

                                                    for (QueryDocumentSnapshot qs: queryDocumentSnapshots){
                                                        JournalApi journalApi = JournalApi.getInstance();
                                                        journalApi.setUsername(qs.getString("username"));
                                                        journalApi.setUserId(qs.getString("userId"));

                                                        //Open QuotesListActivity
                                                        startActivity(new Intent(LoginActivity.this,PostQuotesActivity.class));

                                                    }


                                                }
                                            }
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
        }
        else {
            Toast.makeText(this, "Empty fields not required", Toast.LENGTH_SHORT).show();
        }
    }
}
