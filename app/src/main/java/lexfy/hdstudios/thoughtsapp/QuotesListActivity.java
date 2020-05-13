package lexfy.hdstudios.thoughtsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lexfy.hdstudios.thoughtsapp.Adapters.QuoteAdapter;
import lexfy.hdstudios.thoughtsapp.model.QuoteModel;
import lexfy.hdstudios.thoughtsapp.utils.JournalApi;

public class QuotesListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;

    private CollectionReference collectionReference = firestoreDb.collection("Thoughts_Users");

    private List<QuoteModel> quoteList;
    private RecyclerView recyclerView;
    private QuoteAdapter quoteAdapter;
    private TextView noQuotesPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes_list);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerView);
        noQuotesPost = findViewById(R.id.noQuotesPost);

        quoteList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId", JournalApi.getInstance()
                .getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            for (QueryDocumentSnapshot quotes : queryDocumentSnapshots) {

                                QuoteModel quoteModel = quotes.toObject(QuoteModel.class);
                                quoteList.add(quoteModel);

                            }

                            quoteAdapter = new QuoteAdapter(QuotesListActivity.this, quoteList);
                            recyclerView.setAdapter(quoteAdapter);
                            quoteAdapter.notifyDataSetChanged();


                        } else {

                            noQuotesPost.setVisibility(View.INVISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

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

                if (currentUser != null && firebaseAuth != null) {
                    startActivity(new Intent(QuotesListActivity.this, PostQuotesActivity.class));

                }
                break;

            case R.id.action_signOut:
                /* do something*/

                if (currentUser != null && firebaseAuth != null) {
                    firebaseAuth.signOut();

                    startActivity(new Intent(QuotesListActivity.this, MainActivity.class));
                }
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
