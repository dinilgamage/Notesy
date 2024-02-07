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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

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
                Toast.makeText(getActivity(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
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
        loadProfileImage();
    }

    private void showImagePickerOptions() {
        String[] options = {"Gallery", "Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    chooseImageFromGallery();
                } else if (i == 1) {
                    if (ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(),
                                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    } else {
                        openCamera();
                    }
                }
            }
        });
        builder.show();
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
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
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
        showLoadingDialog();
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("profileImages/" + firebaseAuth.getCurrentUser().getUid() + ".jpg");
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
                        // Upload success
                        Uri downloadUri = task.getResult();
                        // Save the downloadUri to user's profile data in Firestore
                        updateProfileImage(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (task.isSuccessful()) {
                                    // Update the ImageView with the downloaded image
                                    Glide.with(requireContext()).load(downloadUri).into(profileImageView);
                                    Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Error updating profile image
                                    Log.e("ProfileFragment", "Failed to update profile image", task.getException());
                                    Toast.makeText(requireContext(), "Failed to update profile image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        // Error uploading image
                        Log.e("ProfileFragment", "Failed to upload image", task.getException());
                        Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private Task<String> updateProfileImage(String imageUrl) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            // Check if the user profile document exists
            return userRef.get().continueWithTask(new Continuation<DocumentSnapshot, Task<String>>() {
                @Override
                public Task<String> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Update the existing document with the profile image URL
                            return userRef.update("profileImageUrl", imageUrl)
                                    .continueWith(new Continuation<Void, String>() {
                                        @Override
                                        public String then(@NonNull Task<Void> task) throws Exception {
                                            if (task.isSuccessful()) {
                                                return imageUrl;
                                            } else {
                                                throw task.getException();
                                            }
                                        }
                                    });
                        } else {
                            // Create a new document with the profile image URL
                            return userRef.set(new UserModel(currentUser.getUid(), imageUrl), SetOptions.merge())
                                    .continueWith(new Continuation<Void, String>() {
                                        @Override
                                        public String then(@NonNull Task<Void> task) throws Exception {
                                            if (task.isSuccessful()) {
                                                return imageUrl;
                                            } else {
                                                throw task.getException();
                                            }
                                        }
                                    });
                        }
                    } else {
                        throw task.getException();
                    }
                }
            });
        }
        return Tasks.forException(new NullPointerException("User is null"));
    }

    private void loadProfileImage() {
        showLoadingDialog();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    dismissLoadingDialog();
                    if (documentSnapshot.exists()) {
                        UserModel userModel = documentSnapshot.toObject(UserModel.class);
                        if (userModel != null && userModel.getProfileImageUrl() != null) {
                            // Load the profile image using Glide or Picasso
                            Glide.with(requireContext()).load(userModel.getProfileImageUrl()).into(profileImageView);
                        } else {
                            // Use default image if profile image URL is null
                            profileImageView.setImageResource(R.drawable.login);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("ProfileFragment", "Error fetching profile image", e);
                }
            });
        }
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

