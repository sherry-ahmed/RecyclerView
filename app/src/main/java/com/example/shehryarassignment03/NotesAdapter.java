package com.example.shehryarassignment03;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class NotesAdapter extends FirebaseRecyclerAdapter<Note, NotesAdapter.NotesViewHolder> {
    Context parent;
    public NotesAdapter(@NonNull FirebaseRecyclerOptions<Note> options, Context context) {
        super(options);
        parent = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NotesViewHolder notesViewHolder,int i, @NonNull Note note) {
        notesViewHolder.tvTitle.setText(note.getTitle());
        notesViewHolder.tvContent.setText(note.getContent());
        notesViewHolder.tvTimeStamp.setText(note.getTimestamp());
        notesViewHolder.ivBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(parent)
                                .setTitle("Confirmation")
                                .setMessage("Do you really want to delete your Note")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getRef(i).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(parent, "Deleted", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(parent, e.getMessage(), Toast.LENGTH_SHORT).show();

                                                    }
                                                });


                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        confirmationDialog.create();
                        confirmationDialog.show();


            }
        });

        notesViewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v = LayoutInflater.from(parent).inflate(R.layout.add_new_note_design, null);

                TextView tvTimeStamp = v.findViewById(R.id.tvTimeStamp);
                TextInputEditText etTitle = v.findViewById(R.id.etTitle);
                TextInputEditText etContent = v.findViewById(R.id.etContent);
                etTitle.setText(note.getTitle());
                etContent.setText(note.getContent());
                tvTimeStamp.setText(note.getTimestamp());

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Date date = new Date();
                tvTimeStamp.setText(format.format(date));

                AlertDialog.Builder updateDialog = new AlertDialog.Builder(parent)
                        .setTitle("Update Note")
                        .setView(v)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String title = Objects.requireNonNull(etTitle.getText()).toString().trim();
                                String content = Objects.requireNonNull(etContent.getText()).toString().trim();
                                String timestamp = Objects.requireNonNull(tvTimeStamp.getText()).toString();
                                HashMap<String, Object> data = new HashMap<>();
                                data.put("title", title);
                                data.put("content", content);
                                data.put("timestamp", tvTimeStamp.getText().toString());

                                getRef(notesViewHolder.getAbsoluteAdapterPosition() ).updateChildren(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(parent, "Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(parent, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                updateDialog.create();
                updateDialog.show();
            }
        });

    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_note_design, parent, false);
        return new NotesViewHolder(v);
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle, tvContent, tvTimeStamp;
        ImageView ivEdit, ivBin;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            ivBin = itemView.findViewById(R.id.ivBin);
            ivEdit = itemView.findViewById(R.id.ivEdit);
        }
    }

}
