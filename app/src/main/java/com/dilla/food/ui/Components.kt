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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.dilla.food.data.*

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

/** Top App Header styled according to Stitch specifications. */
@Composable
fun ScreenHeader(
    title: String,
    onBack: (() -> Unit)? = null,
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
            if (onBack != null) {
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
                Text(
                    if (address.landmark.isNotBlank()) "Landmark: ${address.landmark}" else address.areaZone,
                    fontSize = 11.sp,
                    color = GrayColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(6.dp))
            IconButton(
                onClick = onChangeLocation,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainer)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Change Location", tint = GrayColor, modifier = Modifier.size(18.dp))
            }
        }
    }
}

/** Address Selection & Delivery Destination Modal. */
@Composable
fun AddressSelectionModal(
    vm: AppVm,
    onDismiss: () -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Delivery Destination", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor)
                Text("Dilla Town & Surrounding Service Area", fontSize = 11.sp, color = GrayColor)
            }
        },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                vm.savedAddresses.forEach { addr ->
                    val isSel = addr.id == vm.selectedAddressId
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { vm.selectAddress(addr.id) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) PrimaryColor.copy(alpha = 0.08f) else SurfaceBg,
                        border = BorderStroke(1.5.dp, if (isSel) PrimaryColor else Color(0xFFE0E0E0))
                    ) {
                        Row(
                            Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSel,
                                onClick = { vm.selectAddress(addr.id) },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor)
                            )
                            Spacer(Modifier.width(6.dp))
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(addr.label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
                                    Spacer(Modifier.width(6.dp))
                                    if (addr.isInsideServiceZone) {
                                        StatusChip("Dilla Zone", TertiaryColor)
                                    } else {
                                        StatusChip("Outside Zone", GrayColor)
                                    }
                                }
                                Text(addr.addressText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = InkColor)
                                if (addr.landmark.isNotBlank()) {
                                    Text("Landmark: ${addr.landmark}", fontSize = 10.sp, color = GrayColor)
                                }
                            }
                            if (vm.savedAddresses.size > 1) {
                                IconButton(
                                    onClick = { vm.deleteAddress(addr.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GrayColor, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
                OutlinedButton(
                    onClick = { showAddForm = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, PrimaryColor)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("+ Add New Delivery Address", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Confirm Selection", fontWeight = FontWeight.Bold)
            }
        }
    )

    if (showAddForm) {
        AddAddressDialog(
            onDismiss = { showAddForm = false },
            onSave = { newAddr ->
                vm.addAddress(newAddr)
                showAddForm = false
            }
        )
    }
}

/** Add Address Form Dialog. */
@Composable
fun AddAddressDialog(
    onDismiss: () -> Unit,
    onSave: (DeliveryAddress) -> Unit
) {
    var label by remember { mutableStateOf("Home") }
    var addressText by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var selectedZone by remember { mutableStateOf(DeliveryZoneConfig.supportedZones.first()) }
    var instructions by remember { mutableStateOf("") }

    val labels = listOf("Home", "Work / Office", "Shop", "Dormitory", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Delivery Address", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Address Label", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    labels.forEach { l ->
                        Pill(l, label == l) { label = l }
                    }
                }

                OutlinedTextField(
                    value = addressText,
                    onValueChange = { addressText = it },
                    label = { Text("Address / Street / Building") },
                    placeholder = { Text("e.g. Office 304, CBE Building, Piazza") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    label = { Text("Recognizable Landmark") },
                    placeholder = { Text("e.g. Opposite Total Gas Station") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Delivery Area Zone (Dilla)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DeliveryZoneConfig.supportedZones.forEach { z ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { selectedZone = z }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedZone == z,
                                onClick = { selectedZone = z },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(z, fontSize = 11.sp, color = InkColor)
                        }
                    }
                }

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Rider Gate Instructions") },
                    placeholder = { Text("e.g. Call upon arrival at reception") },
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TertiaryColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        "✓ Address coordinates verified inside Dilla Town Service Area",
                        Modifier.padding(8.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TertiaryColor
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (addressText.isNotBlank()) {
                        val newAddr = DeliveryAddress(
                            id = "addr_" + System.currentTimeMillis(),
                            label = label,
                            addressText = addressText.trim(),
                            landmark = landmark.trim(),
                            areaZone = selectedZone,
                            deliveryInstructions = instructions.trim(),
                            isInsideServiceZone = true
                        )
                        onSave(newAddr)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Save Address", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = GrayColor)
            }
        }
    )
}

/** Search bar with voice mic button and dietary filter trigger. */
@Composable
fun SearchBarWithFilters(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search shiro, tibs, burgers, juice...",
    onFilterClick: () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(50),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = GrayColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(placeholder, fontSize = 13.sp, color = GrayColor)
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, color = InkColor),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onFilterClick),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.List, contentDescription = "Filters", tint = PrimaryColor, modifier = Modifier.size(20.dp))
            }
        }
    }
}

/** Segmented Delivery Mode toggle bar. */
@Composable
fun DeliveryModeToggleBar(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf("Individual", "Group Order", "Family Feasts", "Office Lunch")
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(50),
        color = SurfaceContainer
    ) {
        Row(
            Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEach { tab ->
                val selected = tab == activeTab
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable { onTabSelected(tab) },
                    shape = RoundedCornerShape(50),
                    color = if (selected) Color.White else Color.Transparent,
                    shadowElevation = if (selected) 2.dp else 0.dp
                ) {
                    Row(
                        Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            tab,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) PrimaryColor else GrayColor
                        )
                    }
                }
            }
        }
    }
}

/** Horizontal Dietary Filter Chips Bar. */
@Composable
fun DietaryChipsBar(
    selectedFilter: String,
    onSelectFilter: (String) -> Unit
) {
    val filters = listOf("All Cuisines", "Fasting (Tsom)", "Halal Certified", "Family Sized", "Under 150 ETB")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        items(filters) { f ->
            val sel = f == selectedFilter
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onSelectFilter(f) },
                shape = RoundedCornerShape(50),
                color = if (sel) PrimaryColor else Color.White,
                border = if (sel) null else BorderStroke(1.dp, Color(0xFFEDEDED)),
                shadowElevation = if (sel) 2.dp else 1.dp
            ) {
                Text(
                    f,
                    Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (sel) Color.White else InkColor
                )
            }
        }
    }
}

/** Category chips carousel. */
@Composable
fun CategoryChipsCarousel(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    val categories = listOf(
        "All Feeds" to "🍽️",
        "Traditional (Tibs, Shiro)" to "🍲",
        "Fast Food & Burgers" to "🍔",
        "Campus Cafes" to "☕",
        "Fresh Juice" to "🥑",
        "Late Night" to "🌙"
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        items(categories) { (cat, emoji) ->
            val sel = cat == selectedCategory
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onSelectCategory(cat) },
                shape = RoundedCornerShape(50),
                color = if (sel) PrimaryColor else Color.White,
                border = if (sel) null else BorderStroke(1.dp, Color(0xFFEDEDED)),
                shadowElevation = if (sel) 3.dp else 1.dp
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 14.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        cat,
                        fontSize = 12.sp,
                        fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium,
                        color = if (sel) Color.White else InkColor
                    )
                }
            }
        }
    }
}

/** Hero Promotional Banner card. */
@Composable
fun PromotionalHeroCard(onStartGroup: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = PrimaryColor,
        shadowElevation = 4.dp
    ) {
        Box(
            Modifier
                .background(Brush.horizontalGradient(listOf(PrimaryColor, PrimaryContainer)))
                .padding(16.dp)
        ) {
            Column {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        "🎉 DILLA POOL DEAL",
                        Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Citywide & Campus Group Feast Discount",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Order with 3+ friends in town or campus and unlock Free Delivery direct to your address.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kebele 01 & Campus ordering now", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Button(
                        onClick = onStartGroup,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PrimaryColor),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Start Group", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColor,
            contentColor = Color.White,
            disabledContainerColor = PrimaryColor.copy(alpha = 0.4f),
            disabledContentColor = Color.White
        )
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OutlineButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 14.dp),
        border = BorderStroke(1.5.dp, if (enabled) PrimaryColor else GrayColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryColor)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PhotoBox(
    name: String,
    modifier: Modifier = Modifier,
    fallback: @Composable BoxScope.() -> Unit
) {
    val ctx = LocalContext.current
    val id = remember(name) { ctx.resources.getIdentifier(name, "drawable", ctx.packageName) }
    Box(modifier) {
        if (id != 0) Image(painterResource(id), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        else fallback()
    }
}

@Composable
fun Avatar(
    p: Person,
    size: Dp = 44.dp,
    ring: Boolean = false,
    badge: Int? = null
) {
    Box(Modifier.size(size)) {
        Box(
            Modifier
                .size(size)
                .clip(CircleShape)
                .background(p.color)
                .then(if (ring) Modifier.border(2.5.dp, PrimaryColor, CircleShape) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Text(
                p.name.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.42f).sp
            )
        }
        if (badge != null) {
            Box(
                Modifier
                    .size(16.dp)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.TopStart),
                contentAlignment = Alignment.Center
            ) {
                Text("$badge", color = InkColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Status Chip tag with optional green/amber/red indicators. */
@Composable
fun StatusChip(text: String, color: Color = PrimaryColor) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text,
            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyState(title: String, message: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = InkColor)
        Spacer(Modifier.height(6.dp))
        Text(message, color = GrayColor, fontSize = 13.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(14.dp), content = content)
    }
}
