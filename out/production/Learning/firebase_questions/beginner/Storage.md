# Firebase Storage

## Q1: What is Cloud Storage for Firebase?

Object storage for user-generated content (images, videos, audio). Files are stored in Google Cloud Storage buckets.

```kotlin
val storage = Firebase.storage
val storageRef = storage.reference
```

### Storage structure
```
gs://your-app.appspot.com/  (root)
  └── images/
        └── profile_123.jpg
  └── videos/
        └── upload_456.mp4
  └── thumbnails/
        └── thumb_789.png
```

---

## Q2: How do you upload files?

```kotlin
val storageRef = Firebase.storage.reference.child("images/profile_123.jpg")

// Upload from file
val file = Uri.fromFile(File("path/to/image.jpg"))
val uploadTask = storageRef.putFile(file)

// Upload from byte array
val data = ByteArray(1024)
storageRef.putBytes(data)

// Upload from stream
val stream = FileInputStream(File("path/to/file"))
storageRef.putStream(stream)

// Monitor upload progress
uploadTask.addOnProgressListener { taskSnapshot ->
    val percent = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
    println("Upload: $percent%")
}.addOnSuccessListener {
    // Upload complete
}.addOnFailureListener { e ->
    // Upload failed
}

// Get download URL after upload
uploadTask.continueWithTask { task ->
    if (!task.isSuccessful) task.exception?.let { throw it }
    storageRef.downloadUrl
}.addOnSuccessListener { uri ->
    val downloadUrl = uri.toString()
}
```

---

## Q3: How do you download files?

```kotlin
val storageRef = Firebase.storage.reference.child("images/profile_123.jpg")

// Download to local file
val localFile = File.createTempFile("profile", "jpg")
storageRef.getFile(localFile).addOnSuccessListener {
    // File downloaded
}

// Download as bytes
storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

// Get download URL (for loading in Glide/Coil)
storageRef.downloadUrl.addOnSuccessListener { uri ->
    // Use URL with image loading library
}

// Download via stream
storageRef.stream.addOnSuccessListener { taskSnapshot ->
    val inputStream = taskSnapshot.stream
    // Read from stream
}
```

---

## Q4: How do you manage file metadata?

```kotlin
val storageRef = Firebase.storage.reference.child("images/profile_123.jpg")

// Create with metadata
val metadata = storageMetadata {
    contentType = "image/jpeg"
    setCustomMetadata("userId", "123")
    setCustomMetadata("description", "Profile picture")
}
storageRef.putFile(fileUri, metadata)

// Update metadata
storageRef.updateMetadata(storageMetadata {
    setCustomMetadata("uploadedAt", System.currentTimeMillis().toString())
})

// Get metadata
storageRef.metadata.addOnSuccessListener { metadata ->
    val contentType = metadata.contentType
    val size = metadata.sizeBytes
    val userId = metadata.getCustomMetadata("userId")
}
```

---

## Q5: How do you delete files?

```kotlin
val storageRef = Firebase.storage.reference.child("images/profile_123.jpg")

// Delete
storageRef.delete().addOnSuccessListener {
    // File deleted
}.addOnFailureListener { e ->
    // Delete failed
}

// Delete with Cloud Function (for cleanup)
// Trigger on document delete → delete associated file
```

---

## Q6: How do you handle upload errors and retries?

```kotlin
uploadTask.addOnFailureListener { exception ->
    when (exception) {
        is StorageException -> {
            val errorCode = exception.errorCode
            when (errorCode) {
                StorageException.ERROR_NOT_AUTHORIZED -> { /* permission denied */ }
                StorageException.ERROR_QUOTA_EXCEEDED -> { /* quota exceeded */ }
                StorageException.ERROR_OBJECT_NOT_FOUND -> { /* file not found */ }
                StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> { /* retry limit */ }
            }
        }
    }
}

// Pause and resume
uploadTask.pause()
uploadTask.resume()
uploadTask.cancel()
```

---

## Q7: How do you use Storage with Firestore?

```kotlin
// Upload image + save URL to Firestore
val imageRef = Firebase.storage.reference.child("images/${UUID.randomUUID()}.jpg")
imageRef.putFile(imageUri).continueWithTask { task ->
    if (!task.isSuccessful) task.exception?.let { throw it }
    imageRef.downloadUrl
}.continueWithTask { urlTask ->
    val downloadUrl = urlTask.result.toString()
    Firebase.firestore.collection("users").document(userId)
        .update("profileImageUrl", downloadUrl)
}.addOnSuccessListener {
    // Image uploaded + URL saved
}
```

---

## 🔗 Related Topics
- [Authentication](Authentication.md)
- [Security Rules](../intermediate/SecurityRules.md)
- [Cloud Functions](../intermediate/CloudFunctions.md)
