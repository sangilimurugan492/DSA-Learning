# Android WebRTC Implementation

## Q1: How do you set up WebRTC on Android?

### Dependencies
```gradle
// build.gradle (app)
dependencies {
    // Google's official WebRTC for Android
    implementation 'org.webrtc:google-webrtc:1.0.32006'
    
    // OR use Stream WebRTC (better maintained)
    implementation 'io.getstream:stream-webrtc-android:1.0.5'
}
```

### Permissions
```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- For screen share -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION" />

<uses-feature android:name="android.hardware.camera" android:required="false" />
<uses-feature android:name="android.hardware.microphone" android:required="false" />
```

### Runtime permissions
```kotlin
val permissions = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO
)
ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
```

---

## Q2: How do you initialize PeerConnectionFactory?

```kotlin
class WebRTCManager(context: Context) {

    private val rootEglBase: EglBase = EglBase.create()

    init {
        // Initialize WebRTC
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(false)
                .createInitializationOptions()
        )
    }

    private val factory: PeerConnectionFactory = PeerConnectionFactory.builder()
        .setVideoEncoderFactory(
            DefaultVideoEncoderFactory(rootEglBase.eglBaseContext, true, true)
        )
        .setVideoDecoderFactory(
            DefaultVideoDecoderFactory(rootEglBase.eglBaseContext)
        )
        .createPeerConnectionFactory()

    fun createPeerConnection(iceServers: List<PeerConnection.IceServer>): PeerConnection {
        val config = PeerConnection.RTCConfiguration(iceServers).apply {
            iceServers = listOf(
                PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
                PeerConnection.IceServer.builder("turn:turn.example.com:3478")
                    .setUsername("user")
                    .setPassword("pass")
                    .createIceServer()
            )
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        }

        return factory.createPeerConnection(config, observer)!!
    }
}
```

---

## Q3: How do you capture and render video?

### Capture camera
```kotlin
fun createVideoTrack(context: Context): VideoTrack {
    val videoCapturer = createCameraCapturer(context)
    
    val surfaceTextureHelper = SurfaceTextureHelper.create(
        "CaptureThread", rootEglBase.eglBaseContext
    )

    val videoSource = factory.createVideoSource(false)
    videoCapturer?.initialize(
        surfaceTextureHelper,
        context,
        videoSource.capturerObserver
    )
    videoCapturer?.startCapture(1280, 720, 30)  // 720p @ 30fps

    return factory.createVideoTrack("VIDEO_LOCAL", videoSource)
}

private fun createCameraCapturer(context: Context): Camera2Enumerator? {
    val enumerator = Camera2Enumerator(context)
    val deviceNames = enumerator.deviceNames

    // Try back camera first
    for (name in deviceNames) {
        if (enumerator.isBackFacing(name)) {
            return enumerator.createCapturer(name, null)
        }
    }
    // Fallback to front camera
    for (name in deviceNames) {
        if (enumerator.isFrontFacing(name)) {
            return enumerator.createCapturer(name, null)
        }
    }
    return null
}
```

### Render video
```kotlin
// In Activity/Fragment layout
<org.webrtc.SurfaceViewRenderer
    android:id="@+id/localVideoView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />

// Initialize and render
val localVideoView = findViewById<SurfaceViewRenderer>(R.id.localVideoView)
localVideoView.init(rootEglBase.eglBaseContext, null)
localVideoView.setMirror(true)  // Mirror for front camera
localVideoView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)

// Attach track to renderer
localVideoTrack.addSink(localVideoView)
```

### Switch camera
```kotlin
fun switchCamera(videoCapturer: CameraVideoCapturer) {
    videoCapturer.switchCamera(object : CameraVideoCapturer.CameraSwitchHandler {
        override fun onCameraSwitchDone(isFrontCamera: Boolean) {
            // Update UI
        }
        override fun onCameraSwitchError(errorDescription: String?) {
            // Handle error
        }
    })
}
```

---

## Q4: How do you capture audio?

```kotlin
fun createAudioTrack(): AudioTrack {
    val audioConstraints = MediaConstraints()
    val audioSource = factory.createAudioSource(audioConstraints)
    
    return factory.createAudioTrack("AUDIO_LOCAL", audioSource).apply {
        // Enable/disable audio
        setEnabled(true)
    }
}

// Mute/unmute
fun toggleMute(audioTrack: AudioTrack, isMuted: Boolean) {
    audioTrack.setEnabled(!isMuted)
}

// Audio focus (handle interruptions)
fun requestAudioFocus(context: Context) {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    audioManager.requestAudioFocus(
        null,
        AudioManager.STREAM_VOICE_CALL,
        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
    )
}
```

### Audio processing
```kotlin
// Enable noise suppression + echo cancellation
val audioConstraints = MediaConstraints().apply {
    mandatory.add(MediaConstraints.KeyValuePair(
        "googEchoCancellation", "true"
    ))
    mandatory.add(MediaConstraints.KeyValuePair(
        "googNoiseSuppression", "true"
    ))
    mandatory.add(MediaConstraints.KeyValuePair(
        "googAutoGainControl", "true"
    ))
}
```

---

## Q5: How do you add tracks to PeerConnection?

```kotlin
fun setupPeerConnection(
    peerConnection: PeerConnection,
    localVideoTrack: VideoTrack,
    localAudioTrack: AudioTrack
) {
    // Create media stream
    val streamId = "stream_local"
    
    // Add tracks to PeerConnection
    peerConnection.addTrack(localAudioTrack, listOf(streamId))
    peerConnection.addTrack(localVideoTrack, listOf(streamId))
}

// Handle remote tracks
val observer = object : PeerConnection.Observer {
    override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<MediaStream>) {
        val track = receiver.track()
        when (track) {
            is VideoTrack -> {
                // Render remote video
                track.addSink(remoteVideoView)
            }
            is AudioTrack -> {
                // Audio auto-plays through speaker
                track.setEnabled(true)
            }
        }
    }
    
    override fun onRemoveStream(mediaStream: MediaStream) {
        // Remote peer stopped sending
    }
    
    override fun onIceCandidate(candidate: IceCandidate) {
        // Send to remote peer via signaling
        signalingClient.sendIceCandidate(candidate)
    }
    
    override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
        when (state) {
            PeerConnection.IceConnectionState.CONNECTED -> { /* connected */ }
            PeerConnection.IceConnectionState.DISCONNECTED -> { /* try reconnect */ }
            PeerConnection.IceConnectionState.FAILED -> { /* ICE failed */ }
            else -> {}
        }
    }
    
    // ... other overrides
}
```

---

## Q6: How do you handle PeerConnection lifecycle?

```kotlin
class WebRTCCallManager {
    private var peerConnection: PeerConnection? = null
    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null

    fun startCall() {
        // 1. Create PeerConnection
        peerConnection = createPeerConnection()
        
        // 2. Add local tracks
        peerConnection?.addTrack(localAudioTrack)
        peerConnection?.addTrack(localVideoTrack)
        
        // 3. Create offer (caller)
        peerConnection?.createOffer(object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription) {
                peerConnection?.setLocalDescription(sdpObserver, sdp)
                signalingClient.sendOffer(sdp.description)
            }
            override fun onCreateFailure(error: String?) { /* handle */ }
            override fun onSetSuccess() {}
            override fun onSetFailure(error: String?) {}
        }, MediaConstraints())
    }

    fun endCall() {
        // Stop video capture
        videoCapturer?.stopCapture()
        videoCapturer?.dispose()
        
        // Remove tracks
        localVideoTrack?.dispose()
        localAudioTrack?.dispose()
        
        // Close PeerConnection
        peerConnection?.close()
        peerConnection?.dispose()
        peerConnection = null
        
        // Release renderer
        localVideoView.release()
        remoteVideoView.release()
    }

    fun toggleVideo(enabled: Boolean) {
        localVideoTrack?.setEnabled(enabled)
    }

    fun toggleAudio(enabled: Boolean) {
        localAudioTrack?.setEnabled(enabled)
    }
}
```

### Lifecycle checklist
| Event | Action |
|-------|--------|
| App backgrounded | Keep audio, pause video rendering |
| App foregrounded | Resume video rendering |
| Network change | Restart ICE gathering |
| Call ended | Dispose all resources |
| Activity destroyed | End call, release everything |

---

## Q7: How do you handle network changes and reconnection?

```kotlin
// Listen for network changes
val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
connectivityManager.registerDefaultNetworkCallback(object : NetworkCallback() {
    override fun onAvailable(network: Network) {
        // Network restored — restart ICE
        peerConnection?.restartIce()
    }
    
    override fun onLost(network: Network) {
        // Network lost — show reconnecting UI
    }
})

// PeerConnection observer
override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
    when (state) {
        PeerConnection.IceConnectionState.CONNECTED -> {
            // ✅ Connection established
            updateCallState(CallState.CONNECTED)
        }
        PeerConnection.IceConnectionState.DISCONNECTED -> {
            // ⚠️ Temporarily disconnected — wait for auto-reconnect
            updateCallState(CallState.RECONNECTING)
            // WebRTC will try to reconnect automatically
        }
        PeerConnection.IceConnectionState.FAILED -> {
            // ❌ ICE failed — need to restart
            updateCallState(CallState.FAILED)
            peerConnection?.restartIce()
        }
        PeerConnection.IceConnectionState.CLOSED -> {
            // Call ended
            updateCallState(CallState.ENDED)
        }
        else -> {}
    }
}

// Configure ICE restart
val config = PeerConnection.RTCConfiguration(iceServers).apply {
    iceRestartPolicy = PeerConnection.IceRestartPolicy.LIMITED
}
```

### Reconnection strategy
| State | Action | Timeout |
|-------|--------|---------|
| DISCONNECTED | Wait for auto-reconnect | 5s |
| Still DISCONNECTED | Restart ICE | — |
| FAILED | Full ICE restart | — |
| Still FAILED after 3 tries | End call | — |

---

## 🔗 Related Topics
- [WebRTC Basics](WebRTCBasics.md)
- [WebRTC Scenarios](WebRTCScenarios.md)
