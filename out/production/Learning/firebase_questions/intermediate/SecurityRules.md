# Firebase Security Rules

## Q1: What are Firebase Security Rules?

Declarative rules that control access to Firestore, Realtime DB, and Storage. Rules are evaluated on the server before any data is returned.

### Key principle
**Rules are filters, not queries.** If a query could return documents the user doesn't have access to, the entire query fails.

---

## Q2: How do you write Firestore security rules?

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Allow read/write only for authenticated users
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Public read, authenticated write
    match /posts/{postId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && resource.data.authorId == request.auth.uid;
    }

    // Role-based access
    match /admin/{document} {
      allow read, write: if isAdmin();
    }

    function isAdmin() {
      return request.auth != null &&
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'admin';
    }
  }
}
```

---

## Q3: How do you validate data in rules?

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /users/{userId} {
      allow create: if request.auth != null
        && request.auth.uid == userId
        && validateUser();

      allow update: if request.auth != null
        && request.auth.uid == userId
        && validateUserUpdate();

      function validateUser() {
        return request.resource.data.name is string
          && request.resource.data.name.size() > 0
          && request.resource.data.name.size() <= 50
          && request.resource.data.email is string
          && request.resource.data.email.matches('^[^@]+@[^@]+\\.[^@]+$');
      }

      function validateUserUpdate() {
        // Only allow updating specific fields
        return request.resource.data.name == resource.data.name
          || request.resource.data.diff(resource.data).affectedKeys()
            .hasOnly(['name', 'photoUrl']);
      }
    }

    // Validate array size
    match /posts/{postId} {
      allow create: if request.resource.data.tags is list
        && request.resource.data.tags.size() <= 10;
    }
  }
}
```

---

## Q4: How do you write Realtime DB rules?

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "auth != null && auth.uid == $uid",
        ".write": "auth != null && auth.uid == $uid"
      }
    },
    "messages": {
      ".read": "auth != null",
      ".write": "auth != null && newData.exists()",
      "$messageId": {
        ".validate": "newData.hasChildren(['text', 'sender', 'timestamp'])",
        "text": { ".validate": "newData.isString() && newData.val().length <= 500" },
        "sender": { ".validate": "newData.val() == auth.uid" }
      }
    },
    "public": {
      ".read": true,
      ".write": "auth != null"
    }
  }
}
```

### Indexes in RTDB rules
```json
{
  "rules": {
    "messages": {
      ".indexOn": ["timestamp", "sender"]
    }
  }
}
```

---

## Q5: How do you write Storage rules?

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {

    // User can only access their own files
    match /users/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Public read for images
    match /public/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null
        && request.resource.size < 5 * 1024 * 1024  // 5MB
        && request.resource.contentType.matches('image/.*');
    }

    // Profile pictures — only images, max 2MB
    match /profiles/{userId}.jpg {
      allow read: if true;
      allow write: if request.auth != null
        && request.auth.uid == userId
        && request.resource.size < 2 * 1024 * 1024
        && request.resource.contentType == 'image/jpeg';
    }
  }
}
```

---

## Q6: How do you handle multi-tenant access?

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /tenants/{tenantId}/{document=**} {
      allow read, write: if belongsToTenant(tenantId);

      function belongsToTenant(tenantId) {
        return request.auth != null &&
          get(/databases/$(database)/documents/tenants/$(tenantId)/members/$(request.auth.uid)).exists();
      }
    }

    // Or check a role field
    match /tenants/{tenantId}/data/{documentId} {
      allow read: if hasRole(tenantId, 'viewer');
      allow write: if hasRole(tenantId, 'editor');

      function hasRole(tenantId, requiredRole) {
        return request.auth != null &&
          get(/databases/$(database)/documents/tenants/$(tenantId)/members/$(request.auth.uid))
            .data.role == requiredRole;
      }
    }
  }
}
```

---

## Q7: What are common rule patterns?

```
// 1. User can only access their own data
match /users/{userId} {
  allow read, write: if request.auth.uid == userId;
}

// 2. Friends can read each other's data
match /users/{userId} {
  allow read: if isFriend(userId);
  function isFriend(uid) {
    return request.auth != null &&
      exists(/databases/$(database)/documents/users/$(request.auth.uid)/friends/$(uid));
  }
}

// 3. Only create, no update/delete
match /messages/{messageId} {
  allow create: if request.auth != null;
  allow update, delete: if false;
}

// 4. Time-limited access
match /documents/{docId} {
  allow read: if request.auth != null &&
    resource.data.expiresAt > request.time;
}

// 5. Rate limiting (via counter document)
match /posts/{postId} {
  allow create: if request.auth != null &&
    get(/databases/$(database)/documents/users/$(request.auth.uid)).data.postsToday < 10;
}

// 6. Admin-only
match /{document=**} {
  allow read, write: if isAdmin();
  function isAdmin() {
    return request.auth.token.admin == true;
  }
}
```

### Rules best practices
- **Default deny** — start with `allow read, write: if false`
- **Test rules** — use the Rules simulator in Firebase Console
- **Use functions** — DRY for common checks
- **Validate data** — don't trust client input
- **Check `resource` vs `request.resource`** — before vs after
- **Avoid `get()` in loops** — each `get()` is a read operation

---

## 🔗 Related Topics
- [Firestore Basics](../beginner/FirestoreBasics.md)
- [Authentication](../beginner/Authentication.md)
- [Storage](../beginner/Storage.md)
