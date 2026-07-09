# Firebase Authentication

## Q1: What authentication providers does Firebase support?

```kotlin
// Email/Password
FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)

// Google
val googleClient = GoogleSignIn.getClient(context, gso)
val account = googleClient.signInIntent  // → startActivityForResult
val credential = GoogleAuthProvider.getCredential(account.idToken, null)
FirebaseAuth.getInstance().signInWithCredential(credential)

// Apple
val provider = OAuthProvider.newBuilder("apple.com")
provider.addCustomParameter("locale", "en")
FirebaseAuth.getInstance().startActivityForSignInWithProvider(activity, provider.build())

// Anonymous
FirebaseAuth.getInstance().signInAnonymously()

// Phone
FirebaseAuth.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, activity, callbacks)

// Custom (server-side token)
FirebaseAuth.getInstance().signInWithCustomToken(token)
```

### Supported providers
| Provider | Method |
|----------|--------|
| Email/Password | `createUserWithEmailAndPassword` |
| Google | `GoogleAuthProvider` |
| Apple | `OAuthProvider("apple.com")` |
| Facebook | `FacebookAuthProvider` |
| Phone | `verifyPhoneNumber` |
| Anonymous | `signInAnonymously` |
| Microsoft/Twitter/GitHub | `OAuthProvider` |
| Custom | `signInWithCustomToken` |

---

## Q2: How do you handle auth state changes?

```kotlin
// Listen to auth state
FirebaseAuth.getInstance().addAuthStateListener { auth ->
    val user = auth.currentUser
    if (user != null) {
        // User is signed in
    } else {
        // User is signed out
    }
}

// Listen to ID token changes (for backend sync)
FirebaseAuth.getInstance().addIdTokenListener { auth ->
    val user = auth.currentUser
    user?.getIdToken(false)?.addOnSuccessListener { result ->
        val token = result.token  // Send to backend
    }
}

// Remove listener (important for lifecycle)
val authStateListener = FirebaseAuth.AuthStateListener { /* ... */ }
FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)

// Get current user
val user = FirebaseAuth.getInstance().currentUser
user?.let {
    val uid = it.uid
    val name = it.displayName
    val email = it.email
    val photoUrl = it.photoUrl
    val isEmailVerified = it.isEmailVerified
    val providerId = it.providerId  // "google.com", "password", etc.
}
```

---

## Q3: How do you link accounts?

```kotlin
// Link anonymous user with email
val credential = EmailAuthProvider.getCredential(email, password)
currentUser.linkWithCredential(credential)

// Link anonymous user with Google
val googleCredential = GoogleAuthProvider.getCredential(idToken, null)
currentUser.linkWithCredential(googleCredential)

// Link phone auth
val phoneCredential = PhoneAuthProvider.getCredential(verificationId, code)
currentUser.linkWithCredential(phoneCredential)

// Unlink a provider
currentUser.unlink("google.com")

// Check linked providers
val providerData = currentUser.providerData  // List of UserInfo
```

### Use case: Anonymous → signed in
```kotlin
// 1. User browses anonymously
auth.signInAnonymously()

// 2. User decides to sign in with Google
// 3. Link the anonymous account with Google credential
// 4. All anonymous data (cart, preferences) is preserved
```

---

## Q4: How do you update user profile?

```kotlin
val user = FirebaseAuth.getInstance().currentUser!!

// Update display name
val profileUpdates = UserProfileChangeRequest.Builder()
    .setDisplayName("Alice")
    .setPhotoUri(Uri.parse("https://..."))
    .build()
user.updateProfile(profileUpdates)

// Update email
user.updateEmail("new@example.com")

// Send email verification
user.sendEmailVerification()

// Update password
user.updatePassword("newPassword123")

// Send password reset email
FirebaseAuth.getInstance().sendPasswordResetEmail("user@example.com")

// Delete user
user.delete()
```

---

## Q5: How do you sign out?

```kotlin
// Sign out from Firebase
FirebaseAuth.getInstance().signOut()

// Sign out from Google (needed if using Google Sign-In)
GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).signOut()

// Sign out from all providers
for (profile in user.providerData) {
    when (profile.providerId) {
        "google.com" -> GoogleSignIn.getClient(context, gso).signOut()
        "facebook.com" -> LoginManager.getInstance().logOut()
    }
}
FirebaseAuth.getInstance().signOut()
```

---

## Q6: How do you handle phone authentication?

```kotlin
val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        // Auto-verification (instant verification)
        signInWithPhoneAuthCredential(credential)
    }

    override fun onVerificationFailed(e: FirebaseException) {
        if (e is FirebaseAuthInvalidCredentialsException) {
            // Invalid phone number
        } else if (e is FirebaseTooManyRequestsException) {
            // SMS quota exceeded
        }
    }

    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
        // SMS code sent — user enters code
        this.verificationId = verificationId
    }
}

// Send verification code
PhoneAuthProvider.verifyPhoneNumber(
    "+1234567890",
    60,
    TimeUnit.SECONDS,
    activity,
    callbacks
)

// Verify code entered by user
val credential = PhoneAuthProvider.getCredential(verificationId, "123456")
FirebaseAuth.getInstance().signInWithCredential(credential)
```

---

## Q7: How do you get the ID token for backend verification?

```kotlin
// Get ID token (force refresh)
FirebaseAuth.getInstance().currentUser?.getIdToken(true)
    ?.addOnSuccessListener { result ->
        val idToken = result.token
        // Send to backend for verification
    }

// Kotlin Coroutines
suspend fun getIdToken(): String? {
    val result = FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()
    return result?.token
}
```

### Backend verification (Node.js)
```javascript
const admin = require('firebase-admin');
admin.initializeApp();

async function verifyToken(idToken) {
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    const uid = decodedToken.uid;
    const email = decodedToken.email;
    return { uid, email };
}
```

---

## 🔗 Related Topics
- [Firestore Basics](FirestoreBasics.md)
- [Security Rules](../intermediate/SecurityRules.md)
- [Cloud Functions](../intermediate/CloudFunctions.md)
