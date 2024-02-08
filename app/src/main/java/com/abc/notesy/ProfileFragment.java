package com.abc.notesy;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_FROM_GALLERY = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    private FirebaseAuth firebaseAuth;
    private Button logoutButton;
    private ImageView profileImageView;
    private Uri imageUri;
    private Dialog loadingDialog;
    private TextView notesCountTextView;
    private TextView wordsCountTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        notesCountTextView = view.findViewById(R.id.notesCountTextView);
        wordsCountTextView = view.findViewById(R.id.wordsCountTextView);


        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Good Morning";
        } else if (timeOfDay >= 12 && timeOfDay < 18) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }

        TextView greetingTextView = view.findViewById(R.id.greetingTextView);
        greetingTextView.setText("Hey there! " + greeting);

        TextView emailTextView = view.findViewById(R.id.emailTextView);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String email = currentUser != null ? currentUser.getEmail() : "";
        emailTextView.setText("You are logged in as: " + email);

        logoutButton = view.findViewById(R.id.logoutButton);
        profileImageView = view.findViewById(R.id.profileImageView);

        loadingDialog = new Dialog(requireContext());
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickerOptions();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Glide.with(this)
                .load(R.drawable.default_profile_pic)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(profileImageView);
        loadProfileImage();
        fetchNotesAndWordsCount();
    }
    private void fetchNotesAndWordsCount() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notes")
                .document(currentUser.getUid())
                .collection("myNotes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int notesCount = queryDocumentSnapshots.size();
                    notesCountTextView.setText("" + notesCount);

                    int wordsCount = 0;
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String noteContent = document.getString("content");
                        if (noteContent != null) {
                            wordsCount += countWords(noteContent);
                        }
                    }
                    wordsCountTextView.setText("" + wordsCount);
                })
                .addOnFailureListener(e -> Log.e("ProfileFragment", "Error fetching notes and words count", e));
    }


    private int countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }


    private void showImagePickerOptions() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            boolean hasProfileImage = documentSnapshot.exists() && documentSnapshot.getString("profileImageUrl") != null && !documentSnapshot.getString("profileImageUrl").isEmpty();

            final Dialog dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.dialog_image_picker);

            Button buttonGallery = dialog.findViewById(R.id.button_gallery);
            Button buttonCamera = dialog.findViewById(R.id.button_camera);
            Button buttonDelete = dialog.findViewById(R.id.button_delete);

            if (hasProfileImage) {
                buttonDelete.setVisibility(View.VISIBLE);
            }

            buttonGallery.setOnClickListener(v -> {
                chooseImageFromGallery();
                dialog.dismiss();
            });

            buttonCamera.setOnClickListener(v -> {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    openCamera();
                }
                dialog.dismiss();
            });

            buttonDelete.setOnClickListener(v -> {
                if (hasProfileImage) {
                    deleteProfileImage();
                }
                dialog.dismiss();
            });

            dialog.show();
        }).addOnFailureListener(e -> Log.e("ProfileFragment", "Error checking for profile image", e));
    }


    private void deleteProfileImage() {
        showLoadingDialog();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;

        StorageReference photoRef = FirebaseStorage.getInstance().getReference()
                .child("profileImages/" + currentUser.getUid() + ".jpg");

        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dismissLoadingDialog();
                Toast.makeText(getContext(), "Avatar deleted", Toast.LENGTH_SHORT).show();
                removeImageFromFirestore();
                Glide.with(requireContext())
                        .load(R.drawable.default_profile_pic)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(profileImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), "Failed to Delete Profile Image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeImageFromFirestore() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());

        userRef.update("profileImageUrl", FieldValue.delete())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ProfileFragment", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ProfileFragment", "Error updating document", e);
                    }
                });
    }

    private void chooseImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_FROM_GALLERY);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == PICK_IMAGE_FROM_GALLERY && data != null) {
                imageUri = data.getData();
                uploadImageToFirebase();
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(extras).get("data");
                imageUri = getImageUri(requireContext(), imageBitmap);
                uploadImageToFirebase();
            }
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri == null) {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingDialog();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("profileImages/" + currentUser.getUid() + ".jpg");
        UploadTask uploadTask = storageReference.putFile(imageUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                dismissLoadingDialog();
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    updateProfileImage(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                Glide.with(requireContext())
                                        .load(downloadUri)
                                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                        .into(profileImageView);
                                Toast.makeText(requireContext(), "Avatar updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("ProfileFragment", "Failed to update profile image", task.getException());
                                Toast.makeText(requireContext(), "Failed to update profile image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Log.e("ProfileFragment", "Failed to upload image", task.getException());
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Task<String> updateProfileImage(String imageUrl) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return Tasks.forException(new NullPointerException("User is null"));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());

        return userRef.get().continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                return userRef.update("profileImageUrl", imageUrl)
                        .continueWith(task12 -> imageUrl);
            } else {
                return userRef.set(new UserModel(currentUser.getUid(), imageUrl), SetOptions.merge())
                        .continueWith(task1 -> imageUrl);
            }
        });
    }

    private void loadProfileImage() {
        if (!isAdded()) return;

        showLoadingDialog();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            dismissLoadingDialog();
            if (documentSnapshot.exists()) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                if (userModel != null && userModel.getProfileImageUrl() != null && getContext() != null) {
                    Glide.with(getContext())
                            .load(userModel.getProfileImageUrl())
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(profileImageView);
                } else if (getContext() != null) {
                    Glide.with(getContext())
                            .load(R.drawable.default_profile_pic)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(profileImageView);
                }
            }
        }).addOnFailureListener(e -> {
            if (isAdded()) {
                Log.e("ProfileFragment", "Error fetching profile image", e);
            }
        });
    }


    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
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
