package com.abc.notesy;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class AddFragment extends Fragment {

    EditText createtitleofnote, createcontentofnote;
    FloatingActionButton savenote;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    private Dialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        loadingDialog = new Dialog(requireContext());
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        savenote = view.findViewById(R.id.saveNote);
        createtitleofnote = view.findViewById(R.id.createtitleofnote);
        createcontentofnote = view.findViewById(R.id.createdescriptionofnote);

        savenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = createtitleofnote.getText().toString();
                String content = createcontentofnote.getText().toString();
                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(getActivity(), "Both Fields are Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                else
                {
                    showLoadingDialog();
                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document();
                    Map<String, Object> note = new HashMap<>();
                    note.put("createdAt", FieldValue.serverTimestamp());
                    note.put("title", title);
                    note.put("content", content);
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dismissLoadingDialog();
                            Toast.makeText(getActivity(), "Note Added", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to Add Note", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

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