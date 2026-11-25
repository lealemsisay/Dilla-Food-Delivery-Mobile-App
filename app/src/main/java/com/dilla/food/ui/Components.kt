package com.dilla.food.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.dilla.food.data.*
import kotlinx.coroutines.launch

fun etb(v: Int) = "ETB " + String.format("%,d", v)

/** Pill selection chip used in auth, roles, and address modals. */
@Composable
fun Pill(
    text: String,
    selected: Boolean,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(50),
        color = if (selected) PrimaryColor else Color.White,
        border = if (selected) null else BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Text(
            text,
            Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else if (enabled) InkColor else GrayColor,
            textAlign = TextAlign.Center
        )
    }
}

/** Top App Header styled according to Stitch specifications with optional Hamburger Menu or Back button. */
@Composable
fun ScreenHeader(
    title: String,
    onBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (onMenuClick != null) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(38.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Open Hamburger Navigation", tint = InkColor)
                }
            } else if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = InkColor)
                }
            }
            Column(
                Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("DELIVERING IN DILLA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor, letterSpacing = 1.sp)
                Text(
                    title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (trailing != null) {
                Box(Modifier.align(Alignment.CenterEnd)) { trailing() }
            }
        }
    }
}

data class DrawerMenuItem(
    val label: String,
    val icon: ImageVector,
    val routeKey: String
)

/** Global Hamburger Navigation Drawer Component supporting Customer, Restaurant, Rider, and Admin roles. */
@Composable
fun GlobalRoleDrawer(
    vm: AppVm,
    role: Role,
    currentSelectedLabel: String,
    onSelectItem: (String) -> Unit,
    onSignOut: () -> Unit,
    content: @Composable (openDrawer: () -> Unit) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val menuItems = remember(role) {
        when (role) {
            Role.Customer -> listOf(
                DrawerMenuItem("Dashboard / Home", Icons.Default.Home, "home"),
                DrawerMenuItem("Restaurants", Icons.Default.ShoppingCart, "restaurants"),
                DrawerMenuItem("All Food", Icons.Default.List, "food"),
                DrawerMenuItem("Individual Ordering", Icons.Default.Person, "individual"),
                DrawerMenuItem("Group Ordering", Icons.Default.Share, "friends"),
                DrawerMenuItem("Food Categories", Icons.Default.List, "categories"),
                DrawerMenuItem("My Cart", Icons.Default.ShoppingCart, "cart"),
                DrawerMenuItem("My Orders", Icons.Default.List, "orders"),
                DrawerMenuItem("Order Tracking", Icons.Default.LocationOn, "tracking"),
                DrawerMenuItem("Favorites", Icons.Default.Favorite, "favorites"),
                DrawerMenuItem("Notifications", Icons.Default.Notifications, "alerts"),
                DrawerMenuItem("Reports / Order History", Icons.Default.DateRange, "history"),
                DrawerMenuItem("My Profile", Icons.Default.AccountCircle, "profile"),
                DrawerMenuItem("Settings", Icons.Default.Settings, "settings"),
                DrawerMenuItem("Change Password", Icons.Default.Lock, "change_pass"),
                DrawerMenuItem("Logout", Icons.AutoMirrored.Filled.ExitToApp, "logout")
            )
            Role.Restaurant -> listOf(
                DrawerMenuItem("Dashboard", Icons.Default.Home, "dashboard"),
                DrawerMenuItem("Incoming Orders", Icons.Default.ShoppingCart, "orders"),
                DrawerMenuItem("Order History", Icons.Default.DateRange, "history"),
                DrawerMenuItem("Food / Menu Management", Icons.Default.List, "menu"),
                DrawerMenuItem("Food Categories", Icons.Default.List, "categories"),
                DrawerMenuItem("Restaurant Profile", Icons.Default.Person, "profile"),
                DrawerMenuItem("Restaurant Location", Icons.Default.LocationOn, "location"),
                DrawerMenuItem("Sales Reports", Icons.Default.DateRange, "reports"),
                DrawerMenuItem("Notifications", Icons.Default.Notifications, "notifications"),
                DrawerMenuItem("Settings", Icons.Default.Settings, "settings"),
                DrawerMenuItem("Change Password", Icons.Default.Lock, "change_pass"),
                DrawerMenuItem("Logout", Icons.AutoMirrored.Filled.ExitToApp, "logout")
            )
            Role.Rider -> listOf(
                DrawerMenuItem("Dashboard", Icons.Default.Home, "dashboard"),
                DrawerMenuItem("Delivery Requests", Icons.Default.Place, "requests"),
                DrawerMenuItem("Active Deliveries", Icons.Default.ShoppingCart, "active"),
                DrawerMenuItem("Delivery History", Icons.Default.DateRange, "history"),
                DrawerMenuItem("Earnings", Icons.Default.CheckCircle, "earnings"),
                DrawerMenuItem("Reports", Icons.Default.DateRange, "reports"),
                DrawerMenuItem("Notifications", Icons.Default.Notifications, "notifications"),
                DrawerMenuItem("My Profile", Icons.Default.AccountCircle, "profile"),
                DrawerMenuItem("Settings", Icons.Default.Settings, "settings"),
                DrawerMenuItem("Change Password", Icons.Default.Lock, "change_pass"),
                DrawerMenuItem("Logout", Icons.AutoMirrored.Filled.ExitToApp, "logout")
            )
            Role.Admin -> listOf(
                DrawerMenuItem("Dashboard", Icons.Default.Home, "dashboard"),
                DrawerMenuItem("Customer Management", Icons.Default.Person, "customers"),
                DrawerMenuItem("Restaurant Management", Icons.Default.ShoppingCart, "restaurants"),
                DrawerMenuItem("Rider Management", Icons.Default.Place, "riders"),
                DrawerMenuItem("Order Management", Icons.Default.List, "orders"),
                DrawerMenuItem("Live Delivery Monitoring", Icons.Default.LocationOn, "monitoring"),
                DrawerMenuItem("Food Categories Management", Icons.Default.List, "categories"),
                DrawerMenuItem("Service Zones", Icons.Default.LocationOn, "zones"),
                DrawerMenuItem("Delivery Fees", Icons.Default.CheckCircle, "fees"),
                DrawerMenuItem("Reports", Icons.Default.DateRange, "reports"),
                DrawerMenuItem("Notifications", Icons.Default.Notifications, "notifications"),
                DrawerMenuItem("Audit Logs", Icons.Default.Lock, "audit"),
                DrawerMenuItem("Settings", Icons.Default.Settings, "settings"),
                DrawerMenuItem("Change Password", Icons.Default.Lock, "change_pass"),
                DrawerMenuItem("Logout", Icons.AutoMirrored.Filled.ExitToApp, "logout")
            )
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                drawerContentColor = InkColor,
                modifier = Modifier.width(300.dp)
            ) {
                // Top Header in Drawer
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(PrimaryColor)
                        .padding(20.dp)
                        .statusBarsPadding()
                ) {
                    Box(
                        Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(vm.userName.take(1).uppercase(), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryColor)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(vm.userName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusChip(role.label, SecondaryColor)
                        Spacer(Modifier.width(6.dp))
                        Text("Dilla Service Area", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                    }
                }

                HorizontalDivider(color = Color(0xFFEAEAEA))

                // Scrollable Drawer Menu Items
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    menuItems.forEach { item ->
                        val isSelected = item.label == currentSelectedLabel || item.routeKey == currentSelectedLabel
                        NavigationDrawerItem(
                            label = { Text(item.label, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                            icon = { Icon(item.icon, contentDescription = item.label, tint = if (isSelected) PrimaryColor else GrayColor, modifier = Modifier.size(20.dp)) },
                            selected = isSelected,
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (item.routeKey == "logout") {
                                    onSignOut()
                                } else {
                                    onSelectItem(item.routeKey)
                                }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = PrimaryColor.copy(alpha = 0.12f),
                                selectedIconColor = PrimaryColor,
                                selectedTextColor = PrimaryColor,
                                unselectedContainerColor = Color.Transparent,
                                unselectedIconColor = GrayColor,
                                unselectedTextColor = InkColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    ) {
        content { scope.launch { drawerState.open() } }
    }
}

/** Address selection modal dialog allowing customers to select or add addresses. */
@Composable
fun AddressSelectionModal(
    vm: AppVm,
    onDismiss: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Delivery Location", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                vm.savedAddresses.forEach { addr ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                vm.selectAddress(addr.id)
                                onDismiss()
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (addr.isSelected) PrimaryColor.copy(alpha = 0.1f) else SurfaceBg,
                        border = BorderStroke(1.dp, if (addr.isSelected) PrimaryColor else Color(0xFFE0E0E0))
                    ) {
                        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (addr.isSelected) Icons.Default.LocationOn else Icons.Default.Place, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(addr.label + " (" + addr.areaZone + ")", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
                                Text(addr.addressText, fontSize = 11.sp, color = GrayColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            if (addr.isSelected) StatusChip("Selected", PrimaryColor)
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                OutlineButton("+ Add New Delivery Address", Modifier.fillMaxWidth()) {
                    showAddDialog = true
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Done", fontWeight = FontWeight.Bold, color = PrimaryColor) } }
    )

    if (showAddDialog) {
        AddAddressDialog(vm = vm, onDismiss = { showAddDialog = false })
    }
}

@Composable
fun AddAddressDialog(
    vm: AppVm,
    onDismiss: () -> Unit
) {
    var labelInput by remember { mutableStateOf("Home") }
    var addressInput by remember { mutableStateOf("") }
    var landmarkInput by remember { mutableStateOf("") }
    var zoneInput by remember { mutableStateOf(DeliveryZoneConfig.supportedZones.first()) }
    var instructionsInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Dilla Delivery Address", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = labelInput, onValueChange = { labelInput = it }, label = { Text("Label (e.g. Work, Home, Dorm)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = addressInput, onValueChange = { addressInput = it }, label = { Text("Street Address / Building") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = landmarkInput, onValueChange = { landmarkInput = it }, label = { Text("Landmark / Nearby Feature") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = zoneInput, onValueChange = { zoneInput = it }, label = { Text("Service Zone Area") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = instructionsInput, onValueChange = { instructionsInput = it }, label = { Text("Delivery Instructions") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (addressInput.isNotBlank()) {
                        val newAddr = DeliveryAddress(
                            id = "addr_" + System.currentTimeMillis(),
                            label = labelInput,
                            addressText = addressInput,
                            landmark = landmarkInput,
                            areaZone = zoneInput,
                            deliveryInstructions = instructionsInput,
                            isSelected = true
                        )
                        vm.addAddress(newAddr)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) { Text("Save Address", fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = GrayColor) } }
    )
}

/** Town & Campus drop-off location notice card. */
@Composable
fun LocationDropoffBanner(
    address: DeliveryAddress,
    onChangeLocation: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("DELIVER TO: ${address.label.uppercase()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    Spacer(Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = TertiaryColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            "In Dilla Zone ✓",
                            Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TertiaryColor
                        )
                    }
                }
                Text(address.addressText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = InkColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (address.landmark.isNotBlank()) {
                    Text("Landmark: ${address.landmark}", fontSize = 11.sp, color = GrayColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(Modifier.width(6.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = SurfaceBg,
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.clickable(onClick = onChangeLocation)
            ) {
                Text(
                    "Change",
                    Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }
        }
    }
}

/** Search bar with filter chip trigger button. */
@Composable
fun SearchBarWithFilters(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search shiro, tibs, burgers, juice...",
    onFilterClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = GrayColor, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(placeholder, color = GrayColor, fontSize = 13.sp)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = InkColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                )
            }
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = GrayColor, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.width(6.dp))
            Surface(
                shape = CircleShape,
                color = PrimaryColor.copy(alpha = 0.12f),
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onFilterClick)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.List, contentDescription = "Filter", tint = PrimaryColor, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

/** Segmented Mode Toggle (Individual vs Group Order vs Family Feasts). */
@Composable
fun DeliveryModeToggleBar(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    val options = listOf("Individual", "Group Order", "Family Feasts")
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        color = SurfaceContainer
    ) {
        Row(Modifier.padding(4.dp)) {
            options.forEach { opt ->
                val selected = activeTab == opt
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(opt) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) Color.White else Color.Transparent,
                    shadowElevation = if (selected) 2.dp else 0.dp
                ) {
                    Text(
                        opt,
                        Modifier.padding(vertical = 8.dp),
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) PrimaryColor else GrayColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/** Horizontal Dietary & Category chips. */
@Composable
fun DietaryChipsBar(
    selectedFilter: String,
    onSelectFilter: (String) -> Unit
) {
    val filters = listOf("All Cuisines", "Fasting (Tsom)", "Halal Certified", "Family Sized (4-8p)", "Under 150 ETB")
    LazyRow(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { f ->
            Pill(f, selectedFilter == f) { onSelectFilter(f) }
        }
    }
}

/** Category Carousel. */
@Composable
fun CategoryChipsCarousel(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    val categories = listOf(
        "All Feeds" to "🍲",
        "Traditional" to "🥩",
        "Fast Food" to "🍔",
        "Campus Cafes" to "☕",
        "Juice & Drinks" to "🥑",
        "Late Night" to "🌙"
    )
    LazyRow(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { (cat, emoji) ->
            val sel = selectedCategory == cat
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelectCategory(cat) },
                shape = RoundedCornerShape(16.dp),
                color = if (sel) PrimaryColor else Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 16.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        cat,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sel) Color.White else InkColor
                    )
                }
            }
        }
    }
}

/** Promotional Hero Card for Dilla Group Feasts. */
@Composable
fun PromotionalHeroCard(
    onStartGroup: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = PrimaryColor,
        shadowElevation = 4.dp
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(PrimaryColor, PrimaryContainer)
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = SecondaryColor
                ) {
                    Text(
                        "CAMPUS & TOWN SPECIAL",
                        Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Order Together, Save More!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    "Split delivery fees equally with roomies or colleagues. Flat 40 ETB off group feasts.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    modifier = Modifier.clickable(onClick = onStartGroup)
                ) {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Start Group Room", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColor,
            contentColor = Color.White,
            disabledContainerColor = PrimaryColor.copy(alpha = 0.4f)
        )
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OutlineButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, PrimaryColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryColor)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatusChip(
    text: String,
    color: Color = PrimaryColor
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text,
            Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun EmptyState(title: String, body: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Info, contentDescription = null, tint = GrayColor, modifier = Modifier.size(36.dp))
        Spacer(Modifier.height(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = InkColor)
        Text(body, fontSize = 12.sp, color = GrayColor, textAlign = TextAlign.Center)
    }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(14.dp), content = content)
    }
}

@Composable
fun Avatar(person: Person, size: Dp = 40.dp) {
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = person.color,
        border = BorderStroke(2.dp, Color.White)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(person.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = (size.value * 0.4).sp)
        }
    }
}
