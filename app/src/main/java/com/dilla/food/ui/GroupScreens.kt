package com.dilla.food.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.dilla.food.data.*

private val PageBg = Brush.verticalGradient(listOf(Color.White, SurfaceBg))

/* ================= Screen 1: Choose your friends ================= */
@Composable
fun FriendsScreen(vm: AppVm, onBack: () -> Unit, onProceed: () -> Unit) {
    var showAdd by remember { mutableStateOf(false) }
    var showRename by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(true) }

    Column(
        Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        ScreenHeader("Choose Your Friends", onBack)

        // Live Active Group Session Header Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
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
                    StatusChip("ACTIVE GROUP SESSION", SecondaryColor)
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SecondaryColor.copy(alpha = 0.15f)
                    ) {
                        Row(
                            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF6C3E00), modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Freeze: 14:32", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6C3E00))
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(vm.groupName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = InkColor)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                    Text(vm.restaurant.emoji, fontSize = 14.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(vm.restaurant.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    Text(" · ${vm.restaurant.distanceKm} km", fontSize = 11.sp, color = GrayColor)
                }

                // Invite Code Sub-bar
                Spacer(Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceContainer
                ) {
                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Invite Code: ", fontSize = 11.sp, color = GrayColor)
                            Text(vm.groupInviteCode, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = InkColor)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                modifier = Modifier.clip(CircleShape).clickable { showRename = true },
                                shape = CircleShape,
                                color = Color.White
                            ) {
                                Text("Edit Name", Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                            }
                        }
                    }
                }
            }
        }

        // Individual vs Group Order Segmented Bar
        Surface(
            Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .fillMaxWidth()
                .height(42.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE3E3E3))
        ) {
            Row(Modifier.padding(3.dp)) {
                listOf("Individual" to false, "Group order" to true).forEach { (label, g) ->
                    val sel = vm.groupMode == g
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (sel) PrimaryColor else Color.Transparent)
                            .clickable { vm.groupMode = g },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, fontSize = 12.sp, color = if (sel) Color.White else InkColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (!vm.groupMode) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(vm.restaurant.emoji, fontSize = 64.sp)
                Text(vm.restaurant.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkColor)
                Text("Ordering just for you in Dilla", color = GrayColor, fontSize = 12.sp)
            }
            StepProgress(0.33f)
            Box(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                PrimaryButton("Continue") {
                    vm.startOrder(false)
                    onProceed()
                }
            }
        } else {
            Row(
                Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                listOf("Start New Group" to false, "Saved Groups" to true).forEach { (t, s) ->
                    Column(
                        Modifier.clickable {
                            saved = s
                            if (!s) vm.selected.clear()
                        },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(t, fontSize = 11.sp, color = if (saved == s) InkColor else GrayColor, fontWeight = if (saved == s) FontWeight.Bold else FontWeight.Normal)
                        if (saved == s) Box(Modifier.padding(top = 3.dp).size(4.dp).clip(CircleShape).background(PrimaryColor))
                    }
                }
            }

            // Collaborator Ribbon
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                items(vm.participants) { p ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Avatar(p, size = 28.dp)
                            Spacer(Modifier.width(6.dp))
                            Column {
                                Text(p.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = InkColor)
                                Text(p.statusText, fontSize = 9.sp, color = TertiaryColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyVerticalGrid(
                    GridCells.Fixed(3),
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    gridItems(vm.people) { p ->
                        val sel = p.id in vm.selected
                        Box(
                            Modifier
                                .aspectRatio(0.92f)
                                .shadow(if (sel) 6.dp else 1.dp, RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.verticalGradient(listOf(p.color.copy(alpha = 0.85f), p.color)))
                                .clickable { vm.toggle(p.id) }
                        ) {
                            PhotoBox("friend_" + p.name.lowercase(), Modifier.fillMaxSize()) {
                                Icon(Icons.Default.Person, null, Modifier.align(Alignment.Center).size(48.dp), tint = Color.White.copy(alpha = 0.55f))
                            }
                            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0x66000000)), startY = 120f)))
                            Column(Modifier.align(Alignment.BottomStart).padding(6.dp)) {
                                Text(p.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text(p.note, color = Color.White.copy(alpha = 0.8f), fontSize = 8.sp, maxLines = 1)
                            }
                            if (sel) {
                                Box(Modifier.fillMaxSize().border(2.5.dp, Color.White, RoundedCornerShape(14.dp)))
                                Box(
                                    Modifier
                                        .padding(6.dp)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryColor)
                                        .align(Alignment.TopStart),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, "Selected", tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                    item {
                        Box(
                            Modifier
                                .aspectRatio(0.92f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFEDEDED))
                                .clickable { showAdd = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, "Add friend", Modifier.size(16.dp), tint = InkColor)
                                }
                                Text("Add friend", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GrayColor, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }

            StepProgress(0.33f)
            Box(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                PrimaryButton("Proceed with ${vm.selected.size + 1} Friends", enabled = true) {
                    vm.startOrder(true)
                    onProceed()
                }
            }
        }
    }

    if (showAdd) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Add Friend", fontWeight = FontWeight.Bold) },
            text = { OutlinedTextField(name, { name = it }, label = { Text("Name or Phone Handle") }, singleLine = true) },
            confirmButton = { TextButton({ if (name.isNotBlank()) vm.addFriend(name); showAdd = false }) { Text("Add", color = PrimaryColor, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton({ showAdd = false }) { Text("Cancel", color = GrayColor) } }
        )
    }
    if (showRename) {
        var name by remember { mutableStateOf(vm.groupName) }
        AlertDialog(
            onDismissRequest = { showRename = false },
            title = { Text("Group Session Name", fontWeight = FontWeight.Bold) },
            text = { OutlinedTextField(name, { name = it }, singleLine = true) },
            confirmButton = { TextButton({ if (name.isNotBlank()) vm.groupName = name.trim(); showRename = false }) { Text("Save", color = PrimaryColor, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton({ showRename = false }) { Text("Cancel", color = GrayColor) } }
        )
    }
}

/** Progress bar. */
@Composable
private fun StepProgress(fraction: Float) {
    Box(
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color(0xFFDEDEDE))
    ) {
        Box(
            Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .background(PrimaryColor)
        )
    }
}

/* ================= Screen 2: Select your food ================= */
@Composable
fun FoodScreen(vm: AppVm, onBack: () -> Unit, onCheckout: () -> Unit) {
    val active = vm.person(vm.activeId)
    var sortByPrice by remember { mutableStateOf(false) }
    val available = vm.menu.filter { it.id !in vm.unavailable }
    val cats = available.map { it.category }.distinct()
    var category by remember { mutableStateOf(cats.firstOrNull() ?: "") }
    val catItems = available.filter { it.category == category }.let { l -> if (sortByPrice) l.sortedBy { it.priceEtb } else l }
    var pickedId by remember(category) { mutableStateOf(catItems.firstOrNull()?.id) }
    val picked = catItems.firstOrNull { it.id == pickedId } ?: catItems.firstOrNull()

    Column(Modifier.fillMaxSize().background(Color.White)) {
        ScreenHeader("Select Your Food", onBack, trailing = {
            IconButton(onClick = { sortByPrice = !sortByPrice }) {
                Icon(Icons.Default.Menu, "Sort", tint = if (sortByPrice) PrimaryColor else InkColor)
            }
        })

        // Participant avatars row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
            modifier = Modifier.height(72.dp)
        ) {
            items(vm.participants) { p ->
                val isActive = p.id == vm.activeId
                Column(Modifier.clickable { vm.activeId = p.id }, horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(if (isActive) 0.dp else 6.dp))
                    Avatar(p, if (isActive) 48.dp else 36.dp, ring = isActive, badge = vm.participants.indexOf(p) + 1)
                    Text(
                        p.name,
                        fontSize = 10.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) PrimaryColor else InkColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Category chips
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cats) { c ->
                val sel = c == category
                Surface(
                    modifier = Modifier.clip(RoundedCornerShape(50)).clickable { category = c },
                    shape = RoundedCornerShape(50),
                    color = if (sel) PrimaryColor else SurfaceBg,
                    border = if (sel) null else BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Text(c, Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (sel) Color.White else InkColor)
                }
            }
        }
        Spacer(Modifier.height(10.dp))

        if (picked == null) {
            EmptyState("No items available", "This restaurant has no available items in this category.", Modifier.weight(1f))
        } else {
            Row(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                LazyColumn(Modifier.width(62.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(catItems) { m ->
                        PhotoBox(
                            "food_" + m.id,
                            Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceBg)
                                .then(if (m.id == picked.id) Modifier.border(2.dp, PrimaryColor, RoundedCornerShape(12.dp)) else Modifier)
                                .clickable { pickedId = m.id }
                        ) {
                            Text(m.emoji, Modifier.align(Alignment.Center), fontSize = 28.sp)
                        }
                    }
                }
                Surface(
                    Modifier.fillMaxSize().padding(start = 8.dp, bottom = 6.dp, top = 2.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Column(Modifier.padding(12.dp)) {
                        PhotoBox(
                            "food_" + picked.id,
                            Modifier.fillMaxWidth().aspectRatio(1.2f).clip(RoundedCornerShape(12.dp)).background(SurfaceBg)
                        ) {
                            Text(picked.emoji, Modifier.align(Alignment.Center), fontSize = 80.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(picked.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor, modifier = Modifier.weight(1f))
                            if (picked.isFasting) {
                                StatusChip("Fasting", TertiaryColor)
                            }
                        }
                        Text(picked.description, color = GrayColor, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 4.dp))
                        Spacer(Modifier.weight(1f))
                        Text(etb(picked.priceEtb), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = InkColor)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlineButton("Add to ${active.name}'s Order") { vm.add(active.id, picked.id) }
                            val q = vm.qtyOf(active.id, picked.id)
                            if (q > 0) Text("  × $q", fontWeight = FontWeight.ExtraBold, color = PrimaryColor, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Sticky Bottom CTA Bar
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val idx = vm.participants.indexOfFirst { it.id == vm.activeId }
            val next = vm.participants.getOrNull(idx + 1)
            val label = if (active.id == "me") "Confirm Your Order" else "Confirm ${active.name}'s Order"
            PrimaryButton(label, Modifier.weight(1f)) { if (next != null) vm.activeId = next.id else onCheckout() }
            Spacer(Modifier.width(10.dp))
            Surface(
                Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onCheckout() },
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.5.dp, PrimaryColor)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ShoppingCart, "Cart", tint = PrimaryColor)
                    if (vm.cartCount() > 0) {
                        Box(
                            Modifier
                                .align(Alignment.TopEnd)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(PrimaryColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${vm.cartCount()}", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/* ================= Screen 3: Checkout ================= */
@Composable
fun CheckoutScreen(vm: AppVm, onBack: () -> Unit, onPlaced: (String) -> Unit) {
    var payment by remember { mutableStateOf("Cash on delivery") }
    var showAddressModal by remember { mutableStateOf(false) }
    val slots = listOf<String?>(null, "Deliver Now (30 min)", "Lunch Break (1:15 PM)", "Evening 7:00 PM")

    val selAddr = vm.selectedAddress

    Column(Modifier.fillMaxSize().background(PageBg)) {
        ScreenHeader("Cart & Checkout", onBack)
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("DELIVERY LOCATION: ${selAddr.label.uppercase()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                            }
                            TextButton(onClick = { showAddressModal = true }) {
                                Text("Change", fontSize = 11.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(selAddr.addressText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = InkColor)
                        if (selAddr.landmark.isNotBlank()) {
                            Text("Landmark: ${selAddr.landmark}", fontSize = 11.sp, color = GrayColor)
                        }
                        Text("Area Zone: ${selAddr.areaZone}", fontSize = 11.sp, color = TertiaryColor, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = SecondaryColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery split equally (10 ETB / person)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6C3E00))
                        StatusChip("Auto-Split", SecondaryColor)
                    }
                }
            }

            item { Text("Participant Breakdown (${vm.participants.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor, modifier = Modifier.padding(bottom = 6.dp)) }
            if (vm.cart.isEmpty()) {
                item { EmptyState("Your cart is empty", "Press back and add items to continue.") }
            }

            items(vm.participants.filter { vm.subtotal(it.id) > 0 }) { p ->
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Avatar(p, 36.dp)
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(p.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = InkColor)
                                Text(p.note, fontSize = 10.sp, color = GrayColor)
                            }
                            Text(etb(vm.subtotal(p.id)), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = PrimaryColor)
                        }
                        HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEAEAEA))
                        vm.cart.filter { it.personId == p.id }.forEach { l ->
                            val m = vm.menu.first { it.id == l.itemId }
                            Row(Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceBg)
                                        .border(1.dp, Color(0xFFCFCFCF), CircleShape)
                                        .clickable { vm.remove(p.id, l.itemId) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("–", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("${l.qty}x ${m.name}", Modifier.weight(1f), fontSize = 12.sp, color = InkColor)
                                Text(etb(m.priceEtb * l.qty), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = InkColor)
                            }
                        }
                    }
                }
            }

            if (vm.cart.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    SoftCard {
                        Text("Delivery Schedule", fontSize = 11.sp, color = GrayColor, fontWeight = FontWeight.Bold)
                        LazyRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(slots) { s ->
                                val sel = vm.scheduledFor == s
                                Surface(
                                    modifier = Modifier.clip(RoundedCornerShape(50)).clickable { vm.scheduledFor = s },
                                    shape = RoundedCornerShape(50),
                                    color = if (sel) PrimaryColor else SurfaceBg,
                                    border = if (sel) null else BorderStroke(1.dp, Color(0xFFE0E0E0))
                                ) {
                                    Text(s ?: "ASAP", Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (sel) Color.White else InkColor)
                                }
                            }
                        }
                        Text("Payment Method", fontSize = 11.sp, color = GrayColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
                        Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = PrimaryColor
                            ) {
                                Text("Cash on Delivery", Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        val svc = vm.serviceFee
                        FeeRow("Delivery Fee (Split)", vm.feesTotal() - svc)
                        FeeRow("Service Fee", svc)
                    }
                }
            }
        }

        // Bottom sheet
        Surface(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Activate Equal Payment Split", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                    Switch(
                        checked = vm.splitOn,
                        onCheckedChange = { vm.splitOn = it },
                        enabled = vm.participants.size > 1,
                        colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor)
                    )
                }
                HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Color(0xFFEAEAEA))
                Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Total to Pay:", Modifier.weight(1f), fontSize = 12.sp, color = GrayColor)
                    Text(etb(vm.grandTotal()), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = PrimaryColor)
                }
                PrimaryButton("Make Payment of ${etb(vm.grandTotal())}", enabled = vm.cart.isNotEmpty()) {
                    onPlaced(vm.placeOrder(payment + if (vm.splitOn) " (split)" else ""))
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

@Composable
private fun FeeRow(label: String, v: Int) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 11.sp, color = GrayColor)
        Text(etb(v), fontSize = 11.sp, color = InkColor)
    }
}
