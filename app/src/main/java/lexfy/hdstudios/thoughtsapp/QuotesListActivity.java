package lexfy.hdstudios.thoughtsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class QuotesListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;

    private CollectionReference collectionReference = firestoreDb.collection("Thoughts_Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes_list);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addBtn:
                /* Do something*/

                if (currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(QuotesListActivity.this,PostQuotesActivity.class));

                }
                break;

            case R.id.action_signOut:
                /* do something*/

                if (currentUser != null && firebaseAuth != null){
                    firebaseAuth.signOut();

                    startActivity(new Intent(QuotesListActivity.this,MainActivity.class));
                }
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
