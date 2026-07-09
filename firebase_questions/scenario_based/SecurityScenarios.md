# Firebase Security Scenarios

## Scenario 1: Role-Based Access Control

### Problem
Implement RBAC with roles: `admin`, `editor`, `viewer`. Only admins can delete, editors can write, viewers can read.

### Solution

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /content/{documentId} {
      allow read: if isAuthenticated();
      allow create: if hasRole('editor') || hasRole('admin');
      allow update: if hasRole('editor') || hasRole('admin');
      allow delete: if hasRole('admin');
    }

    function isAuthenticated() {
      return request.auth != null;
    }

    function hasRole(role) {
      return isAuthenticated() &&
        getUserData().role == role;
    }

    function getUserData() {
      return get(/databases/$(database)/documents/users/$(request.auth.uid)).data;
    }
  }
}
```

### Optimization: Use custom claims instead of Firestore reads
```javascript
// Set custom claim via Cloud Function
exports.setRole = functions.https.onCall(async (data, context) => {
  // Verify caller is admin
  await admin.auth().setCustomUserClaims(data.uid, { role: data.role });
});
```

```
// Rules with custom claims (no Firestore read needed)
function hasRole(role) {
  return request.auth != null && request.auth.token.role == role;
}
```

---

## Scenario 2: Multi-Tenant Application

### Problem
SaaS app where each tenant should only access their own data. Users belong to tenants with different roles.

### Solution

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /tenants/{tenantId}/{document=**} {
      allow read, write: if isTenantMember(tenantId);
    }

    match /tenants/{tenantId}/data/{documentId} {
      allow read: if hasTenantRole(tenantId, ['admin', 'editor', 'viewer']);
      allow write: if hasTenantRole(tenantId, ['admin', 'editor']);
      allow delete: if hasTenantRole(tenantId, ['admin']);
    }

    function isTenantMember(tenantId) {
      return request.auth != null &&
        exists(/databases/$(database)/documents/tenants/$(tenantId)/members/$(request.auth.uid));
    }

    function hasTenantRole(tenantId, roles) {
      return request.auth != null &&
        request.auth.uid in
          get(/databases/$(database)/documents/tenants/$(tenantId)/members/$(request.auth.uid))
            .data.role in roles;
    }
  }
}
```

### Data structure
```
tenants/{tenantId}
  ├── name: "Acme Corp"
  └── plan: "enterprise"

tenants/{tenantId}/members/{userId}
  ├── role: "admin" | "editor" | "viewer"
  ├── email: "user@acme.com"
  └── joinedAt: timestamp

tenants/{tenantId}/data/{documentId}
  └── { ... tenant-specific data ... }
```

---

## Scenario 3: User-Generated Content Validation

### Problem
Allow users to create posts with validation:
- Title: 1-100 chars, string
- Content: 1-10000 chars, string
- Tags: max 5, each max 20 chars
- No HTML/scripts
- Author must be the signed-in user

### Solution

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /posts/{postId} {
      allow read: if true;  // Public read
      allow create: if request.auth != null && validatePost();
      allow update: if request.auth != null
        && request.auth.uid == resource.data.authorId
        && validatePost();
      allow delete: if request.auth != null
        && request.auth.uid == resource.data.authorId;

      function validatePost() {
        return request.resource.data.authorId == request.auth.uid
          && request.resource.data.title is string
          && request.resource.data.title.size() > 0
          && request.resource.data.title.size() <= 100
          && request.resource.data.content is string
          && request.resource.data.content.size() > 0
          && request.resource.data.content.size() <= 10000
          && request.resource.data.tags is list
          && request.resource.data.tags.size() <= 5
          && !request.resource.data.title.matches('<.*>')
          && !request.resource.data.content.matches('<script.*>');
      }
    }
  }
}
```

---

## Scenario 4: Time-Limited Access

### Problem
Documents should only be accessible for 24 hours after creation. After that, they're read-only.

### Solution

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /documents/{docId} {
      // Read: always allowed if authenticated
      allow read: if request.auth != null;

      // Create: only within 24 hours of creation
      allow create: if request.auth != null;

      // Update: only within 24 hours of creation
      allow update: if request.auth != null
        && request.time < resource.data.createdAt + duration.value(24, 'h');

      // Delete: only owner, anytime
      allow delete: if request.auth != null
        && request.auth.uid == resource.data.ownerId;
    }
  }
}
```

---

## Scenario 5: Rate Limiting

### Problem
Limit users to 10 posts per day to prevent spam.

### Solution

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /posts/{postId} {
      allow create: if request.auth != null
        && !hasExceededDailyLimit();

      function hasExceededDailyLimit() {
        val today = request.time.toDate();
        val recentPosts = get(/databases/$(database)/documents/users/$(request.auth.uid))
          .data.postsToday;
        return recentPosts >= 10;
      }
    }
  }
}
```

### Better approach: Cloud Function counter
```javascript
// Cloud Function: increment counter on post create
exports.onPostCreated = functions.firestore
  .document('posts/{postId}')
  .onCreate(async (snap, context) => {
    const authorId = snap.data().authorId;
    const today = new Date().toDateString();

    await admin.firestore().collection('userStats').doc(authorId)
      .set({
        postsToday: admin.firestore.FieldValue.increment(1),
        lastPostDate: today
      }, { merge: true });
  });

// Cloud Function: reset counter daily
exports.resetDailyCount = functions.pubsub
  .schedule('0 0 * * *')
  .onRun(async (context) => {
    const users = await admin.firestore().collection('userStats').get();
    const batch = admin.firestore().batch();
    users.docs.forEach(doc => {
      batch.update(doc.ref, { postsToday: 0 });
    });
    await batch.commit();
  });
```

---

## Scenario 6: File Upload Security

### Problem
Users can upload profile pictures:
- Max 2MB
- Only JPEG/PNG
- Only their own folder
- Max 1 file

### Solution

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {

    match /profiles/{userId}/{fileName} {
      allow read: if true;  // Public read
      allow write: if request.auth != null
        && request.auth.uid == userId
        && request.resource.size < 2 * 1024 * 1024  // 2MB
        && (request.resource.contentType == 'image/jpeg'
            || request.resource.contentType == 'image/png');
    }

    // Prevent uploads to other users' folders
    match /profiles/{userId}/{allPaths=**} {
      allow write: if request.auth != null
        && request.auth.uid == userId;
    }
  }
}
```

---

## 🔗 Related Topics
- [Security Rules](../intermediate/SecurityRules.md)
- [Authentication](../beginner/Authentication.md)
- [App Check](../advanced/AppCheck.md)
