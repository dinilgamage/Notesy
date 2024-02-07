package com.abc.notesy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ViewFragment extends Fragment {

    private TextView titleofnoteview, contentofnoteview;
    FloatingActionButton gotoedit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);
        titleofnoteview = view.findViewById(R.id.titleofnoteview);
        contentofnoteview = view.findViewById(R.id.descriptionofnoteview);
        gotoedit = view.findViewById(R.id.gotoeditNote);
        gotoedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditFragment editFragment = new EditFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title",titleofnoteview.getText().toString());
                bundle.putString("content",contentofnoteview.getText().toString());
                bundle.putString("noteId",getArguments().getString("noteId"));
                editFragment.setArguments(bundle);

                Toast.makeText(getContext(), "Edit Mode", Toast.LENGTH_SHORT).show();

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, editFragment)
                        .addToBackStack(null)
                        .commit();


            }
        });
        contentofnoteview.setText(getArguments().getString("content"));
        titleofnoteview.setText(getArguments().getString("title"));
        return view;

    }
}