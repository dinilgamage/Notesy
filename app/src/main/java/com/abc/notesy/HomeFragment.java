package com.abc.notesy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<firebasemodel,NoteViewHolder> noteAdapter;


    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView noteTitle;
        private final TextView noteContent;

        LinearLayout notelayout;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.notetitle);
            noteContent = itemView.findViewById(R.id.notecontent);
            notelayout = itemView.findViewById(R.id.note);


        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Query query = firebaseFirestore.collection("notes")
                .document(firebaseUser.getUid())
                .collection("myNotes")
                .orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<firebasemodel> allusernotes = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, firebasemodel model) {

//                ImageView popupbutton = holder.itemView.findViewById(R.id.menupopbutton);
                ImageView deleteButton = holder.itemView.findViewById(R.id.deleteButton);

                int colorcode = getRandomColor();
                holder.notelayout.setBackgroundColor(holder.itemView.getResources().getColor(colorcode, null));
                holder.noteTitle.setText(model.getTitle());
                holder.noteContent.setText(model.getContent());

                String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();
                
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewFragment viewFragment = new ViewFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("title", model.getTitle());
                        bundle.putString("content", model.getContent());
                        bundle.putString("noteId", docId);
                        viewFragment.setArguments(bundle);

                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.frameLayout, viewFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_layout, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(dialogView);

                        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
                        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);

                        TextView dialogButton = dialogView.findViewById(R.id.dialog_button);

                        TextView dialogCancelButton = dialogView.findViewById(R.id.dialog_cancel_button);

                        dialogTitle.setText("Confirm Deletion");
                        dialogMessage.setText("Are you sure you want to delete this note?");

                        AlertDialog dialog = builder.create();

                        dialog.show();

                        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);

                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DocumentReference documentReference = firebaseFirestore.collection("notes")
                                        .document(firebaseUser.getUid())
                                        .collection("myNotes")
                                        .document(docId);
                                documentReference.delete().addOnSuccessListener(unused -> {
                                    Toast.makeText(getContext(), "Note Deleted", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed To Delete", Toast.LENGTH_SHORT).show();
                                });
                                dialog.dismiss();
                            }
                        });

                        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });





//                popupbutton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
//                        popupMenu.setGravity(Gravity.END);
//                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(item -> {
//                            EditFragment editFragment = new EditFragment();
//                            Bundle bundle = new Bundle();
//                            bundle.putString("title", model.getTitle());
//                            bundle.putString("content", model.getContent());
//                            bundle.putString("noteId", docId);
//                            editFragment.setArguments(bundle);
//
//                            getParentFragmentManager().beginTransaction()
//                                    .replace(R.id.frameLayout, editFragment)
//                                    .addToBackStack(null)
//                                    .commit();
//                            return false;
//                        });
//                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(item -> {
//                            DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
//                            documentReference.delete().addOnSuccessListener(unused -> {
//                                Toast.makeText(view.getContext(), "Note Deleted", Toast.LENGTH_SHORT).show();
//                            }).addOnFailureListener(e -> {
//                                Toast.makeText(view.getContext(), "Failed To Delete", Toast.LENGTH_SHORT).show();
//                            });
//                            return false;
//                        });
//                        popupMenu.show();
//                    }
//                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(noteAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        if(noteAdapter != null){
            noteAdapter.stopListening();
        }
    }

    private int getRandomColor() {
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.green);

        Random randomColor = new Random();
        int number = randomColor.nextInt(colorcode.size());
        return colorcode.get(number);
    }
}