package com.example.stepsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerHistory;
    HistoryAdapter adapter;
    DatabaseReference dbRef;

    List<StepHistory> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // MATCH XML ID
        recyclerHistory = findViewById(R.id.recyclerHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoryAdapter(historyList);
        recyclerHistory.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbRef = FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .child("steps");

        loadHistory();
    }

    private void loadHistory() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                historyList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {

                    String date = snap.getKey();
                    Long steps = snap.getValue(Long.class);

                    if (date != null && steps != null) {
                        historyList.add(new StepHistory(date, steps));
                    }
                }

                Collections.reverse(historyList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}


