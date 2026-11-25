package com.dilla.food.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.dilla.food.data.*

@Composable
fun CustomerShell(
    vm: AppVm,
    onPickRestaurant: () -> Unit,
    onOpenOrder: (String) -> Unit,
    onOpenProfile: () -> Unit = {},
    onOpenChangePass: () -> Unit = {},
    onOpenReports: () -> Unit = {},
    onSignOut: () -> Unit
) {
    var tab by remember { mutableStateOf(0) }
    val labels = listOf("Dashboard / Home", "My Orders", "Notifications", "Profile & Settings")
    val unreadNotifCount = vm.notifications.count { !it.isRead }

    GlobalRoleDrawer(
        vm = vm,
        role = Role.Customer,
        currentSelectedLabel = labels[tab],
        onSelectItem = { routeKey ->
            when (routeKey) {
                "home" -> tab = 0
                "restaurants" -> tab = 0
                "food" -> onPickRestaurant()
                "individual" -> vm.deliveryModeTab = "Individual"
                "friends" -> onPickRestaurant()
                "categories" -> tab = 0
                "cart" -> onPickRestaurant()
                "orders" -> tab = 1
                "tracking" -> { if (vm.orders.isNotEmpty()) onOpenOrder(vm.orders.first().id) else tab = 1 }
                "favorites" -> tab = 0
                "alerts" -> tab = 2
                "history" -> onOpenReports()
                "profile" -> onOpenProfile()
                "settings" -> tab = 3
                "change_pass" -> onOpenChangePass()
                "logout" -> onSignOut()
            }
        },
        onSignOut = onSignOut
    ) { openDrawer ->
        Scaffold(
            containerColor = SurfaceBg,
            bottomBar = {
                NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                    listOf(
                        "Home" to Icons.Default.Home,
                        "Orders" to Icons.Default.List,
                        "Alerts" to Icons.Default.Notifications,
                        "Profile" to Icons.Default.Person
                    ).forEachIndexed { i, (l, ic) ->
                        NavigationBarItem(
                            selected = tab == i,
                            onClick = { tab = i },
                            icon = {
                                Box {
                                    Icon(ic, l)
                                    if (i == 2 && unreadNotifCount > 0) {
                                        Box(
                                            Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryColor)
                                                .align(Alignment.TopEnd)
                                        )
                                    }
                                }
                            },
                            label = { Text(l, fontSize = 11.sp, fontWeight = if (tab == i) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryColor,
                                selectedTextColor = PrimaryColor,
                                indicatorColor = PrimaryColor.copy(alpha = 0.12f),
                                unselectedIconColor = GrayColor,
                                unselectedTextColor = GrayColor
                            )
                        )
                    }
                }
            }
        ) { pad ->
            Column(
                Modifier
                    .padding(pad)
                    .fillMaxSize()
            ) {
                when (tab) {
                    0 -> CustomerHome(vm, onPickRestaurant, onOpenOrder, openDrawer)
                    1 -> {
                        ScreenHeader("My Orders", onMenuClick = openDrawer)
                        OrdersList(vm, onOpenOrder, onPickRestaurant)
                    }
                    2 -> CustomerNotificationCenter(vm, onOpenOrder, openDrawer)
                    else -> {
                        ScreenHeader("Profile & Settings", onMenuClick = openDrawer)
                        ProfileCard(vm, onOpenProfile, onOpenChangePass, onSignOut)
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerHome(
    vm: AppVm,
    onPick: () -> Unit,
    onOpenOrder: (String) -> Unit,
    openDrawer: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var showAddressModal by remember { mutableStateOf(false) }

    // Smooth Coral-Red Entrance Transition (Section 7)
    val headerAnimBg = remember { Animatable(PrimaryColor.copy(alpha = 0.15f)) }
    LaunchedEffect(Unit) {
        headerAnimBg.animateTo(Color.White, animationSpec = tween(600))
    }

    // Category & Cuisine Filter Logic
    val filteredDishes = remember(query, vm.activeCategoryFilter, vm.activeDietaryFilter, vm.activeSortOption, vm.restaurantMenuList.size) {
        var list = vm.restaurantMenuList.filter { m ->
            m.id !in vm.unavailable &&
            (query.isBlank() || m.name.contains(query, ignoreCase = true) || m.category.contains(query, ignoreCase = true)) &&
            (vm.activeCategoryFilter == "All Feeds" || m.category.contains(vm.activeCategoryFilter, ignoreCase = true) || (vm.activeCategoryFilter == "Low Prices" && m.priceEtb <= 150)) &&
            (when (vm.activeDietaryFilter) {
                "Fasting (Tsom)" -> m.isFasting
                "Halal Certified" -> m.isHalal
                "Under 150 ETB" -> m.priceEtb <= 150
                else -> true
            })
        }

        list = when (vm.activeSortOption) {
            "Lowest Price" -> list.sortedBy { it.priceEtb }
            "Highest Price" -> list.sortedByDescending { it.priceEtb }
            "Newest" -> list.sortedByDescending { it.createdAt }
            else -> list
        }
        list
    }

    // Foods posted within previous 7 days ("New This Week", Section 5)
    val sevenDaysAgo = remember { System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L }
    val newThisWeekDishes = remember(vm.restaurantMenuList.size) {
        vm.restaurantMenuList.filter { m ->
            m.id !in vm.unavailable && m.createdAt >= sevenDaysAgo
        }.sortedByDescending { it.createdAt }
    }

    val activeOrder = vm.orders.firstOrNull {
        it.status == OrderStatus.InTransit || it.status == OrderStatus.Preparing ||
                it.status == OrderStatus.Pending || it.status == OrderStatus.Ready
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(headerAnimBg.value)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        ScreenHeader("Dilla Food Delivery", onMenuClick = openDrawer)
        LocationDropoffBanner(
            address = vm.selectedAddress,
            onChangeLocation = { showAddressModal = true }
        )

        // Active Order Banner on Home Screen
        if (activeOrder != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onOpenOrder(activeOrder.id) },
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = BorderStroke(1.5.dp, PrimaryColor)
            ) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("ACTIVE ORDER #${activeOrder.id}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                            Spacer(Modifier.width(6.dp))
                            StatusChip(activeOrder.status.label, TertiaryColor)
                        }
                        Text("${activeOrder.restaurant} · ${etb(activeOrder.totalEtb)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = InkColor)
                        Text("To: ${activeOrder.deliveryAddress.addressText}", fontSize = 11.sp, color = GrayColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = PrimaryColor
                    ) {
                        Text("Track Order", Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        SearchBarWithFilters(
            query = query,
            onQueryChange = { query = it },
            placeholder = "Search shiro, tibs, burgers, juice..."
        )
        DeliveryModeToggleBar(
            activeTab = vm.deliveryModeTab,
            onTabSelected = { vm.deliveryModeTab = it }
        )
        DietaryChipsBar(
            selectedFilter = vm.activeDietaryFilter,
            onSelectFilter = { vm.activeDietaryFilter = it }
        )

        // Sort Control Bar
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sort by:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("Lowest Price", "Highest Price", "Newest").forEach { sort ->
                    Pill(sort, vm.activeSortOption == sort) { vm.activeSortOption = sort }
                }
            }
        }

        PromotionalHeroCard(
            onStartGroup = onPick
        )

        // Section 5: Foods Posted Within the Last Seven Days ("New This Week")
        if (newThisWeekDishes.isNotEmpty()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("New This Week 🌟", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)
                StatusChip("Posted Recently", SecondaryColor)
            }
            LazyRow(
                Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(newThisWeekDishes) { dish ->
                    Surface(
                        modifier = Modifier
                            .width(160.dp)
                            .clickable { onPick() },
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 3.dp
                    ) {
                        Column(Modifier.padding(10.dp)) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(dish.emoji, fontSize = 32.sp)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(dish.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = InkColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(dish.restaurantName, fontSize = 11.sp, color = GrayColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(etb(dish.priceEtb), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryColor, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }

        CategoryChipsCarousel(
            selectedCategory = vm.activeCategoryFilter,
            onSelectCategory = { vm.activeCategoryFilter = it }
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Featured Dilla Kitchen Dishes (${filteredDishes.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)
                Text("Hot meals delivered anywhere in Dilla service area", fontSize = 11.sp, color = GrayColor)
            }
            StatusChip("${filteredDishes.size} Available", TertiaryColor)
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredDishes.isEmpty()) {
                EmptyState("No food items currently available in this category.", "Try selecting another category or resetting filters.")
            } else {
                filteredDishes.forEach { dish ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPick() },
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        shadowElevation = 3.dp
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SurfaceBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(dish.emoji, fontSize = 34.sp)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(dish.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = InkColor)
                                        Spacer(Modifier.width(6.dp))
                                        if (dish.isFasting) StatusChip("Fasting", TertiaryColor)
                                    }
                                    Text("By ${dish.restaurantName} · ${dish.category}", fontSize = 12.sp, color = GrayColor)
                                    Text(
                                        etb(dish.priceEtb),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryColor,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                                PrimaryButton("Add to Cart", modifier = Modifier.width(96.dp)) {
                                    vm.add("me", dish.id)
                                    onPick()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddressModal) {
        AddressSelectionModal(
            vm = vm,
            onDismiss = { showAddressModal = false }
        )
    }
}

/** Notification Center with read/unread status, unread count badge, and order navigation. */
@Composable
fun CustomerNotificationCenter(
    vm: AppVm,
    onOpenOrder: (String) -> Unit,
    openDrawer: () -> Unit
) {
    val unreadCount = vm.notifications.count { !it.isRead }

    Column(
        Modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        ScreenHeader("Notification Center", onMenuClick = openDrawer) {
            if (unreadCount > 0) {
                TextButton(onClick = { vm.markAllNotificationsAsRead() }) {
                    Text("Mark All Read", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                }
            }
        }

        if (vm.notifications.isEmpty()) {
            EmptyState("No notifications yet", "Order updates, rider tracking alerts, and new restaurant announcements will appear here.")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(vm.notifications) { notif ->
                    SoftCard(
                        modifier = Modifier.clickable {
                            vm.markNotificationAsRead(notif.id)
                            notif.orderId?.let { onOpenOrder(it) }
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (notif.isRead) SurfaceBg else PrimaryColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (notif.title.contains("Delivered")) Icons.Default.CheckCircle else Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = if (notif.isRead) GrayColor else PrimaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = InkColor)
                                    Spacer(Modifier.width(6.dp))
                                    if (!notif.isRead) StatusChip("New", PrimaryColor)
                                }
                                Text(notif.message, fontSize = 11.sp, color = GrayColor, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(notif.timestamp, fontSize = 9.sp, color = GrayColor, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrdersList(
    vm: AppVm,
    onOpen: (String) -> Unit,
    onReorder: () -> Unit
) {
    if (vm.orders.isEmpty()) {
        EmptyState("No orders yet", "Your placed orders will show up here.")
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(vm.orders) { o ->
            SoftCard(Modifier.clickable { onOpen(o.id) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("${o.restaurant} · ${o.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                        Text("${o.itemCount} items · ${etb(o.totalEtb)}", fontSize = 12.sp, color = GrayColor)
                        Text("Destination: ${o.deliveryAddress.addressText}", fontSize = 11.sp, color = GrayColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    StatusChip(o.status.label, if (o.status == OrderStatus.InTransit) PrimaryColor else TertiaryColor)
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlineButton("Reorder") {
                        vm.reorder(o.id)
                        onReorder()
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCard(
    vm: AppVm,
    onOpenProfile: () -> Unit,
    onOpenChangePass: () -> Unit,
    onSignOut: () -> Unit
) {
    var showAddressModal by remember { mutableStateOf(false) }

    // Notification Preferences
    var pushNotify by remember { mutableStateOf(true) }
    var orderNotify by remember { mutableStateOf(true) }
    var smsNotify by remember { mutableStateOf(false) }

    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SoftCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(vm.me, size = 56.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(vm.userName, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = InkColor)
                        Spacer(Modifier.width(6.dp))
                        StatusChip("Active", TertiaryColor)
                    }
                    Text("Role: ${vm.role?.label ?: "Customer"}", color = GrayColor, fontSize = 12.sp)
                    Text("Active Address: ${vm.selectedAddress.label}", color = PrimaryColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlineButton("Manage Profile", Modifier.weight(1f)) { onOpenProfile() }
                OutlineButton("Change Password", Modifier.weight(1f)) { onOpenChangePass() }
            }
        }

        // Notification Preferences Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(Modifier.padding(14.dp)) {
                Text("Notification Preferences", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Push Notifications", fontSize = 12.sp, color = InkColor)
                    Switch(pushNotify, { pushNotify = it }, colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Order Status Alerts", fontSize = 12.sp, color = InkColor)
                    Switch(orderNotify, { orderNotify = it }, colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("SMS Receipts & Group Invites", fontSize = 12.sp, color = InkColor)
                    Switch(smsNotify, { smsNotify = it }, colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor))
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(Modifier.padding(14.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Saved Delivery Addresses", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                    TextButton(onClick = { showAddressModal = true }) {
                        Text("Manage", fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(6.dp))
                vm.savedAddresses.forEach { addr ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (addr.isSelected) Icons.Default.LocationOn else Icons.Default.Place,
                            contentDescription = null,
                            tint = if (addr.isSelected) PrimaryColor else GrayColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(addr.label + " (" + addr.areaZone + ")", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
                            Text(addr.addressText, fontSize = 11.sp, color = GrayColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        if (addr.isSelected) {
                            StatusChip("Active", PrimaryColor)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        PrimaryButton("Sign Out", onClick = onSignOut)
    }

    if (showAddressModal) {
        AddressSelectionModal(
            vm = vm,
            onDismiss = { showAddressModal = false }
        )
    }
}

/** Comprehensive User Profile Management Screen for all roles with Android System Gallery Picker. */
@Composable
fun UserProfileScreen(
    vm: AppVm,
    onBack: () -> Unit
) {
    var fullName by remember { mutableStateOf(vm.userName) }
    var phone by remember { mutableStateOf("+251 91 234 5678") }
    var email by remember { mutableStateOf("user@dilla.edu.et") }
    var address by remember { mutableStateOf(vm.selectedAddress.addressText) }
    var landmark by remember { mutableStateOf(vm.selectedAddress.landmark) }
    var bio by remember { mutableStateOf("Food lover in Dilla Town & Campus.") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var hasCustomPhoto by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var savedMsg by remember { mutableStateOf(false) }

    val repository = remember { FirebaseRepository() }

    // Native Android System Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
            isUploading = true
            hasCustomPhoto = true
            repository.uploadImage(
                path = "profile_photos/${System.currentTimeMillis()}.jpg",
                imageUri = uri,
                onProgress = {},
                onSuccess = {
                    isUploading = false
                },
                onError = {
                    isUploading = false
                }
            )
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        ScreenHeader("My Profile", onBack = onBack)

        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SoftCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(fullName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = InkColor)
                        Text("Role: ${vm.role?.label ?: "Customer"}", fontSize = 12.sp, color = GrayColor)
                        Text(
                            when {
                                isUploading -> "Uploading photo to Firebase..."
                                hasCustomPhoto -> "✓ Custom profile photo active"
                                else -> "Default profile avatar active"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasCustomPhoto) TertiaryColor else PrimaryColor
                        )
                    }
                    OutlineButton("Change Photo") {
                        galleryLauncher.launch("image/*")
                    }
                }
            }

            SoftCard {
                Text("Personal Profile Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(value = fullName, onValueChange = { fullName = it; savedMsg = false }, label = { Text("Full Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it; savedMsg = false }, label = { Text("Phone Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it; savedMsg = false }, label = { Text("Email Address") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = address, onValueChange = { address = it; savedMsg = false }, label = { Text("Delivery Address") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = landmark, onValueChange = { landmark = it; savedMsg = false }, label = { Text("Landmark") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = bio, onValueChange = { bio = it; savedMsg = false }, label = { Text("Biography / Notes") }, modifier = Modifier.fillMaxWidth())
            }

            if (savedMsg) {
                Text("✓ Profile information saved successfully!", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TertiaryColor)
            }

            PrimaryButton("Save Profile Changes") {
                if (fullName.isNotBlank()) vm.userName = fullName.trim()
                savedMsg = true
            }
        }
    }
}

/** Customer Personal Financial & Order History Report Screen. */
@Composable
fun CustomerReportScreen(
    vm: AppVm,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedPeriod by remember { mutableStateOf("This Month") }
    val periods = listOf("Today", "This Week", "This Month", "Custom Range")

    val completedOrders = vm.orders.filter { it.status == OrderStatus.Delivered }
    val cancelledOrders = vm.orders.filter { it.status == OrderStatus.Rejected }
    val pendingOrders = vm.orders.filter { it.status == OrderStatus.Pending || it.status == OrderStatus.Preparing || it.status == OrderStatus.Ready || it.status == OrderStatus.InTransit }
    val totalSpending = completedOrders.sumOf { it.totalEtb }

    Column(
        Modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        ScreenHeader("Order History & Spending Report", onBack = onBack)

        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Period Selector
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Select Reporting Period:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GrayColor)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(periods) { p ->
                        Pill(p, selectedPeriod == p) { selectedPeriod = p }
                    }
                }
            }

            // Summary Metrics Cards
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Total Spending", etb(totalSpending), Icons.Default.ShoppingCart, PrimaryColor, Modifier.weight(1f))
                MetricCard("Completed", "${completedOrders.size} orders", Icons.Default.CheckCircle, TertiaryColor, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Active Pending", "${pendingOrders.size} orders", Icons.Default.DateRange, SecondaryColor, Modifier.weight(1f))
                MetricCard("Cancelled", "${cancelledOrders.size} orders", Icons.Default.Clear, GrayColor, Modifier.weight(1f))
            }

            // Payment Breakdown Card
            SoftCard {
                Text("PAYMENT METHOD BREAKDOWN ($selectedPeriod)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
                Spacer(Modifier.height(8.dp))
                ReportRow("Cash on Delivery (COD)", etb(totalSpending))
                ReportRow("Digital Wallet / CBE Birr", etb(0))
                ReportRow("Equal Group Splits Paid", etb(if (vm.splitOn) totalSpending / 2 else 0))
            }

            // Export Controls Card
            SoftCard {
                Text("Export Authorized Customer Report", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                Text("Download or share verified order history and financial totals.", fontSize = 11.sp, color = GrayColor)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PrimaryButton("Export CSV (.csv)", Modifier.weight(1f)) {
                        ReportExportService.exportCustomerReportCsv(context, vm, selectedPeriod)
                    }
                    OutlineButton("Export Excel (.xlsx)", Modifier.weight(1f)) {
                        ReportExportService.exportCustomerReportExcel(context, vm, selectedPeriod)
                    }
                }
            }

            // Orders Table List
            Text("Detailed Order Logs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = InkColor)
            if (vm.orders.isEmpty()) {
                EmptyState("No orders found for $selectedPeriod", "Your completed orders will appear here.")
            } else {
                vm.orders.forEach { o ->
                    SoftCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("ORDER #${o.id} · ${o.restaurant}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = InkColor)
                                Text("Items: ${o.itemCount} · Total: ${etb(o.totalEtb)}", fontSize = 11.sp, color = GrayColor)
                                Text("Payment: ${o.payment}", fontSize = 11.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                            }
                            StatusChip(o.status.label, if (o.status == OrderStatus.Delivered) TertiaryColor else PrimaryColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(12.dp)) {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = InkColor)
            Text(title, fontSize = 11.sp, color = GrayColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ReportRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = GrayColor)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
    }
}

@Composable
fun OrderDetailScreen(vm: AppVm, id: String, onBack: () -> Unit) {
    val o = vm.order(id) ?: vm.orders.firstOrNull()
    Column(
        Modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        ScreenHeader("Live Delivery Tracker", onBack = onBack)
        if (o == null) {
            EmptyState("Order not found", "It may have been removed.")
            return
        }
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero Status Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ORDER #${o.id}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
                        StatusChip("Live GPS Tracking", SecondaryColor)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (o.status == OrderStatus.InTransit) "Rider ${o.riderName} is on the way!" else o.status.label,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkColor
                    )
                    Text(
                        "Estimated Arrival: 12:48 PM (Approx. 8 mins remaining)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Delivery Destination & Pickup Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("PICKUP: ${o.pickupLocation}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEAEAEA))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text("DESTINATION: ${o.deliveryAddress.label.uppercase()} (${o.deliveryAddress.areaZone})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                            Text(o.deliveryAddress.addressText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = InkColor)
                            if (o.deliveryAddress.landmark.isNotBlank()) {
                                Text("Landmark: ${o.deliveryAddress.landmark}", fontSize = 11.sp, color = GrayColor)
                            }
                            if (o.deliveryAddress.deliveryInstructions.isNotBlank()) {
                                Text("Instructions: ${o.deliveryAddress.deliveryInstructions}", fontSize = 11.sp, color = GrayColor)
                            }
                        }
                    }
                }
            }

            // Interactive TomTom Map Canvas Box
            TomTomMapView(order = o)

            // Delivery Milestones Timeline
            SoftCard {
                Text("Delivery Milestones", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Modifier.padding(bottom = 10.dp).let { InkColor })
                val stages = listOf(
                    OrderStatus.Pending to "Order Placed at ${o.restaurant}",
                    OrderStatus.Preparing to "Prepared & Packed in Thermal Bag",
                    OrderStatus.Ready to "Handed over to Rider ${o.riderName}",
                    OrderStatus.InTransit to "In Transit to ${o.deliveryAddress.label}",
                    OrderStatus.Delivered to "Delivered & PIN Confirmed"
                )
                stages.forEach { (st, label) ->
                    val done = st.ordinal <= o.status.ordinal
                    Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (done) TertiaryColor else Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (done) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            label,
                            fontWeight = if (st == o.status) FontWeight.Bold else FontWeight.Normal,
                            color = if (st == o.status) PrimaryColor else InkColor,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Handover Delivery PIN Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("SECURITY HANDOVER PIN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
                    Text(o.pin, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryColor, letterSpacing = 4.sp)
                    Text("Give this 4-digit PIN to rider ${o.riderName} when receiving your food.", fontSize = 11.sp, color = GrayColor, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
