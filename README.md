# Quizzlet

Quizzlet is an **online exam / flashcard** application built on **Android** that leverages **Firebase** for user authentication and Firestore for retrieving questions. The app helps students quickly learn and revise topics through quizzes and flashcards, providing a straightforward and modern user experience.  

## Table of Contents
1. [Overview](#overview)  
2. [Features](#features)  
3. [Tech Stack](#tech-stack)  
4. [Project Structure](#project-structure)  
5. [Setup and Installation](#setup-and-installation)  
6. [Usage](#usage)  
7. [Screenshots](#screenshots)  

---

## Overview

Quizzlet allows students to **log in**, **register**, and **sign in with Google**. Upon successful login, users see a **Home** screen with quiz details. The app queries Firestore for quiz questions, displays them in a user-friendly format, and calculates final scores. For better UX, it also supports:

- **Responsive design** for various screen sizes.  
- **Smooth animations** in transitions.  
- **Dark mode** toggling.  
- **Local notifications** (e.g., welcome notification).  
- **Forgot password** workflow for resetting user credentials.

---

## Features

1. **User Authentication**  
   - Email/Password sign-in and registration.  
   - Google Sign-In using Firebase.

2. **Responsive UI**  
   - Uses `ConstraintLayout` and dimension resources to adapt to different screen sizes.

3. **Quiz Mechanics**  
   - Fetches multiple-choice questions from Firestore.  
   - Displays question text and 4 possible answers.  
   - Tracks score and displays the result at the end.

4. **Dark Mode Support**  
   - Toggle night mode from an options menu item.

5. **Animations**  
   - Fades in “Next” button.  
   - Scaling icons for a dynamic user experience.

6. **Local Notification**  
   - “Welcome” notification after login (Android 13+ permission check included).

7. **Forgot Password**  
   - Simple password reset via Firebase’s `sendPasswordResetEmail`.

---

## Tech Stack

- **Language**: Java  
- **IDE**: Android Studio  
- **Firebase Services**:  
  - Firebase Authentication (Email/Password & Google)  
  - Firestore (for retrieving questions)  
- **Architecture Components**:  
  - Android Jetpack (ViewModel, LiveData)  
  - Navigation Component  
- **UI**: ConstraintLayout, Material Components  

---

## Project Structure

```
.
├── app
│   ├── src
│   │   ├── main
│   │   │   ├── java/com/example/quizzlet
│   │   │   │   ├── MainActivity.java           // Hosts toolbar, nav drawer, notifications
│   │   │   │   ├── ui
│   │   │   │   │   ├── login
│   │   │   │   │   │   └── LoginActivity.java  // Handles Firebase Auth, registration, forgot password
│   │   │   │   │   ├── home
│   │   │   │   │   │   └── HomeFragment.java   // Displays home UI, triggers quizzes
│   │   │   │   │   └── exam
│   │   │   │   │       └── ExamFragment.java   // Quiz logic, displays questions, calculates score
│   │   │   │   └── data/model
│   │   │   │       └── Question.java           // Model class for question data
│   │   │   ├── res
│   │   │   │   ├── layout/                     // Contains activity_login.xml, fragment_exam.xml, etc.
│   │   │   │   ├── values/                     // colors, dimens, strings, themes
│   │   │   │   ├── menu/                       // activity_main_drawer.xml, main.xml (dark mode)
│   │   │   │   └── drawable/                   // icons, backgrounds
│   │   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

Key files to note:
- **`MainActivity.java`**: Sets up navigation drawer, notification channel, welcome notification, dark mode toggling.  
- **`LoginActivity.java`**: Manages Firebase Auth login/registration, Google Sign-In, password complexity checks, forgot password.  
- **`ExamFragment.java`**: Handles question display, animates UI, calculates final score.  
- **`HomeFragment.java`**: Basic fragment showing the “Start Quiz” button, brief animations.

---

## Setup and Installation

1. **Clone the Repository**  
   ```bash
   git clone https://github.com/<YourUsername>/Quizzlet.git
   cd Quizzlet
   ```

2. **Open in Android Studio**  
   - Go to **File** > **Open** > select the **Quizzlet** folder.

3. **Add Firebase Configuration**  
   - Download `google-services.json` from your Firebase project settings.  
   - Place it in `app/` directory.  
   - Make sure the `applicationId` in your app-level `build.gradle` matches your Firebase project settings.

4. **Sync Gradle**  
   - Let Android Studio sync dependencies.  

5. **Run on Emulator/Device**  
   - Connect an Android device or use an emulator.  
   - Press **Run**.

---

## Usage

- **Login**  
  1. Enter your email/password or tap **Google Sign-In**.  
  2. If new user, tap **Register** to create an account (Firebase).  
  3. If forgot password, tap **Forgot Password?** to get a reset link.

- **Dark Mode**  
  1. Tap the **three-dot menu** in the toolbar.  
  2. Select **Toggle Dark Mode**.

- **Quiz**  
  1. On **Home** screen, press **Start**.  
  2. In **ExamFragment**, pick an answer, press **Next**.  
  3. After final question, see your **score**.

- **Notifications**  
  - On returning to **MainActivity**, a **welcome notification** appears (requires POST_NOTIFICATIONS permission on Android 13+).

---

## Screenshots
![Quiz](https://github.com/user-attachments/assets/812ef34f-e3df-4ed2-8b43-9044774979ae)
![Home](https://github.com/user-attachments/assets/3f9ba70c-0de1-49d9-a0b7-fc6c6d3bde81)
![Login](https://github.com/user-attachments/assets/f6565eb1-2775-4a09-9b04-5dadafd5cb4f)

---



## Contributors
 1. Ardit Haklaj
 2. Pashtrik Zogaj
 3. Elda Drenica
 4. Erite Hyseni

**Thank you** for checking out Quizzlet! 
