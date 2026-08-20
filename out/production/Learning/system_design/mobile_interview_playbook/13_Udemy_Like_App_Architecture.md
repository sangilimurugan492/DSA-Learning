# 🏗️ Udemy-Like App — Mobile Architecture & Data Model Design

> **Technical design document** for building an e-learning mobile app in Flutter with Clean Architecture.
> This complements the feature list (11) and interview answer (12).

---

## 1. Architecture Overview

### Clean Architecture Layers (Flutter + BLoC)

```
┌──────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐    │
│  │ Widgets  │  │ Screens  │  │ Routing  │  │  Theme   │    │
│  └────┬─────┘  └────┬─────┘  └──────────┘  └──────────┘    │
│       │              │                                         │
│       └──────┬───────┘                                         │
│              ↓                                                  │
│  ┌───────────────────────┐                                    │
│  │     BLoC / Cubit       │  Events → States                   │
│  └───────────┬───────────┘                                    │
└──────────────┼───────────────────────────────────────────────┘
               ↓
┌──────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                             │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐     │
│  │  Use Cases     │  │  Entities     │  │  Repository   │     │
│  │  (Interactors) │  │  (Models)     │  │  Interfaces   │     │
│  └───────┬───────┘  └───────────────┘  └───────────────┘     │
│          │                                                      │
│  ┌───────▼───────────────────────────────────────────────┐   │
│  │  SearchCoursesUC  GetCourseDetailUC  StreamLectureUC   │   │
│  │  PurchaseCourseUC  TrackProgressUC  DownloadLectureUC │   │
│  │  SubmitQuizUC      GenerateCertUC    SyncProgressUC    │   │
│  └───────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
               ↓
┌──────────────────────────────────────────────────────────────┐
│                        DATA LAYER                              │
│  ┌──────────────────────────────────────────────────────┐    │
│  │                  Repository Implementations           │    │
│  │  CatalogRepo  CourseRepo  VideoRepo  EnrollmentRepo   │    │
│  │  ProgressRepo  DownloadRepo  AuthRepo  PaymentRepo    │    │
│  └──────────┬───────────────────────────┬───────────────┘    │
│             ↓                             ↓                    │
│  ┌─────────────────────┐     ┌───────────────────────┐      │
│  │  Remote Data Sources │     │  Local Data Sources    │      │
│  │  • Dio (REST API)   │     │  • Drift (SQLite)      │      │
│  │  • HLS Client        │     │  • Hive (key-value)    │      │
│  │  • DRM License API   │     │  • File System (downloads)│  │
│  │  • Payment SDK       │     │  • Secure Storage      │      │
│  └─────────────────────┘     └───────────────────────┘      │
└──────────────────────────────────────────────────────────────┘
```

---

## 2. Domain Entities (Data Models)

### Core Entities:

```dart
// ============ USER ============
class User {
  final String id;
  final String email;
  final String name;
  final String? avatarUrl;
  final UserRole role;           // student | instructor
  final DateTime createdAt;
  final SubscriptionStatus subscription;  // none | active | expired
}

// ============ COURSE ============
class Course {
  final String id;
  final String title;
  final String description;
  final String instructorId;
  final String instructorName;
  final String? instructorAvatar;
  final String category;
  final String subCategory;
  final CourseLevel level;       // beginner | intermediate | advanced
  final String language;
  final double price;
  final double? discountPrice;
  final double rating;
  final int ratingCount;
  final int enrollmentCount;
  final int totalLectures;
  final Duration totalDuration;
  final String? trailerUrl;
  final String thumbnailUrl;
  final List<String> whatYouLearn;
  final List<String> requirements;
  final List<String> targetAudience;
  final DateTime createdAt;
  final DateTime updatedAt;
}

// ============ SECTION ============
class Section {
  final String id;
  final String courseId;
  final String title;
  final int order;
  final List<Lecture> lectures;
}

// ============ LECTURE ============
class Lecture {
  final String id;
  final String sectionId;
  final String title;
  final int order;
  final LectureType type;        // video | article | quiz
  final Duration? duration;
  final bool isPreview;          // free preview (no purchase needed)
  final String? hlsUrl;          // HLS manifest URL
  final String? articleContent;
  final String? quizId;
  final List<Attachment> attachments;
  final bool isDownloaded;
  final DownloadStatus downloadStatus;
}

enum LectureType { video, article, quiz }
enum DownloadStatus { none, downloading, paused, completed, error }

// ============ ATTACHMENT ============
class Attachment {
  final String id;
  final String lectureId;
  final String filename;
  final String fileUrl;
  final AttachmentType type;     // pdf, slide, sourceCode, other
  final int sizeBytes;
  final bool isDownloaded;
}

// ============ ENROLLMENT ============
class Enrollment {
  final String id;
  final String userId;
  final String courseId;
  final DateTime enrolledAt;
  final EnrollmentType type;     // purchased | subscription
  final double progressPercent;  // 0-100
  final String? lastLectureId;
  final Duration? lastPosition;
  final DateTime? lastAccessedAt;
  final bool isCompleted;
  final DateTime? completedAt;
}

enum EnrollmentType { purchased, subscription }

// ============ PROGRESS ============
class LectureProgress {
  final String id;
  final String userId;
  final String courseId;
  final String lectureId;
  final Duration position;
  final Duration duration;
  final bool isCompleted;
  final DateTime? completedAt;
  final DateTime updatedAt;
  final SyncStatus syncStatus;  // pending | synced | error
}

enum SyncStatus { pending, synced, error }

// ============ QUIZ ============
class Quiz {
  final String id;
  final String lectureId;
  final String title;
  final List<QuizQuestion> questions;
  final int passingScore;       // percentage
  final int maxAttempts;
}

class QuizQuestion {
  final String id;
  final String question;
  final List<QuizOption> options;
  final String? correctOptionId;   // null until submitted
  final String? explanation;
}

class QuizOption {
  final String id;
  final String text;
  final bool isCorrect;
}

class QuizAttempt {
  final String id;
  final String quizId;
  final String userId;
  final int score;
  final bool passed;
  final DateTime attemptedAt;
  final SyncStatus syncStatus;
}

// ============ REVIEW ============
class Review {
  final String id;
  final String courseId;
  final String userId;
  final String userName;
  final String? userAvatar;
  final int rating;              // 1-5
  final String comment;
  final DateTime createdAt;
  final int helpfulCount;
  final bool isHelpful;          // current user marked helpful
}

// ============ WISHLIST ============
class WishlistItem {
  final String id;
  final String userId;
  final String courseId;
  final DateTime addedAt;
  final SyncStatus syncStatus;
}

// ============ CART ============
class CartItem {
  final String id;
  final String userId;
  final String courseId;
  final Course course;           // embedded for display
  final DateTime addedAt;
}

// ============ ORDER ============
class Order {
  final String id;
  final String userId;
  final List<OrderItem> items;
  final double totalAmount;
  final double discountAmount;
  final double finalAmount;
  final String? couponCode;
  final OrderStatus status;     // pending | paid | failed | refunded
  final PaymentMethod paymentMethod;
  final String idempotencyKey;
  final DateTime createdAt;
}

class OrderItem {
  final String courseId;
  final String courseTitle;
  final double price;
}

enum OrderStatus { pending, paid, failed, refunded }
enum PaymentMethod { card, upi, wallet, netBanking }

// ============ CERTIFICATE ============
class Certificate {
  final String id;
  final String userId;
  final String courseId;
  final String courseTitle;
  final String userName;
  final DateTime issuedAt;
  final String certificateUrl;   // PDF URL
  final String certificateNumber;
}

// ============ DOWNLOAD TASK ============
class DownloadTask {
  final String id;
  final String lectureId;
  final String courseId;
  final String lectureTitle;
  final DownloadStatus status;
  final double progress;         // 0-100
  final int totalBytes;
  final int downloadedBytes;
  final DateTime? startedAt;
  final DateTime? completedAt;
  final String? error;
}
```

---

## 3. Local Database Schema (Drift / SQLite)

```sql
-- Courses cache (for offline browse)
CREATE TABLE cached_courses (
  id              TEXT PRIMARY KEY,
  title           TEXT NOT NULL,
  description     TEXT,
  instructor_name TEXT,
  category        TEXT,
  level           TEXT,
  price           REAL,
  discount_price  REAL,
  rating          REAL,
  rating_count    INTEGER,
  thumbnail_url   TEXT,
  total_lectures  INTEGER,
  total_duration  INTEGER,       -- seconds
  cached_at       INTEGER NOT NULL  -- epoch millis (for TTL)
);

-- Sections (cached with course detail)
CREATE TABLE cached_sections (
  id          TEXT PRIMARY KEY,
  course_id   TEXT NOT NULL,
  title       TEXT NOT NULL,
  sort_order  INTEGER NOT NULL,
  FOREIGN KEY (course_id) REFERENCES cached_courses(id)
);

-- Lectures (cached with course detail)
CREATE TABLE cached_lectures (
  id              TEXT PRIMARY KEY,
  section_id      TEXT NOT NULL,
  title           TEXT NOT NULL,
  sort_order      INTEGER NOT NULL,
  type            TEXT NOT NULL,     -- video | article | quiz
  duration        INTEGER,
  is_preview      INTEGER NOT NULL,  -- 0 or 1
  hls_url         TEXT,
  is_downloaded   INTEGER NOT NULL DEFAULT 0,
  FOREIGN KEY (section_id) REFERENCES cached_sections(id)
);

-- Enrolled courses (My Learning)
CREATE TABLE enrollments (
  id                  TEXT PRIMARY KEY,
  course_id           TEXT NOT NULL,
  enrolled_at         INTEGER NOT NULL,
  type                TEXT NOT NULL,
  progress_percent    REAL NOT NULL DEFAULT 0,
  last_lecture_id     TEXT,
  last_position       INTEGER,       -- seconds
  last_accessed_at    INTEGER,
  is_completed        INTEGER NOT NULL DEFAULT 0,
  completed_at        INTEGER
);

-- Lecture progress (per lecture, synced)
CREATE TABLE lecture_progress (
  id              TEXT PRIMARY KEY,
  course_id       TEXT NOT NULL,
  lecture_id      TEXT NOT NULL,
  position        INTEGER NOT NULL,  -- seconds
  duration        INTEGER NOT NULL,
  is_completed    INTEGER NOT NULL DEFAULT 0,
  completed_at    INTEGER,
  updated_at      INTEGER NOT NULL,
  sync_status     TEXT NOT NULL DEFAULT 'pending',
  UNIQUE(lecture_id)
);

-- Wishlist (local-first)
CREATE TABLE wishlist (
  id          TEXT PRIMARY KEY,
  course_id   TEXT NOT NULL,
  added_at    INTEGER NOT NULL,
  sync_status TEXT NOT NULL DEFAULT 'pending',
  UNIQUE(course_id)
);

-- Cart
CREATE TABLE cart_items (
  id          TEXT PRIMARY KEY,
  course_id   TEXT NOT NULL,
  added_at    INTEGER NOT NULL,
  UNIQUE(course_id)
);

-- Download tasks
CREATE TABLE download_tasks (
  id                TEXT PRIMARY KEY,
  lecture_id        TEXT NOT NULL,
  course_id         TEXT NOT NULL,
  lecture_title     TEXT NOT NULL,
  status            TEXT NOT NULL,     -- none|downloading|paused|completed|error
  progress          REAL NOT NULL DEFAULT 0,
  total_bytes       INTEGER NOT NULL DEFAULT 0,
  downloaded_bytes  INTEGER NOT NULL DEFAULT 0,
  started_at        INTEGER,
  completed_at      INTEGER,
  error             TEXT,
  UNIQUE(lecture_id)
);

-- Sync metadata
CREATE TABLE sync_metadata (
  key   TEXT PRIMARY KEY,
  value INTEGER
);
-- Key values: 'last_progress_sync', 'last_catalog_sync', 'last_wishlist_sync'

-- Search history
CREATE TABLE search_history (
  id          INTEGER PRIMARY KEY AUTOINCREMENT,
  query       TEXT NOT NULL,
  searched_at INTEGER NOT NULL
);

-- Notifications
CREATE TABLE notifications (
  id          TEXT PRIMARY KEY,
  type        TEXT NOT NULL,
  title       TEXT NOT NULL,
  body        TEXT,
  data        TEXT,          -- JSON payload
  is_read     INTEGER NOT NULL DEFAULT 0,
  created_at  INTEGER NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_lecture_progress_course ON lecture_progress(course_id);
CREATE INDEX idx_lecture_progress_sync ON lecture_progress(sync_status);
CREATE INDEX idx_cached_courses_category ON cached_courses(category);
CREATE INDEX idx_wishlist_sync ON wishlist(sync_status);
CREATE INDEX idx_download_tasks_status ON download_tasks(status);
```

---

## 4. BLoC State Design

### CourseDetailBloc Example:

```dart
// EVENTS
abstract class CourseDetailEvent {}
class FetchCourseDetail extends CourseDetailEvent {
  final String courseId;
}
class ToggleWishlist extends CourseDetailEvent {
  final String courseId;
}
class AddToCart extends CourseDetailEvent {
  final String courseId;
}
class BuyNow extends CourseDetailEvent {
  final String courseId;
}

// STATES
abstract class CourseDetailState {}
class CourseDetailInitial extends CourseDetailState {}
class CourseDetailLoading extends CourseDetailState {}
class CourseDetailLoaded extends CourseDetailState {
  final Course course;
  final List<Section> sections;
  final List<Review> reviews;
  final bool isInWishlist;
  final bool isInCart;
  final bool isEnrolled;
}
class CourseDetailError extends CourseDetailState {
  final String message;
}

// BLoC
class CourseDetailBloc extends Bloc<CourseDetailEvent, CourseDetailState> {
  CourseDetailBloc({
    required GetCourseDetailUseCase getDetailUseCase,
    required ToggleWishlistUseCase toggleWishlistUseCase,
    required AddToCartUseCase addToCartUseCase,
  }) : super(CourseDetailInitial()) {
    on<FetchCourseDetail>(_onFetch);
    on<ToggleWishlist>(_onToggleWishlist);
    on<AddToCart>(_onAddToCart);
  }
  // ... handlers call use cases, emit states
}
```

### PlayerBloc State Machine:

```
Player States:
  ┌──────────┐    load    ┌──────────┐
  │  Initial  │──────────→│ Loading  │
  └──────────┘            └────┬─────┘
                                 │ loaded
                                 ↓
  ┌──────────┐    error   ┌──────────┐    play    ┌──────────┐
  │  Error   │←──────────│  Ready   │──────────→│ Playing  │
  └──────────┘            └──────────┘            └────┬─────┘
                                 ↑                       │ pause
                                 │                       ↓
                                 │                  ┌──────────┐
                                 └──────────────────│ Paused   │
                                  resume            └──────────┘

  Sub-states during Playing:
  → Buffering (show spinner)
  → Playing (video active)
  → Ended (auto-advance or show "Next")
```

---

## 5. API Endpoints

```
AUTH:
  POST   /auth/login              → { access_token, refresh_token }
  POST   /auth/register           → { access_token, refresh_token }
  POST   /auth/refresh            → { access_token }
  POST   /auth/logout             → 204

CATALOG:
  GET    /catalog/recommendations → [Course]
  GET    /catalog/trending        → [Course]
  GET    /catalog/categories      → [Category]

COURSE:
  GET    /courses/{id}            → Course + Sections + Lectures
  GET    /courses/search?q=&f=&cursor= → [Course] (cursor pagination)
  GET    /courses/{id}/reviews?cursor= → [Review]

LECTURE:
  GET    /lectures/{id}/manifest   → { hls_url, drm_needed }
  POST   /lectures/{id}/download-manifest → { segments[], drm_license_url }
  GET    /lectures/{id}/attachments → [Attachment]

DRM:
  POST   /drm/license             → { license_token }

ENROLLMENT:
  GET    /enrollments              → [Enrollment] (My Learning)
  GET    /enrollments/{courseId}/progress → { last_lecture, position, percent }

PROGRESS:
  POST   /progress/batch           → [{ lecture_id, position, completed }]
  GET    /progress/{courseId}      → [LectureProgress]

ORDER:
  POST   /orders                   → Order (idempotency key required)
  GET    /orders                   → [Order] (purchase history)
  POST   /orders/{id}/refund       → Refund

WISHLIST:
  GET    /wishlist                 → [Course]
  POST   /wishlist/{courseId}      → 201
  DELETE /wishlist/{courseId}      → 204

QUIZ:
  GET    /quizzes/{id}             → Quiz (questions, no answers)
  POST   /quizzes/{id}/submit      → { score, passed, correct_answers }

CERTIFICATE:
  POST   /certificates             → Certificate (when course 100%)
  GET    /certificates             → [Certificate]

NOTIFICATION:
  GET    /notifications?cursor=    → [Notification]
  POST   /notifications/{id}/read  → 204
  PUT    /notifications/preferences → 200

SUBSCRIPTION:
  POST   /subscriptions            → Subscription (start/trial)
  GET    /subscriptions/current    → Subscription
  DELETE /subscriptions            → 204 (cancel)
```

---

## 6. Dependency Injection (get_it)

```dart
// injection_container.dart
final sl = GetIt.instance;

Future<void> init() async {
  // ===== EXTERNAL =====
  sl.registerLazySingleton(() => Dio());
  sl.registerLazySingleton<Database>(() => AppDatabase());

  // ===== DATA SOURCES =====
  sl.registerLazySingleton<CourseRemoteDataSource>(
    () => CourseRemoteDataSourceImpl(sl<Dio>()));
  sl.registerLazySingleton<CourseLocalDataSource>(
    () => CourseLocalDataSourceImpl(sl<Database>()));
  sl.registerLazySingleton<ProgressLocalDataSource>(
    () => ProgressLocalDataSourceImpl(sl<Database>()));
  sl.registerLazySingleton<DownloadDataSource>(
    () => DownloadDataSourceImpl(sl<Database>()));

  // ===== REPOSITORIES =====
  sl.registerLazySingleton<CourseRepository>(
    () => CourseRepositoryImpl(
      remote: sl<CourseRemoteDataSource>(),
      local: sl<CourseLocalDataSource>(),
    ));
  sl.registerLazySingleton<ProgressRepository>(
    () => ProgressRepositoryImpl(
      local: sl<ProgressLocalDataSource>(),
      remote: sl<ProgressRemoteDataSource>(),
    ));
  sl.registerLazySingleton<DownloadRepository>(
    () => DownloadRepositoryImpl(sl<DownloadDataSource>()));

  // ===== USE CASES =====
  sl.registerLazySingleton(() => SearchCoursesUseCase(sl<CourseRepository>()));
  sl.registerLazySingleton(() => GetCourseDetailUseCase(sl<CourseRepository>()));
  sl.registerLazySingleton(() => StreamLectureUseCase(sl<VideoRepository>(), sl<DRMRepository>()));
  sl.registerLazySingleton(() => DownloadLectureUseCase(sl<DownloadRepository>()));
  sl.registerLazySingleton(() => TrackProgressUseCase(sl<ProgressRepository>()));
  sl.registerLazySingleton(() => SyncProgressUseCase(sl<ProgressRepository>()));
  sl.registerLazySingleton(() => PurchaseCourseUseCase(sl<OrderRepository>()));

  // ===== BLOCS =====
  sl.registerFactory(() => HomeBloc(
    getRecommendations: sl(),
    getTrending: sl(),
  ));
  sl.registerFactory(() => CourseDetailBloc(
    getDetail: sl(),
    toggleWishlist: sl(),
    addToCart: sl(),
  ));
  sl.registerFactory(() => PlayerBloc(
    stream: sl(),
    trackProgress: sl(),
  ));
  sl.registerFactory(() => MyLearningBloc(
    getEnrollments: sl(),
  ));
  sl.registerFactory(() => DownloadBloc(
    download: sl(),
  ));
}
```

---

## 7. Key Design Decisions

| Area | Decision | Rationale |
|------|----------|-----------|
| State Management | BLoC | Testable, event-driven, scales for complex state (video player, downloads) |
| Local DB | Drift (SQLite) | Relational data (courses → sections → lectures), SQL queries for filtering |
| Networking | Dio | Interceptors (auth, retry, logging), cancel tokens, FormData |
| Video | Native via platform channels | DRM (Widevine/FairPlay), ABR, hardware decode — not possible in pure Flutter |
| DI | get_it + injectable | Compile-time safe, no performance overhead |
| Routing | go_router | Declarative, deep linking, nested navigation |
| Image Loading | CachedNetworkImage | Disk + memory cache, placeholder, fade-in |
| Downloads | workmanager | Background, survives app kill, constraints (WiFi only) |
| Push | firebase_messaging | FCM for Android + iOS, data + notification payloads |
| Secure Storage | flutter_secure_storage | Keystore (Android) / Keychain (iOS) for tokens |
| Encryption | SQLCipher (via drift) | Encrypt local DB (progress, enrollment data) |
| Code Gen | freezed + json_serializable | Immutable models, copyWith, JSON (de)serialization |
| Testing | bloc_test, mockito, integration_test | BLoC testing, mock use cases, E2E |
