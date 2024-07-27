package com.example.shehryarassignment03;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton btnAddNewNote;

    RecyclerView rvNotes;
    NotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        init();
        /*FirebaseDatabase.getInstance().getReference().child("Notes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String result = "";
                for(DataSnapshot snap: snapshot.getChildren())
                {
                    result = result + "Title: "  + snap.child("title").getValue(String.class)
                            + "\nContent: "+ snap.child("content").getValue(String.class)
                            + "\nTimeStamp: "+ snap.child("timestamp").getValue(String.class)
                            + "\n\n";
                }
                tvResult.setText(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        btnAddNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_new_note_design, null);
                TextView tvTimeStamp = v.findViewById(R.id.tvTimeStamp);
                TextInputEditText etTitle = v.findViewById(R.id.etTitle);
                TextInputEditText etContent = v.findViewById(R.id.etContent);

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Date date = new Date();
                tvTimeStamp.setText(format.format(date));
                AlertDialog.Builder addNoteDialogue = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Creating New Note")
                        .setView(v)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String title = Objects.requireNonNull(etTitle.getText()).toString().trim();
                                String content = Objects.requireNonNull(etContent.getText()).toString().trim();
                                HashMap<String, Object> data = new HashMap<>();
                                data.put("title", title);
                                data.put("content", content);
                                data.put("timestamp", tvTimeStamp.getText().toString());

                                FirebaseDatabase.getInstance().getReference()
                                        .child("Notes")
                                        .push()
                                        .setValue(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(MainActivity.this, "Note Created", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });



                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                addNoteDialogue.create();
                addNoteDialogue.show();



            }
        });
    }
    private void init()
    {
        btnAddNewNote = findViewById(R.id.btnAddNewNote);

        rvNotes = findViewById(R.id.rvNotes);
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Notes")
                .limitToLast(50);
        FirebaseRecyclerOptions<Note> options =
                new FirebaseRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();
        adapter = new NotesAdapter(options, this);
        rvNotes.setAdapter(adapter);


    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}