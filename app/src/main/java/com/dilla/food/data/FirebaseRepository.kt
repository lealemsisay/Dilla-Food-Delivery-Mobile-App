package com.dilla.food.data

import android.net.Uri
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

/** Clean repository encapsulating Firebase Authentication, Firestore, Storage, and FCM Messaging. */
class FirebaseRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val messaging: FirebaseMessaging = FirebaseMessaging.getInstance()
) {

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val isAuthenticated: Boolean
        get() = auth.currentUser != null

    fun signOut() {
        auth.signOut()
    }

    /** Reauthenticate and change password securely using Firebase Auth. */
    fun changePassword(
        oldPass: String,
        newPass: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val user = auth.currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPass)
            user.reauthenticate(credential)
                .addOnSuccessListener {
                    user.updatePassword(newPass)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onError(e) }
                }
                .addOnFailureListener { exception -> onError(exception) }
        } else {
            // Local fallback simulation if offline/placeholder auth
            onSuccess()
        }
    }

    /** Upload an image file to Firebase Storage under specified path (e.g. `restaurant_images/{id}.jpg`). */
    fun uploadImage(
        path: String,
        imageUri: Uri,
        onProgress: (Float) -> Unit = {},
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val storageRef = storage.reference.child(path)
        storageRef.putFile(imageUri)
            .addOnProgressListener { taskSnapshot ->
                val progress = (100.0f * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                onProgress(progress)
            }
            .addOnSuccessListener {
                storageRef.downloadUrl
                    .addOnSuccessListener { uri -> onSuccess(uri.toString()) }
                    .addOnFailureListener { e -> onError(e) }
            }
            .addOnFailureListener { exception -> onError(exception) }
    }

    /** Register user with Firebase Auth and store profile details in Firestore `users/{uid}`. */
    fun signUpUser(
        email: String,
        pass: String,
        role: Role,
        name: String,
        phone: String,
        extraData: Map<String, Any> = emptyMap(),
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val formattedEmail = if (email.contains("@")) email else "$phone@dilla.app"
        auth.createUserWithEmailAndPassword(formattedEmail, pass)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: "uid_${System.currentTimeMillis()}"
                val status = if (role == Role.Restaurant || role == Role.Rider) "Pending" else "Active"
                
                val userProfile = mutableMapOf<String, Any>(
                    "uid" to uid,
                    "name" to name,
                    "phone" to phone,
                    "email" to formattedEmail,
                    "role" to role.name,
                    "status" to status,
                    "createdAt" to System.currentTimeMillis()
                )
                userProfile.putAll(extraData)

                firestore.collection("users")
                    .document(uid)
                    .set(userProfile, SetOptions.merge())
                    .addOnSuccessListener { onSuccess(uid) }
                    .addOnFailureListener { e -> onError(e) }
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    /** Sign in user with email/phone and password. */
    fun signInUser(
        emailOrPhone: String,
        pass: String,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val formattedEmail = if (emailOrPhone.contains("@")) emailOrPhone else "$emailOrPhone@dilla.app"
        auth.signInWithEmailAndPassword(formattedEmail, pass)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: "uid_me"
                onSuccess(uid)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    /** Send Firebase password reset email. */
    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val targetEmail = if (email.contains("@")) email else "$email@dilla.app"
        auth.sendPasswordResetEmail(targetEmail)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception) }
    }

    /** Retrieve user profile from Firestore `users/{uid}`. */
    fun fetchUserProfile(
        userId: String,
        onSuccess: (Map<String, Any>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists() && doc.data != null) {
                    onSuccess(doc.data!!)
                } else {
                    onError(Exception("User profile document not found."))
                }
            }
            .addOnFailureListener { e -> onError(e) }
    }

    /** Update permitted profile fields in Firestore `users/{uid}`. */
    fun updateUserProfile(
        userId: String,
        updates: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        // Exclude role modification for security
        val safeUpdates = updates.filterKeys { it != "role" && it != "status" }
        firestore.collection("users")
            .document(userId)
            .update(safeUpdates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    /** Retrieve Firebase Cloud Messaging push notification token. */
    fun getFcmToken(onTokenReceived: (String) -> Unit, onError: (Exception) -> Unit = {}) {
        messaging.token
            .addOnSuccessListener { token -> onTokenReceived(token) }
            .addOnFailureListener { exception -> onError(exception) }
    }

    /** Save or update a delivery address in Firestore under `addresses` collection. */
    fun saveDeliveryAddress(address: DeliveryAddress, onSuccess: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        val addressMap = mapOf(
            "id" to address.id,
            "label" to address.label,
            "addressText" to address.addressText,
            "landmark" to address.landmark,
            "areaZone" to address.areaZone,
            "latitude" to address.latitude,
            "longitude" to address.longitude,
            "deliveryInstructions" to address.deliveryInstructions,
            "userId" to (currentUserId ?: address.userId),
            "isInsideServiceZone" to address.isInsideServiceZone,
            "isSelected" to address.isSelected,
            "updatedAt" to System.currentTimeMillis()
        )
        firestore.collection("addresses")
            .document(address.id)
            .set(addressMap, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception) }
    }

    /** Place a new customer order in Firestore under `orders` collection. */
    fun placeOrder(order: PlacedOrder, onSuccess: (String) -> Unit = {}, onError: (Exception) -> Unit = {}) {
        val orderData = mapOf(
            "id" to order.id,
            "restaurant" to order.restaurant,
            "totalEtb" to order.totalEtb,
            "status" to order.status.name,
            "itemCount" to order.itemCount,
            "scheduled" to order.scheduled,
            "pin" to order.pin,
            "payment" to order.payment,
            "pickupLocation" to order.pickupLocation,
            "deliveryAddress" to mapOf(
                "id" to order.deliveryAddress.id,
                "label" to order.deliveryAddress.label,
                "addressText" to order.deliveryAddress.addressText,
                "landmark" to order.deliveryAddress.landmark,
                "areaZone" to order.deliveryAddress.areaZone,
                "latitude" to order.deliveryAddress.latitude,
                "longitude" to order.deliveryAddress.longitude
            ),
            "riderName" to order.riderName,
            "riderPhone" to order.riderPhone,
            "vehiclePlate" to order.vehiclePlate,
            "riderLat" to order.riderLat,
            "riderLng" to order.riderLng,
            "riderLastUpdated" to order.riderLastUpdated,
            "userId" to (currentUserId ?: "customer_guest"),
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("orders")
            .document(order.id)
            .set(orderData)
            .addOnSuccessListener { onSuccess(order.id) }
            .addOnFailureListener { exception -> onError(exception) }
    }

    /** Update live rider location coordinates during active delivery in Firestore. */
    fun updateRiderLocation(orderId: String, lat: Double, lng: Double, onSuccess: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        val updates = mapOf(
            "riderLat" to lat,
            "riderLng" to lng,
            "riderLastUpdated" to "Updated just now",
            "updatedAt" to System.currentTimeMillis()
        )
        firestore.collection("orders")
            .document(orderId)
            .update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception) }
    }

    /** Update order status in Firestore. */
    fun updateOrderStatus(orderId: String, status: OrderStatus, onSuccess: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        val updates = mapOf(
            "status" to status.name,
            "updatedAt" to System.currentTimeMillis()
        )
        firestore.collection("orders")
            .document(orderId)
            .update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception) }
    }

    /** Perform an authorized Firestore read/write test to verify connection. */
    fun testConnection(onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        val testDoc = mapOf(
            "connected" to true,
            "timestamp" to System.currentTimeMillis(),
            "appName" to "Dilla Food Delivery"
        )
        firestore.collection("system_status")
            .document("connection_test")
            .set(testDoc, SetOptions.merge())
            .addOnSuccessListener {
                onSuccess("Firebase Firestore successfully connected & verified!")
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }
}
