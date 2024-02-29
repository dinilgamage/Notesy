# Introducing Notesy
Notesy is a sleek and intuitive note-taking app that keeps things simple, minimal, and stylish

## Features:
1. Google and Email authentication
2. Password Reset Functionality
3. Email Verification
4. CRUD Notes
5. View stats like the number of notes and words typed
6. Get greeted by the time of day
7. Update or Delete Avatar with camera or gallery images
8. Secure storage of notes in Firebase so you can access your notes from anywhere
9. Minimal and aesthetic UI with a great color scheme with custom fonts, custom dialog boxes and progress bars, etc.
10. Navigation to all screens seamlessly.

## Other features
1. Camera - Keep your avatar up-to-date
2. Ambient light sensor- Perfect screen brightness to view your notes clearly at any lighting condition!
3. Dark and Light mode - Choose your desired theme
4. Configured app manifest with custom icons - Notesy specific icons throughout the application

## Installation Guide

### Set up Android Studio
If you haven't already, [download and set up Android Studio](https://developer.android.com/studio) on your computer.

To install and run the Notesy app in Android Studio, follow these steps:

1. **Clone the Repository:**
   - Clone the repository using Git by running the following command in your terminal or Git Bash:
     ```bash
     git clone https://github.com/dinilgamage/Notesy.git
     ```

2. **Open Project in Android Studio:**
   - Open Android Studio and select "Open an existing Android Studio project."
   - Navigate to the directory where you cloned the repository and select the `Notesy` folder.
   - Wait for Android Studio to import the project and sync the Gradle files. This process may take some time, as Android Studio downloads any missing dependencies and prepares the project for building.
   - Once the Gradle sync is complete, you should see the project structure in the Android Studio project explorer.

3. **Build and Run the App:**
   - Build the project by clicking on the "Build" menu in Android Studio and selecting "Make Project."
   - Connect your Android device to your computer using a USB cable or start an Android emulator.
   - Select your connected device from the list of available devices and click "OK."
   - Click on the green play button (Run 'app') in the Android Studio toolbar.
   - Android Studio will install the app on the selected device and launch it.

4. **Test Ambient Light Sensor (Optional):**
   - If you want to test the ambient light sensor feature, ensure your device supports this sensor and that it's enabled in the device settings.

5. **Interact with the App:**
   - Use the app to create notes, manage your profile, and explore its various features.

6. **Stop the App:**
   - To stop the app, click on the square stop button in the Android Studio toolbar.

By following these steps, you should be able to install and run the Notesy app on your Android device, allowing you to test its functionality and user interface.

## Screens

### Login
<div style="display: flex; justify-content: space-around;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/8c802141-cb37-4c14-a496-41a1a315abee" alt="Login Screen 1" style="width: 25%;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/4acb1593-9796-4c81-b5bd-2a882d7a0752" alt="Login Screen 2" style="width: 25%;">
</div>
In the login screen, the user can select either Google login or email login. If logging in through Google, they just have to click “Continue with Google,” and if they want to login with email, they have to enter a verified email and password and click login. If they don’t have an account, they can choose to register, if they forgot their password, they can click forgot password.

### Register
<div style="display: flex; justify-content: space-around;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/f2781400-c2e6-403b-af8c-22bee99c01e1" alt="Register Screen 1" style="width: 25%;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/1490e4b6-e8e5-4680-9ee4-aca009f6d41d" alt="Register Screen 2" style="width: 25%;">
</div>
In the register screen, the user can either “Continue with Google” or register via an email. They can also navigate back to login.

### Forgot Password
<div style="display: flex; justify-content: space-around;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/d54e37ba-4a8d-4c1f-85a5-e2cd664e3c7f" alt="Forgot Password Screen 1" style="width: 25%;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/e47682dc-b829-4baf-b1dc-6bb1bea61497" alt="Forgot Password Screen 2" style="width: 25%;">
</div>
In the forgot password screen, the users can choose to enter their registered email and click recover to send a password reset email to that email address.

### All Notes
<div style="display: flex; justify-content: space-around;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/f9230f02-876a-4e06-892a-c6f9f8a68d6b" alt="All Notes Screen 1" style="width: 25%;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/60e83907-8b69-4ff9-8e07-67abcb999a41" alt="All Notes Screen 2" style="width: 25%;">
</div>
Here, the users can see all the notes they posted ordered by their created at date, the note card colors are random and changes every time. They can choose to delete and/or view a note.

### Add Note
<div style="display: flex; justify-content: space-around;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/6d93e1d3-8e18-45f1-a8c0-6baaa0c8c991" alt="Add Note Screen 1" style="width: 25%;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/899cb270-43a4-4783-b134-29ee9805ec70" alt="Add Note Screen 2" style="width: 25%;">
</div>
The user can add a note title and description here and then upload it.

### Profile
<div style="display: flex; justify-content: space-around;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/f00a69c6-fc4f-4139-85c6-0d3957285f55" alt="Profile Screen 1" style="width: 25%;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/59770513-71a4-459a-9ec2-32f8a4d8fafd" alt="Profile Screen 2" style="width: 25%;">
</div>
Here, the user can manage their profile by uploading, changing, or deleting their avatar by clicking on the avatar. They can upload from gallery or directly open the camera. They can also see the number of notes and words they’ve typed. They can also choose to logout.

### View Note
<div style="display: flex; justify-content: space-around;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/c6f7f564-8815-40e2-a573-be15e8ded204" alt="View Note Screen 3" style="width: 25%;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/ffd97304-816d-4774-9b79-df449c495db3" alt="View Note Screen 4" style="width: 25%;">
</div>


### Edit Note
<div style="display: flex; justify-content: space-around;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/f099af50-257a-48a3-b456-789e7998e7de" alt="Edit Note Screen 1" style="width: 25%;">
    <img src="https://github.com/dinilgamage/Notesy/assets/113094888/f4f5e7da-f51d-4c24-ac27-6520e25e4921" alt="Edit Note Screen 2" style="width: 25%;">
</div>


