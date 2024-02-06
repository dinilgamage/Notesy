package com.abc.notesy;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class EditFragment extends Fragment {

    EditText editNoteTitle, editNoteContent;
    FloatingActionButton saveEditedNote;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    private Dialog loadingDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        loadingDialog = new Dialog(requireContext());
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        editNoteTitle = view.findViewById(R.id.edittitleofnote);
        editNoteContent = view.findViewById(R.id.editdescriptionofnote);
        saveEditedNote = view.findViewById(R.id.editNote);



        saveEditedNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = editNoteTitle.getText().toString();
                String newContent = editNoteContent.getText().toString();
                firebaseFirestore.collection("notes").document(getArguments().getString("noteId"));
                        if(newTitle.isEmpty() || newContent.isEmpty()){
                            Toast.makeText(getActivity(), "Title or Content cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            showLoadingDialog();
                            DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(getArguments().getString("noteId"));
                            Map<String, Object> note = new HashMap<>();
                            note.put("createdAt", FieldValue.serverTimestamp());
                            note.put("title", newTitle);
                            note.put("content", newContent);

                            documentReference.set(note).addOnSuccessListener(unused -> {
                                dismissLoadingDialog();
                                Toast.makeText(getActivity(), "Note Updated", Toast.LENGTH_SHORT).show();
                                HomeFragment homeFragment = new HomeFragment();
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.frameLayout, homeFragment)
                                        .commit();

                            }).addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Failed to Update Note", Toast.LENGTH_SHORT).show();
                            });
                        }


            }
        });

        String notetitle = getArguments().getString("title");
        String notecontent = getArguments().getString("content");
        editNoteTitle.setText(notetitle);
        editNoteContent.setText(notecontent);
        return view;
    }

    private void showLoadingDialog() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}