package com.dilla.food.data

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlin.random.Random

// Exact Colors from Stitch "Warm Campus Dining" Design System
val PrimaryColor = Color(0xFFAD292A)      // Vibrant Coral / Crimson (#AD292A)
val PrimaryContainer = Color(0xFFD04240) // Container Coral
val SecondaryColor = Color(0xFFFEA53C)   // Warm Honey Amber (#FEA53C)
val TertiaryColor = Color(0xFF186A22)    // Natural Harvest Green (#186A22) - Fasting/Beyaynetu/Open
val SurfaceBg = Color(0xFFFCF9F8)        // Base Canvas (#FCF9F8)
val SurfaceContainer = Color(0xFFF0EDED) // Low Tonal Surface (#F0EDED)
val InkColor = Color(0xFF1B1C1C)         // High Emphasis Charcoal (#1B1C1C)
val GrayColor = Color(0xFF666666)        // Medium Emphasis (#666666)
val OutlineVariant = Color(0xFFE1BFBB)   // Outline Variant (#E1BFBB)

// Backward compatibility alias constants
val Coral = PrimaryColor
val SoftBg = SurfaceBg
val Ink = InkColor
val Gray = GrayColor

enum class Role(val label: String) { Customer("Customer"), Restaurant("Restaurant"), Rider("Rider"), Admin("Admin") }

data class Person(
    val id: String,
    val name: String,
    val color: Color,
    val note: String = "Friend",
    val statusText: String = "Added"
)

data class FoodCategory(
    val id: String,
    val name: String,
    val emoji: String = "🍲",
    val isGlobal: Boolean = true,
    val isActive: Boolean = true,
    val restaurantId: String = ""
)

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val priceEtb: Int,
    val category: String,
    val emoji: String,
    val isFasting: Boolean = false,
    val isHalal: Boolean = true,
    val prepTimeMin: Int = 15,
    val restaurantId: String = "r1",
    val restaurantName: String = "Lucy Traditional Kitchen",
    val photoUri: String = "",
    val imageList: List<String> = emptyList(), // Up to 7 photos per food item
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class CartLine(val personId: String, val itemId: String, val qty: Int)

data class Restaurant(
    val id: String,
    val name: String,
    val cuisine: String,
    val etaMin: Int,
    val feeEtb: Int,
    val open: Boolean,
    val emoji: String,
    val distanceKm: Double = 0.8,
    val isBayenetuFriendly: Boolean = true,
    val rating: Double = 4.8,
    val address: String = "Piazza Center, Dilla Town",
    val lat: Double = 6.4085,
    val lng: Double = 38.3105,
    val operatingHours: String = "08:00 AM - 10:00 PM",
    val phone: String = "+251 91 111 2233",
    val isApprovedByAdmin: Boolean = true,
    val licenseSubmitted: Boolean = true
)

enum class OrderStatus(val label: String) {
    Pending("Order Placed"),
    Accepted("Order Confirmed"),
    Preparing("Prepared & Packed"),
    Ready("Rider Picked Up"),
    InTransit("In Transit to Customer"),
    Delivered("Delivered & PIN Confirmed"),
    Rejected("Rejected")
}

data class DeliveryAddress(
    val id: String,
    val label: String, // "Work / Office", "Home", "Shop", "Dormitory", "Other"
    val addressText: String, // e.g. "CBE Building Office 304, Piazza"
    val landmark: String, // e.g. "Opposite Total Energy Gas Station"
    val areaZone: String, // e.g. "Kebele 01 (Dilla Town Center)"
    val latitude: Double = 6.4083,
    val longitude: Double = 38.3102,
    val deliveryInstructions: String = "",
    val userId: String = "user_me",
    val isInsideServiceZone: Boolean = true,
    val isSelected: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class PlacedOrder(
    val id: String,
    val restaurant: String,
    val totalEtb: Int,
    val status: OrderStatus,
    val itemCount: Int,
    val scheduled: String?,
    val pin: String,
    val payment: String,
    val deliveryAddress: DeliveryAddress,
    val pickupLocation: String = "Lucy Traditional Kitchen, Piazza",
    val riderName: String = "Amanuel Girma",
    val riderPhone: String = "+251 91 234 5678",
    val vehiclePlate: String = "ET-3-4819 (TVS Motor)",
    val riderLat: Double = 6.4095,
    val riderLng: Double = 38.3110,
    val riderLastUpdated: String = "Updated 30s ago",
    val rejectionReason: String = ""
)

data class Share(val person: Person, val subtotal: Int, val fees: Int) { val due get() = subtotal + fees }

/* ---------- Phase 3: Notification Data Model ---------- */
data class NotificationModel(
    val id: String,
    val userId: String = "user_me",
    val role: Role = Role.Customer,
    val title: String,
    val message: String,
    val timestamp: String = "Just Now",
    val isRead: Boolean = false,
    val orderId: String? = null,
    val restaurantId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/* ---------- Phase 6: Admin Data Models ---------- */
data class CustomerUser(
    val id: String,
    val name: String,
    val phone: String,
    val email: String = "",
    val registeredDate: String = "Oct 2026",
    val isActive: Boolean = true,
    val totalOrders: Int = 3,
    val defaultAddress: String = "Kebele 01 Piazza, Dilla",
    val customFieldsData: Map<String, String> = emptyMap()
)

data class RestaurantApplication(
    val id: String,
    val name: String,
    val ownerName: String,
    val cuisine: String,
    val phone: String,
    val address: String,
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val rejectionReason: String = "",
    val submittedAt: String = "Today, 10:15 AM",
    val docUrl: String = "gedeo_trade_license_2026.pdf",
    val licenseSubmitted: Boolean = true,
    val customFieldsData: Map<String, String> = emptyMap()
)

data class RiderApplication(
    val id: String,
    val name: String,
    val phone: String,
    val vehicleType: String = "TVS Motorcycle",
    val plateNumber: String = "ET-3-4819",
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val rejectionReason: String = "",
    val submittedAt: String = "Today, 09:30 AM",
    val licenseDocUrl: String = "rider_license.pdf",
    val isOnline: Boolean = true,
    val totalEarningsEtb: Int = 1250,
    val completedDeliveries: Int = 18
)

data class ServiceZone(
    val id: String,
    val name: String,
    val areaDescription: String,
    val baseFeeEtb: Int,
    val minOrderEtb: Int,
    val isActive: Boolean = true
)

data class AuditLog(
    val id: String,
    val adminId: String = "admin_01",
    val action: String,
    val targetRecord: String,
    val timestamp: String,
    val details: String
)

data class AdminNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val type: String = "Application"
)

/** Service Area & Delivery Geofence Config for Dilla Town and Surrounding Areas. */
object DeliveryZoneConfig {
    const val centerLat = 6.4083
    const val centerLng = 38.3102
    const val maxServiceRadiusKm = 8.0 // 8km service radius covering all Dilla Town Kebeles & Campus

    val supportedZones = listOf(
        "Kebele 01 (Dilla Town Center / Piazza)",
        "Kebele 02 (Market & Residential)",
        "Kebele 03 (Oda Roba Sector)",
        "Hospital Quarter (Dilla Referral Hospital)",
        "Bus Terminal Hub (Kebele 01)",
        "Dilla University Main Campus (Oda Hall / Dorms)",
        "Dilla University Gate 2 & Campus Area"
    )

    fun isCoordinatesInZone(lat: Double, lng: Double): Boolean {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat - centerLat)
        val dLng = Math.toRadians(lng - centerLng)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(centerLat)) * Math.cos(Math.toRadians(lat)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return (earthRadiusKm * c) <= maxServiceRadiusKm
    }
}

/** Authentic Stitch-styled Dilla Food Delivery dataset. */
object Demo {
    val defaultAddresses = listOf(
        DeliveryAddress(
            id = "addr_1",
            label = "Work / Office",
            addressText = "CBE Building Office 304, Piazza, Dilla",
            landmark = "Opposite Total Energy Gas Station",
            areaZone = "Kebele 01 (Dilla Town Center / Piazza)",
            latitude = 6.4083,
            longitude = 38.3102,
            deliveryInstructions = "3rd floor office reception. Call upon arrival at security gate.",
            isSelected = true
        ),
        DeliveryAddress(
            id = "addr_2",
            label = "Home",
            addressText = "House #412, Kebele 02 Residence, Dilla",
            landmark = "Near St. Michael Church & Commercial Bank Branch",
            areaZone = "Kebele 02 (Market & Residential)",
            latitude = 6.4120,
            longitude = 38.3140,
            deliveryInstructions = "Blue gate opposite the bakery.",
            isSelected = false
        ),
        DeliveryAddress(
            id = "addr_3",
            label = "Dormitory",
            addressText = "Dilla University Main Campus · Block 14 Dorm Lounge",
            landmark = "Near Central Library Quad & Cafeteria Gate",
            areaZone = "Dilla University Main Campus (Oda Hall / Dorms)",
            latitude = 6.4250,
            longitude = 38.3210,
            deliveryInstructions = "Handover at Block 14 security point.",
            isSelected = false
        ),
        DeliveryAddress(
            id = "addr_4",
            label = "Shop",
            addressText = "Shop #12, Dilla Central Bus Terminal, Kebele 01",
            landmark = "Next to Bank of Abyssinia",
            areaZone = "Bus Terminal Hub (Kebele 01)",
            latitude = 6.4050,
            longitude = 38.3080,
            deliveryInstructions = "Ground floor electronic shop.",
            isSelected = false
        )
    )

    val friends = listOf(
        Person("u1", "Kidus", PrimaryColor, "Host · Dilla Town", "Host"),
        Person("u2", "Selam T.", Color(0xFF5C6BC0), "Kebele 02 · Fasting", "Added"),
        Person("u3", "Henok B.", Color(0xFF26A69A), "Piazza · Architecture", "Added"),
        Person("u4", "Marta T.", Color(0xFFEC407A), "Oda Roba Rep", "Ordering"),
        Person("u5", "Daniel D.", Color(0xFF7E57C2), "Kebele 02 Colleague", "Ready"),
        Person("u6", "Jackie M.", Color(0xFFFFA726), "Main Campus", "Added")
    )

    val globalCategories = listOf(
        FoodCategory("gc1", "Fasting Food", "🍲"),
        FoodCategory("gc2", "Low Prices", "🏷️"),
        FoodCategory("gc3", "Drinks", "🍹"),
        FoodCategory("gc4", "Lunch", "🍱"),
        FoodCategory("gc5", "Dinner", "🍲"),
        FoodCategory("gc6", "Breakfast", "🍳"),
        FoodCategory("gc7", "Cakes", "🍰"),
        FoodCategory("gc8", "Pizza", "🍕"),
        FoodCategory("gc9", "Burgers", "🍔"),
        FoodCategory("gc10", "Traditional Food", "🥩"),
        FoodCategory("gc11", "Fast Food", "🍟"),
        FoodCategory("gc12", "Halal Food", "🌙")
    )

    val restaurants = listOf(
        Restaurant("r1", "Lucy Traditional Kitchen", "Traditional · Tibs & Shiro", 25, 30, true, "🍲", 0.8, true, 4.9, "Piazza Center, Kebele 01", 6.4085, 38.3105),
        Restaurant("r2", "Green Paradise Garden", "Ethiopian · Agelgil & Salads", 30, 40, true, "🥗", 1.2, true, 4.8, "Kebele 02 Garden Quarter", 6.4110, 38.3130),
        Restaurant("r3", "Dilla Burger & Grill", "Burgers · Fast Food", 20, 35, true, "🍔", 0.5, false, 4.7, "Main Street, Piazza", 6.4075, 38.3090),
        Restaurant("r4", "Campus Central Cafe", "Juice · Coffee · Pastry", 15, 20, true, "☕", 0.3, true, 4.6, "University Gate 2 Road", 6.4220, 38.3180),
        Restaurant("r5", "Sidama Pizza & Pasta", "Pizza · Italian", 40, 50, false, "🍕", 1.5, false, 4.5, "Referral Hospital Road", 6.4150, 38.3160)
    )

    val menu = listOf(
        MenuItem("m1", "Special Shekla Tibs", "Sizzling beef tibs served on a traditional clay pot with fresh green chili & injera.", 380, "Traditional Food", "🥩", false, true, 20, "r1", "Lucy Traditional Kitchen", "https://images.unsplash.com/photo-1544025162-d76694265947", listOf("https://images.unsplash.com/photo-1544025162-d76694265947")),
        MenuItem("m2", "Fasting Beyaynetu", "Authentic Ethiopian platter with shiro, misir wot, kik alicha, gomen and beet salad.", 220, "Fasting Food", "🍲", true, true, 15, "r1", "Lucy Traditional Kitchen", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd", listOf("https://images.unsplash.com/photo-1512621776951-a57141f2eefd")),
        MenuItem("m3", "Special Shiro Tegabino", "Rich bubbling chickpea stew in clay pot served with hot Injera.", 180, "Fasting Food", "🥘", true, true, 12, "r1", "Lucy Traditional Kitchen"),
        MenuItem("m4", "Special Agelgil Family Combo", "Traditional leather-wrapped basket pack with doro wot, key wot, shiro & 4 boiled eggs.", 750, "Traditional Food", "🧺", false, true, 25, "r2", "Green Paradise Garden"),
        MenuItem("m5", "Avocado-Papaya Spris Juice", "Fresh layered Dilla fruit smoothie with lime wedge.", 90, "Drinks", "🥑", true, true, 8, "r4", "Campus Central Cafe"),
        MenuItem("m6", "Cheeky Chick'N Burger", "Crispy spiced chicken fillet, lettuce, tomato and signature house sauce.", 240, "Burgers", "🍔", false, true, 15, "r3", "Dilla Burger & Grill"),
        MenuItem("m7", "Double Cheese Burger Combo", "Two beef patties, melted cheese, seasoned fries and cold soda.", 340, "Burgers", "🍟", false, true, 15, "r3", "Dilla Burger & Grill"),
        MenuItem("m8", "Chechebsa with Honey", "Shredded flatbread cooked in spiced kibe butter, topped with honey and fresh yogurt.", 190, "Breakfast", "🍳", false, true, 12, "r4", "Campus Central Cafe"),
        MenuItem("m9", "Sparkling Ambo Water", "500ml glass bottle.", 40, "Drinks", "🍾", true, true, 2, "r4", "Campus Central Cafe")
    )

    val initialCustomers = listOf(
        CustomerUser("c1", "Kidus Tefera", "+251 91 234 5678", "kidus@dilla.edu.et", "Oct 12, 2026", true, 5, "Kebele 01 Piazza, Dilla"),
        CustomerUser("c2", "Selam Tadesse", "+251 92 111 2233", "selam@dilla.edu.et", "Oct 15, 2026", true, 3, "Kebele 02 Residence"),
        CustomerUser("c3", "Henok Berhanu", "+251 93 444 5566", "henok@gmail.com", "Oct 18, 2026", true, 8, "Dilla University Gate 2"),
        CustomerUser("c4", "Marta Tefera", "+251 94 777 8899", "marta@dilla.edu.et", "Oct 20, 2026", true, 2, "Oda Roba District"),
        CustomerUser("c5", "Dr. Daniel Desta", "+251 95 000 1122", "daniel.desta@dilla.edu.et", "Oct 22, 2026", false, 1, "Hospital Quarter")
    )

    val initialRestaurantApps = listOf(
        RestaurantApplication("ra1", "Abyssinia Traditional House", "Abebe Bikila", "Ethiopian · Tibs", "+251 91 555 1234", "Piazza Main Street, Kebele 01", "Pending", "", "Today, 10:15 AM", "gedeo_trade_license_2026.pdf", true, mapOf("Trade License Number" to "TL-88421", "Tax ID (TIN)" to "TIN-908122")),
        RestaurantApplication("ra2", "Gedeo Coffee & Pastry", "Tigist Alemu", "Cafeteria · Coffee", "+251 92 666 2345", "Referral Hospital Gate", "Approved", "", "Yesterday, 02:30 PM", "health_permit_2026.pdf", true, mapOf("Trade License Number" to "TL-11029", "Tax ID (TIN)" to "TIN-445100"))
    )

    val initialRiderApps = listOf(
        RiderApplication("ri1", "Solomon Worku", "+251 91 888 9900", "TVS Motorcycle", "ET-3-9912", "Pending", "", "Today, 09:30 AM"),
        RiderApplication("ri2", "Amanuel Girma", "+251 91 234 5678", "TVS Motorcycle", "ET-3-4819", "Approved", "", "Oct 10, 2026", isOnline = true, totalEarningsEtb = 1450, completedDeliveries = 22)
    )

    val initialServiceZones = listOf(
        ServiceZone("z1", "Kebele 01 (Piazza Town Center)", "Central commercial hub & Piazza market", 30, 100, true),
        ServiceZone("z2", "Kebele 02 (Market & Residential)", "Residential kebeles & local market", 35, 120, true),
        ServiceZone("z3", "Kebele 03 (Oda Roba Sector)", "Outer residential quarter", 40, 150, true),
        ServiceZone("z4", "Hospital Quarter", "Referral hospital & clinic zone", 35, 100, true),
        ServiceZone("z5", "Dilla University Main Campus", "Main campus, Oda Hall & dorm blocks", 30, 80, true)
    )

    val initialAuditLogs = listOf(
        AuditLog("log1", "admin_01", "RIDER_APPROVED", "Amanuel Girma (ET-3-4819)", "Oct 25, 2026 11:20 AM", "Approved verification document and driver license."),
        AuditLog("log2", "admin_01", "ZONE_FEE_UPDATED", "Kebele 01 Piazza", "Oct 26, 2026 09:00 AM", "Updated delivery base fee to 30 ETB.")
    )

    val initialNotifications = listOf(
        AdminNotification("an1", "New Restaurant Application", "Abyssinia Traditional House submitted registration documents.", "10 mins ago", false, "Application"),
        AdminNotification("an2", "New Rider Registration", "Solomon Worku applied for courier account.", "45 mins ago", false, "Rider")
    )
}

class AppVm : ViewModel() {
    var role by mutableStateOf<Role?>(null)
    var userName by mutableStateOf("Kidus (Dilla Resident)")

    // Notification System State
    val notifications = mutableStateListOf<NotificationModel>().apply {
        add(
            NotificationModel(
                id = "notif_1",
                title = "Order in Transit 🛵",
                message = "Rider Amanuel Girma is on the way with your order #DF-88219.",
                timestamp = "5 mins ago",
                isRead = false,
                orderId = "DF-88219"
            )
        )
    }

    fun sendNotification(
        title: String,
        message: String,
        role: Role = Role.Customer,
        orderId: String? = null,
        restaurantId: String? = null
    ) {
        val newNotif = NotificationModel(
            id = "notif_" + System.currentTimeMillis(),
            userId = "user_me",
            role = role,
            title = title,
            message = message,
            timestamp = "Just Now",
            isRead = false,
            orderId = orderId,
            restaurantId = restaurantId
        )
        notifications.add(0, newNotif)
    }

    fun markNotificationAsRead(id: String) {
        val idx = notifications.indexOfFirst { it.id == id }
        if (idx >= 0) {
            notifications[idx] = notifications[idx].copy(isRead = true)
        }
    }

    fun markAllNotificationsAsRead() {
        notifications.indices.forEach { i ->
            notifications[i] = notifications[i].copy(isRead = true)
        }
    }

    // Food Category State Management (Global & Restaurant-created)
    val globalFoodCategories = mutableStateListOf<FoodCategory>().apply { addAll(Demo.globalCategories) }
    val restaurantCustomCategories = mutableStateListOf<FoodCategory>()

    fun addGlobalCategory(category: FoodCategory) {
        if (category.name.isNotBlank() && globalFoodCategories.none { it.name.equals(category.name, ignoreCase = true) }) {
            globalFoodCategories.add(0, category)
            addAuditLog("GLOBAL_CATEGORY_ADDED", category.name, "Added new global food category")
        }
    }

    fun toggleGlobalCategoryActive(id: String) {
        val idx = globalFoodCategories.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val cat = globalFoodCategories[idx]
            globalFoodCategories[idx] = cat.copy(isActive = !cat.isActive)
        }
    }

    fun addRestaurantCategory(name: String, emoji: String = "🍲") {
        if (name.isNotBlank() && restaurantCustomCategories.none { it.name.equals(name, ignoreCase = true) }) {
            val newCat = FoodCategory(
                id = "rc_" + System.currentTimeMillis(),
                name = name.trim(),
                emoji = emoji,
                isGlobal = false,
                isActive = true,
                restaurantId = restaurant.id
            )
            restaurantCustomCategories.add(0, newCat)
        }
    }

    fun deleteRestaurantCategory(id: String) {
        restaurantCustomCategories.removeAll { it.id == id }
    }

    // Dynamic Custom Fields configured by Admin in Settings
    val customUserFields = mutableStateListOf<String>("Emergency Phone Number", "Preferred Delivery Slot")
    val customRestaurantFields = mutableStateListOf<String>("Trade License Number", "Tax Identification Number (TIN)", "Health & Safety Permit Code")

    fun addCustomUserField(fieldName: String) {
        if (fieldName.isNotBlank() && fieldName !in customUserFields) {
            customUserFields.add(fieldName.trim())
        }
    }

    fun removeCustomUserField(fieldName: String) {
        customUserFields.remove(fieldName)
    }

    fun addCustomRestaurantField(fieldName: String) {
        if (fieldName.isNotBlank() && fieldName !in customRestaurantFields) {
            customRestaurantFields.add(fieldName.trim())
        }
    }

    fun removeCustomRestaurantField(fieldName: String) {
        customRestaurantFields.remove(fieldName)
    }

    // Customer Favorites State Management
    val favoriteRestaurants = mutableStateListOf<String>().apply { add("r1"); add("r2") }

    fun toggleFavorite(restaurantId: String) {
        if (restaurantId in favoriteRestaurants) {
            favoriteRestaurants.remove(restaurantId)
        } else {
            favoriteRestaurants.add(restaurantId)
        }
    }

    // Customer Address State Management
    val savedAddresses = mutableStateListOf<DeliveryAddress>().apply { addAll(Demo.defaultAddresses) }
    var selectedAddressId by mutableStateOf("addr_1")

    val selectedAddress: DeliveryAddress
        get() = savedAddresses.firstOrNull { it.id == selectedAddressId }
            ?: savedAddresses.firstOrNull()
            ?: Demo.defaultAddresses.first()

    fun selectAddress(id: String) {
        val target = savedAddresses.firstOrNull { it.id == id }
        if (target != null && target.isInsideServiceZone) {
            selectedAddressId = id
            savedAddresses.indices.forEach { i ->
                savedAddresses[i] = savedAddresses[i].copy(isSelected = savedAddresses[i].id == id)
            }
        }
    }

    fun addAddress(address: DeliveryAddress) {
        val inZone = DeliveryZoneConfig.isCoordinatesInZone(address.latitude, address.longitude)
        val newAddr = address.copy(isInsideServiceZone = inZone, isSelected = true)
        savedAddresses.add(0, newAddr)
        selectedAddressId = newAddr.id
        savedAddresses.indices.forEach { i ->
            savedAddresses[i] = savedAddresses[i].copy(isSelected = savedAddresses[i].id == newAddr.id)
        }
    }

    fun updateAddress(address: DeliveryAddress) {
        val idx = savedAddresses.indexOfFirst { it.id == address.id }
        if (idx >= 0) {
            val inZone = DeliveryZoneConfig.isCoordinatesInZone(address.latitude, address.longitude)
            savedAddresses[idx] = address.copy(isInsideServiceZone = inZone)
        }
    }

    fun deleteAddress(id: String) {
        savedAddresses.removeAll { it.id == id }
        if (selectedAddressId == id && savedAddresses.isNotEmpty()) {
            selectAddress(savedAddresses.first().id)
        }
    }

    /* ---------- Phase 4: Restaurant Management State ---------- */
    val restaurantMenuList = mutableStateListOf<MenuItem>().apply { addAll(Demo.menu) }

    fun addMenuItem(item: MenuItem) {
        restaurantMenuList.add(0, item)
    }

    fun updateMenuItem(item: MenuItem) {
        val idx = restaurantMenuList.indexOfFirst { it.id == item.id }
        if (idx >= 0) {
            restaurantMenuList[idx] = item
        }
    }

    fun deleteMenuItem(itemId: String) {
        restaurantMenuList.removeAll { it.id == itemId }
    }

    fun toggleRestaurantOpenStatus() {
        restaurant = restaurant.copy(open = !restaurant.open)
    }

    fun rejectOrderWithReason(orderId: String, reason: String) {
        val idx = orders.indexOfFirst { it.id == orderId }
        if (idx >= 0) {
            orders[idx] = orders[idx].copy(status = OrderStatus.Rejected, rejectionReason = reason)
        }
    }

    /* ---------- Phase 6: Admin Management State ---------- */
    val customers = mutableStateListOf<CustomerUser>().apply { addAll(Demo.initialCustomers) }
    val restaurantApplications = mutableStateListOf<RestaurantApplication>().apply { addAll(Demo.initialRestaurantApps) }
    val riderApplications = mutableStateListOf<RiderApplication>().apply { addAll(Demo.initialRiderApps) }
    val serviceZones = mutableStateListOf<ServiceZone>().apply { addAll(Demo.initialServiceZones) }
    val auditLogs = mutableStateListOf<AuditLog>().apply { addAll(Demo.initialAuditLogs) }
    val adminNotifications = mutableStateListOf<AdminNotification>().apply { addAll(Demo.initialNotifications) }

    fun approveRestaurantApplication(id: String) {
        val idx = restaurantApplications.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val app = restaurantApplications[idx]
            restaurantApplications[idx] = app.copy(status = "Approved")
            addAuditLog("RESTAURANT_APPROVED", app.name, "Approved vendor application for ${app.ownerName}")

            // Trigger New Restaurant Announcement Notification to Customers
            sendNotification(
                title = "New Restaurant Available! 🍲",
                message = "A new restaurant has joined Dilla Food Delivery! Visit ${app.name} and explore its menu. You can now place your order.",
                role = Role.Customer,
                orderId = null,
                restaurantId = app.id
            )
        }
    }

    fun rejectRestaurantApplication(id: String, reason: String) {
        val idx = restaurantApplications.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val app = restaurantApplications[idx]
            restaurantApplications[idx] = app.copy(status = "Rejected", rejectionReason = reason)
            addAuditLog("RESTAURANT_REJECTED", app.name, "Reason: $reason")
        }
    }

    fun approveRiderApplication(id: String) {
        val idx = riderApplications.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val app = riderApplications[idx]
            riderApplications[idx] = app.copy(status = "Approved")
            addAuditLog("RIDER_APPROVED", app.name, "Approved courier account for ${app.plateNumber}")
        }
    }

    fun rejectRiderApplication(id: String, reason: String) {
        val idx = riderApplications.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val app = riderApplications[idx]
            riderApplications[idx] = app.copy(status = "Rejected", rejectionReason = reason)
            addAuditLog("RIDER_REJECTED", app.name, "Reason: $reason")
        }
    }

    fun toggleCustomerActive(id: String) {
        val idx = customers.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val c = customers[idx]
            val newStatus = !c.isActive
            customers[idx] = c.copy(isActive = newStatus)
            addAuditLog(if (newStatus) "CUSTOMER_ACTIVATED" else "CUSTOMER_SUSPENDED", c.name, "Status changed to active=$newStatus")
        }
    }

    fun toggleRiderActive(id: String) {
        val idx = riderApplications.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val r = riderApplications[idx]
            val newOnline = !r.isOnline
            riderApplications[idx] = r.copy(isOnline = newOnline)
            addAuditLog("RIDER_AVAILABILITY_CHANGED", r.name, "Online status set to $newOnline")
        }
    }

    fun toggleServiceZoneActive(id: String) {
        val idx = serviceZones.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val z = serviceZones[idx]
            val newActive = !z.isActive
            serviceZones[idx] = z.copy(isActive = newActive)
            addAuditLog("ZONE_STATUS_CHANGED", z.name, "Service zone active=$newActive")
        }
    }

    fun addServiceZone(zone: ServiceZone) {
        serviceZones.add(0, zone)
        addAuditLog("ZONE_CREATED", zone.name, "Base fee: ${zone.baseFeeEtb} ETB")
    }

    fun cancelOrder(orderId: String) {
        setStatus(orderId, OrderStatus.Rejected)
        addAuditLog("ORDER_CANCELLED_BY_ADMIN", orderId, "Order cancelled by administrator")
    }

    fun addAuditLog(action: String, targetRecord: String, details: String) {
        val newLog = AuditLog(
            id = "log_${System.currentTimeMillis()}",
            adminId = "admin_01",
            action = action,
            targetRecord = targetRecord,
            timestamp = "Just Now",
            details = details
        )
        auditLogs.add(0, newLog)
    }

    val me = Person("me", "Kidus (You)", PrimaryColor, "Host", "Host")
    val people = mutableStateListOf<Person>().apply { addAll(Demo.friends.drop(1)) }
    val selected = mutableStateListOf<String>().apply { addAll(Demo.friends.drop(1).map { it.id }) }
    var groupName by mutableStateOf("Dilla Group Lunch")
    var groupInviteCode by mutableStateOf("DLA-9482")
    var groupMode by mutableStateOf(true)
    var deliveryModeTab by mutableStateOf("Individual") // "Individual", "Group Order", "Family Feasts", "Office Lunch"
    var activeCategoryFilter by mutableStateOf("All Feeds")
    var activeDietaryFilter by mutableStateOf("All Cuisines")
    var activeSortOption by mutableStateOf("Lowest Price") // "Lowest Price", "Highest Price", "Newest", "Popularity"
    var restaurant by mutableStateOf(Demo.restaurants.first())
    var activeId by mutableStateOf("me")

    val menu = Demo.menu
    val unavailable = mutableStateListOf<String>()
    val cart = mutableStateListOf<CartLine>()
    var splitOn by mutableStateOf(true)
    var scheduledFor by mutableStateOf<String?>(null)
    val orders = mutableStateListOf<PlacedOrder>().apply {
        add(
            PlacedOrder(
                id = "DF-88219",
                restaurant = "Lucy Traditional Kitchen",
                totalEtb = 680,
                status = OrderStatus.InTransit,
                itemCount = 4,
                scheduled = "In 15 min",
                pin = "4819",
                payment = "Cash on delivery (split)",
                deliveryAddress = Demo.defaultAddresses.first(),
                pickupLocation = "Lucy Traditional Kitchen, Piazza Center",
                riderName = "Amanuel Girma",
                riderPhone = "+251 91 234 5678",
                vehiclePlate = "ET-3-4819 (TVS Motor)",
                riderLat = 6.4095,
                riderLng = 38.3110,
                riderLastUpdated = "Updated 30s ago"
            )
        )
    }
    private var counter = 88220

    val participants: List<Person>
        get() = if (groupMode) listOf(me) + people.filter { it.id in selected } else listOf(me)

    fun person(id: String) = participants.firstOrNull { it.id == id } ?: me
    fun toggle(id: String) { if (id in selected) selected.remove(id) else selected.add(id) }
    fun addFriend(name: String) {
        val colors = listOf(0xFF8E24AA, 0xFF00897B, 0xFFF4511E, 0xFF3949AB)
        val p = Person("c${people.size + 1}", name.trim(), Color(colors[people.size % colors.size]), "Friend", "Added")
        people.add(p); selected.add(p.id)
    }

    fun startOrder(group: Boolean) { groupMode = group; activeId = "me"; cart.clear(); splitOn = group; scheduledFor = null }

    fun price(itemId: String) = menu.first { it.id == itemId }.priceEtb
    fun qtyOf(pid: String, itemId: String) = cart.firstOrNull { it.personId == pid && it.itemId == itemId }?.qty ?: 0
    fun add(pid: String, itemId: String) {
        val i = cart.indexOfFirst { it.personId == pid && it.itemId == itemId }
        if (i >= 0) cart[i] = cart[i].copy(qty = cart[i].qty + 1) else cart.add(CartLine(pid, itemId, 1))
    }
    fun remove(pid: String, itemId: String) {
        val i = cart.indexOfFirst { it.personId == pid && it.itemId == itemId }
        if (i < 0) return
        if (cart[i].qty > 1) cart[i] = cart[i].copy(qty = cart[i].qty - 1) else cart.removeAt(i)
    }
    fun cartCount() = cart.sumOf { it.qty }
    fun subtotal(pid: String) = cart.filter { it.personId == pid }.sumOf { price(it.itemId) * it.qty }
    val serviceFee = 10
    fun feesTotal() = if (cart.isEmpty()) 0 else restaurant.feeEtb + serviceFee
    fun grandTotal() = cart.sumOf { price(it.itemId) * it.qty } + feesTotal()

    /** Reorder an existing order: populates cart with items and sets restaurant. */
    fun reorder(orderId: String) {
        val o = order(orderId) ?: return
        val r = Demo.restaurants.firstOrNull { it.name == o.restaurant } ?: Demo.restaurants.first()
        restaurant = r
        cart.clear()
        selectedAddressId = o.deliveryAddress.id
        val defaultItem = menu.firstOrNull { it.category == "Traditional Food" || it.category == "Fast Food" } ?: menu.first()
        add("me", defaultItem.id)
    }

    /** Fees split equally between participants with items; remainder goes to the earliest participants. */
    fun shares(): List<Share> {
        val active = participants.filter { subtotal(it.id) > 0 }
        if (active.isEmpty()) return emptyList()
        val per = feesTotal() / active.size
        val rem = feesTotal() % active.size
        return active.mapIndexed { i, p -> Share(p, subtotal(p.id), per + if (i < rem) 1 else 0) }
    }

    fun placeOrder(payment: String): String {
        counter += 1
        val id = "DF-$counter"
        val newOrder = PlacedOrder(
            id = id,
            restaurant = restaurant.name,
            totalEtb = grandTotal(),
            status = OrderStatus.Pending,
            itemCount = cartCount(),
            scheduled = scheduledFor,
            pin = Random.nextInt(1000, 9999).toString(),
            payment = payment,
            deliveryAddress = selectedAddress,
            pickupLocation = "${restaurant.name}, ${restaurant.address}",
            riderName = "Amanuel Girma",
            riderPhone = "+251 91 234 5678",
            vehiclePlate = "ET-3-4819 (TVS Motor)",
            riderLat = restaurant.lat,
            riderLng = restaurant.lng,
            riderLastUpdated = "Rider Assigned"
        )
        orders.add(0, newOrder)
        cart.clear(); activeId = "me"

        // Trigger Order Placed Notification
        sendNotification(
            title = "Order Placed Successfully! 🛒",
            message = "Your order #$id has been received by ${restaurant.name}.",
            role = Role.Customer,
            orderId = id,
            restaurantId = null
        )

        return id
    }

    fun order(id: String) = orders.firstOrNull { it.id == id }

    fun setStatus(id: String, s: OrderStatus) {
        val i = orders.indexOfFirst { it.id == id }
        if (i >= 0) {
            val oldStatus = orders[i].status
            orders[i] = orders[i].copy(status = s)
            if (oldStatus != s) {
                val title = when (s) {
                    OrderStatus.Accepted -> "Order Confirmed! 👨‍🍳"
                    OrderStatus.Preparing -> "Kitchen is Preparing Food 🍳"
                    OrderStatus.Ready -> "Food Ready for Pickup 🛍️"
                    OrderStatus.InTransit -> "Rider in Transit 🛵"
                    OrderStatus.Delivered -> "Order Delivered! 🎉"
                    OrderStatus.Rejected -> "Order Cancelled ⚠️"
                    else -> "Order Update #${id}"
                }
                val msg = when (s) {
                    OrderStatus.Accepted -> "Your order #${id} has been accepted by ${orders[i].restaurant}."
                    OrderStatus.Preparing -> "The kitchen is actively preparing your dishes."
                    OrderStatus.Ready -> "Your meal is packed and ready for rider pickup."
                    OrderStatus.InTransit -> "Rider ${orders[i].riderName} is on the way to ${orders[i].deliveryAddress.label}."
                    OrderStatus.Delivered -> "Your order #${id} was delivered & PIN confirmed."
                    OrderStatus.Rejected -> "Order #${id} was cancelled or rejected."
                    else -> "Order #${id} status changed to ${s.label}."
                }
                sendNotification(
                    title = title,
                    message = msg,
                    role = Role.Customer,
                    orderId = id,
                    restaurantId = null
                )
            }
        }
    }
}
