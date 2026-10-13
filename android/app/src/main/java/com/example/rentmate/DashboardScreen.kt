package com.example.rentmate

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BgDark = Color(0xFF2D3142)
private val BgLight = Color(0xFFF4F5F7)
private val CardGrey = Color(0xFF4F5D75)
private val Orange = Color(0xFFF56038)
private val Pink = Color(0xFFEC4899)
private val TextSub = Color(0xFF8D99AE)
private val Success = Color(0xFF10B981)
private val SAGreen = Color(0xFF007A3D)
private val SAGold = Color(0xFFFFB81C)
private val ErrorRed = Color(0xFFDC2626)

data class ChoreItem(
    val id: String,
    val title: String,
    val assignedTo: String,
    val time: String,
    val done: Boolean,
    val status: String = "OPEN"
)

data class MealItem(
    val id: String,
    val day: String,
    val meal: String,
    val cook: String,
    val time: String
)

data class GroceryItem(
    val id: String,
    val name: String,
    val quantity: String,
    val buyer: String,
    val bought: Boolean
)

data class BillItem(
    val id: String,
    val billId: String,
    val name: String,
    val cost: String,
    val split: String,
    val paid: Boolean
)

data class ScheduleItem(
    val id: String,
    val event: String,
    val person: String,
    val time: String
)

data class LoadsheddingInfo(
    val status: String = "Stage 2 Active",
    val nextSlot: String = "20:00 - 22:30"
)

@Composable
fun DashboardScreen(
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {

    val context = LocalContext.current

    val prefs = remember {
        context.getSharedPreferences(
            "rentmate_prefs",
            Context.MODE_PRIVATE
        )
    }

    val currentUser = remember {
        prefs.getString(
            "current_user",
            null
        ) ?: "Maloba"
    }

    val scope = rememberCoroutineScope()

    var currentHouseholdId by remember {
        mutableStateOf(
            prefs.getString("current_household_id", null)
        )
    }

    var showHouseholdSetup by remember {
        mutableStateOf(currentHouseholdId == null)
    }

    var selectedCategory by remember {
        mutableStateOf<String?>(null)
    }

    var paymentBill by remember {
        mutableStateOf<BillItem?>(null)
    }

    var note by remember {
        mutableStateOf(
            prefs.getString(
                "dashboard_note",
                "Remember to keep the house clean and respect your housemates."
            )
        )
    }

    var editingNote by remember {
        mutableStateOf(false)
    }

    var familyMode by remember {
        mutableStateOf(false)
    }

    // Settings state
    var settingsVisible by remember {
        mutableStateOf(false)
    }

    var notificationsEnabled by remember {
        mutableStateOf(true)
    }

    var soundEnabled by remember {
        mutableStateOf(true)
    }

    val chores = remember {
        mutableStateListOf<ChoreItem>()
    }

    fun refreshChores() {
        val householdId = currentHouseholdId ?: return

        scope.launch {
            try {
                val response = ApiClient.choreApi.getChores(householdId)

                chores.clear()
                chores.addAll(
                    response.chores.map { dto ->
                        ChoreItem(
                            id = dto.id,
                            title = dto.title,
                            assignedTo = dto.claimedBy?.fullName ?: "Unclaimed",
                            time = dto.dueDate ?: "--",
                            done = dto.status == "DONE",
                            status = dto.status
                        )
                    }
                )
            } catch (e: Exception) {
                // Chores failed to load; list just stays empty for now.
            }
        }
    }

    LaunchedEffect(currentHouseholdId) {
        refreshChores()
    }

    val meals = remember {
        mutableStateListOf<MealItem>()
    }

    fun refreshMeals() {
        val householdId = currentHouseholdId ?: return

        scope.launch {
            try {
                val response = ApiClient.mealApi.getMeals(householdId)
                meals.clear()
                meals.addAll(
                    response.meals.map { dto ->
                        MealItem(
                            id = dto.id,
                            day = dto.day,
                            meal = dto.meal,
                            cook = dto.cook,
                            time = dto.time
                        )
                    }
                )
            } catch (e: Exception) {
                // Meals failed to load; list just stays empty for now.
            }
        }
    }

    LaunchedEffect(currentHouseholdId) {
        refreshMeals()
    }

    val groceries = remember {
        mutableStateListOf<GroceryItem>()
    }

    fun refreshGroceries() {
        val householdId = currentHouseholdId ?: return

        scope.launch {
            try {
                val response = ApiClient.groceryApi.getGroceries(householdId)

                groceries.clear()
                groceries.addAll(
                    response.groceries.map { dto ->
                        GroceryItem(
                            id = dto.id,
                            name = dto.name,
                            quantity = dto.quantity,
                            buyer = dto.buyer,
                            bought = dto.bought
                        )
                    }
                )
            } catch (e: Exception) {
                // Groceries failed to load; list just stays empty for now.
            }
        }
    }

    LaunchedEffect(currentHouseholdId) {
        refreshGroceries()
    }

    val bills = remember {
        mutableStateListOf<BillItem>()
    }

    fun refreshBills() {
        val householdId = currentHouseholdId ?: return

        scope.launch {
            try {
                val response = ApiClient.billApi.getBills(householdId)
                bills.clear()
                bills.addAll(
                    response.bills.map { dto ->
                        BillItem(
                            id = dto.yourShare.id,
                            billId = dto.id,
                            name = dto.name,
                            cost = "R${dto.totalAmount}",
                            split = "R${dto.yourShare.amount}",
                            paid = dto.yourShare.paid
                        )
                    }
                )
            } catch (e: Exception) {
                // Bills failed to load; list just stays empty for now.
            }
        }
    }

    LaunchedEffect(currentHouseholdId) {
        refreshBills()
    }

    val schedules = remember {
        mutableStateListOf<ScheduleItem>()
    }

    fun refreshSchedules() {
        val householdId = currentHouseholdId ?: return

        scope.launch {
            try {
                val response = ApiClient.scheduleApi.getEvents(householdId)
                schedules.clear()
                schedules.addAll(
                    response.events.map { dto ->
                        ScheduleItem(
                            id = dto.id,
                            event = dto.event,
                            person = dto.person,
                            time = dto.time
                        )
                    }
                )
            } catch (e: Exception) {
                // Schedule failed to load; list just stays empty for now.
            }
        }
    }

    LaunchedEffect(currentHouseholdId) {
        refreshSchedules()
    }

    var loadshedding by remember {
        mutableStateOf(
            LoadsheddingInfo()
        )
    }

    val housemates = remember { mutableStateListOf<String>() }
    LaunchedEffect(currentHouseholdId) {
        housemates.clear()
        val householdId = currentHouseholdId
        if (householdId != null) {
            try {
                housemates.addAll(ApiClient.householdApi.members(householdId).members.map { it.fullName })
            } catch (_: Exception) {
                // No fabricated names are shown when the request fails.
            }
        }
    }

    val dateFormat = remember {
        SimpleDateFormat(
            "EEEE, dd MMMM yyyy",
            Locale.getDefault()
        )
    }

    val timeFormat = remember {
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        )
    }

    var currentTime by remember {
        mutableStateOf(
            timeFormat.format(Date())
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = timeFormat.format(Date())
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxSize(0.96f),
            color = BgLight,
            shape = RoundedCornerShape(24.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                // =================================================
                // TOP BAR
                // =================================================

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgDark)
                        .padding(
                            horizontal = 18.dp,
                            vertical = 14.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = dateFormat.format(Date()),
                            color = Color.White,
                            fontSize = 12.sp
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "Welcome Back, $currentUser 👋",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = currentTime,
                            color = SAGold,
                            fontSize = 13.sp
                        )
                    }

                    IconButton(
                        onClick = {
                            selectedCategory = "Alarms"
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Alarms",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = onProfileClick
                    ) {

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }

                    // SETTINGS NOW WORKS
                    IconButton(
                        onClick = {
                            settingsVisible = true
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                }

                // =================================================
                // DASHBOARD CONTENT
                // =================================================

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    item {

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Housemates",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "${housemates.size} ${if (housemates.size == 1) "person" else "people"}",
                                color = TextSub,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            housemates
                                .take(4)
                                .forEach { person ->

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(
                                                RoundedCornerShape(12.dp)
                                            )
                                            .background(Color.White)
                                            .padding(8.dp),
                                        contentAlignment =
                                            Alignment.Center
                                    ) {

                                        Text(
                                            text = person
                                                .split(" ")
                                                .first(),
                                            fontSize = 11.sp,
                                            fontWeight =
                                                FontWeight.Medium
                                        )
                                    }
                                }
                        }
                    }

                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            ModeButton(
                                text = "Student Commune",
                                selected = !familyMode,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    familyMode = false
                                }
                            )

                            ModeButton(
                                text = "Family Home",
                                selected = familyMode,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    familyMode = true
                                }
                            )
                        }
                    }

                    item {

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {

                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Text(
                                    text = "📌",
                                    fontSize = 23.sp
                                )

                                Spacer(
                                    modifier = Modifier.width(10.dp)
                                )

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = "Pinned Note",
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = note ?: "",
                                        color = TextSub,
                                        fontSize = 12.sp
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        editingNote = true
                                    }
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.Edit,
                                        contentDescription =
                                            "Edit note"
                                    )
                                }
                            }
                        }
                    }

                    item {

                        Text(
                            text = "RentMate Dashboard",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text =
                                "Tap a section to manage your shared home.",
                            color = TextSub,
                            fontSize = 12.sp
                        )
                    }

                    // =================================================
                    // CIRCULAR SECTION 1
                    // =================================================

                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            CircularDashboardItem(
                                title = "Commune Chores",
                                icon = "🧹",
                                badge =
                                    chores.count {
                                        !it.done
                                    }.toString(),
                                background = CardGrey,
                                onClick = {
                                    selectedCategory = "Chores"
                                }
                            )

                            CircularDashboardItem(
                                title = "Kitchen & Braai",
                                icon = "🔥",
                                badge =
                                    meals.size.toString(),
                                background = Pink,
                                onClick = {
                                    selectedCategory = "Meals"
                                }
                            )
                        }
                    }

                    // =================================================
                    // CIRCULAR SECTION 2
                    // =================================================

                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            CircularDashboardItem(
                                title = "Shared Groceries",
                                icon = "🛒",
                                badge =
                                    groceries.count {
                                        !it.bought
                                    }.toString(),
                                background = Orange,
                                onClick = {
                                    selectedCategory =
                                        "Groceries"
                                }
                            )

                            CircularDashboardItem(
                                title = "Rent & Bills",
                                icon = "💰",
                                badge =
                                    bills.count {
                                        !it.paid
                                    }.toString(),
                                background = Pink,
                                onClick = {
                                    selectedCategory = "Bills"
                                }
                            )
                        }
                    }

                    // =================================================
                    // CIRCULAR SECTION 3
                    // =================================================

                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            CircularDashboardItem(
                                title = "Campus & Tasks",
                                icon = "🎓",
                                badge =
                                    schedules.size.toString(),
                                background = Orange,
                                onClick = {
                                    selectedCategory =
                                        "Schedule"
                                }
                            )

                            CircularDashboardItem(
                                title = "Load-shedding",
                                icon = "⚡",
                                badge = "!",
                                background = CardGrey,
                                onClick = {
                                    selectedCategory =
                                        "Loadshedding"
                                }
                            )
                        }
                    }

                    item {

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {

                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = SAGold
                                )

                                Spacer(
                                    modifier = Modifier.width(10.dp)
                                )

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text =
                                            loadshedding.status,
                                        fontWeight =
                                            FontWeight.Bold
                                    )

                                    Text(
                                        text =
                                            "Next slot: ${loadshedding.nextSlot}",
                                        color = TextSub,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    item {

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        OutlinedButton(
                            onClick = onLogoutClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text("Logout")
                        }

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )
                    }
                }
            }
        }
    }

    // =============================================================
    // CHORES
    // =============================================================

    when (selectedCategory) {

        "Chores" -> {

            ChoresDialog(
                chores = chores,

                onAdd = { title, assignedTo, time ->

                    val householdId = currentHouseholdId

                    if (householdId != null) {

                        scope.launch {
                            try {
                                ApiClient.choreApi.createChore(
                                    CreateChoreRequest(
                                        householdId = householdId,
                                        title = title,
                                        frequency = "ONE_OFF"
                                    )
                                )
                                refreshChores()
                            } catch (e: Exception) {
                                // Creation failed; chore just won't appear.
                            }
                        }

                        if (time.isNotBlank()) {

                            AlarmScheduler.scheduleAlarm(
                                context,
                                System.currentTimeMillis(),
                                "Chore: $title",
                                time
                            )
                        }
                    }
                },

                onToggle = { chore ->

                    scope.launch {
                        try {
                            if (chore.status == "OPEN") {
                                ApiClient.choreApi.claimChore(chore.id)
                            } else if (chore.status == "CLAIMED") {
                                ApiClient.choreApi.completeChore(chore.id)
                            }
                            refreshChores()
                        } catch (e: Exception) {
                            // Claim/complete failed; state just won't change.
                        }
                    }
                },

                onDelete = { chore ->
                    // Deleting isn't supported by the backend yet.
                    // Left as a no-op for now.
                },

                onDismiss = {
                    selectedCategory = null
                }
            )
        }

        // =========================================================
        // MEALS
        // =========================================================

        "Meals" -> {

            MealsDialog(
                meals = meals,

                onAdd = { day, meal, cook, time ->

                    val householdId = currentHouseholdId

                    if (householdId != null) {

                        scope.launch {
                            try {
                                ApiClient.mealApi.createMeal(
                                    CreateMealRequest(
                                        householdId = householdId,
                                        day = day.ifBlank { null },
                                        meal = meal,
                                        cook = cook.ifBlank { null },
                                        time = time.ifBlank { null }
                                    )
                                )
                                refreshMeals()
                            } catch (e: Exception) {
                                // Creation failed; meal just won't appear.
                            }
                        }

                        if (time.isNotBlank()) {

                            AlarmScheduler.scheduleAlarm(
                                context,
                                System.currentTimeMillis(),
                                "Meal: $meal",
                                time
                            )
                        }
                    }
                },

                onDelete = { meal ->

                    scope.launch {
                        try {
                            ApiClient.mealApi.deleteMeal(meal.id)
                            refreshMeals()
                        } catch (e: Exception) {
                            // Delete failed; item just stays in the list.
                        }
                    }
                },

                onDismiss = {
                    selectedCategory = null
                }
            )
        }

        // =========================================================
        // GROCERIES
        // =========================================================

        "Groceries" -> {

            GroceriesDialog(
                groceries = groceries,

                onAdd = {
                        name,
                        quantity,
                        buyer ->

                    val householdId = currentHouseholdId

                    if (householdId != null) {

                        scope.launch {
                            try {
                                ApiClient.groceryApi.createGrocery(
                                    CreateGroceryRequest(
                                        householdId = householdId,
                                        name = name,
                                        quantity = quantity.ifBlank { null },
                                        buyer = buyer.ifBlank { null }
                                    )
                                )
                                refreshGroceries()
                            } catch (e: Exception) {
                                // Creation failed; item just won't appear.
                            }
                        }
                    }
                },

                onToggle = { grocery ->

                    scope.launch {
                        try {
                            ApiClient.groceryApi.toggleGrocery(grocery.id)
                            refreshGroceries()
                        } catch (e: Exception) {
                            // Toggle failed; state just won't change.
                        }
                    }
                },

                onDelete = { grocery ->

                    scope.launch {
                        try {
                            ApiClient.groceryApi.deleteGrocery(grocery.id)
                            refreshGroceries()
                        } catch (e: Exception) {
                            // Delete failed; item just stays in the list.
                        }
                    }
                },

                onDismiss = {
                    selectedCategory = null
                }
            )
        }

        // =========================================================
        // BILLS
        // =========================================================

        "Bills" -> {

            BillsDialog(
                bills = bills,

                onAdd = { name, cost, _ ->

                    val householdId = currentHouseholdId
                    val amount = cost.toDoubleOrNull()

                    if (householdId != null && amount != null) {

                        scope.launch {
                            try {
                                ApiClient.billApi.createBill(
                                    CreateBillRequest(
                                        householdId = householdId,
                                        name = name,
                                        totalAmount = amount
                                    )
                                )
                                refreshBills()
                            } catch (e: Exception) {
                                // Creation failed; bill just won't appear.
                            }
                        }
                    }
                },

                onPay = { bill ->

                    selectedCategory = null
                    paymentBill = bill
                },

                onDelete = { bill ->

                    scope.launch {
                        try {
                            ApiClient.billApi.deleteBill(bill.billId)
                            refreshBills()
                        } catch (e: Exception) {
                            // Delete failed; only the bill's creator can delete it.
                        }
                    }
                },

                onDismiss = {
                    selectedCategory = null
                }
            )
        }

        // =========================================================
        // SCHEDULE
        // =========================================================

        "Schedule" -> {

            ScheduleDialog(
                schedules = schedules,

                onAdd = { event, person, time ->

                    val householdId = currentHouseholdId

                    if (householdId != null) {

                        scope.launch {
                            try {
                                ApiClient.scheduleApi.createEvent(
                                    CreateScheduleRequest(
                                        householdId = householdId,
                                        event = event,
                                        person = person.ifBlank { null },
                                        time = time.ifBlank { null }
                                    )
                                )
                                refreshSchedules()
                            } catch (e: Exception) {
                                // Creation failed; event just won't appear.
                            }
                        }

                        if (time.isNotBlank()) {

                            AlarmScheduler.scheduleAlarm(
                                context,
                                System.currentTimeMillis(),
                                "Event: $event",
                                time
                            )
                        }
                    }
                },

                onDelete = { schedule ->

                    scope.launch {
                        try {
                            ApiClient.scheduleApi.deleteEvent(schedule.id)
                            refreshSchedules()
                        } catch (e: Exception) {
                            // Delete failed; item just stays in the list.
                        }
                    }
                },

                onDismiss = {
                    selectedCategory = null
                }
            )
        }

        // =========================================================
        // LOAD-SHEDDING
        // =========================================================

        "Loadshedding" -> {

            LoadsheddingDialog(
                information = loadshedding,

                onUpdate = {
                    loadshedding = it
                },

                onDismiss = {
                    selectedCategory = null
                }
            )
        }

        // =========================================================
        // ALARMS
        // =========================================================

        "Alarms" -> {

            AlarmInfoDialog(
                onDismiss = {
                    selectedCategory = null
                }
            )
        }
    }

    // =============================================================
    // SETTINGS DIALOG
    // =============================================================

    if (settingsVisible) {

        SettingsDialog(
            notificationsEnabled = notificationsEnabled,
            soundEnabled = soundEnabled,

            onNotificationsChange = {
                notificationsEnabled = it
            },

            onSoundChange = {
                soundEnabled = it
            },

            onProfileClick = {
                settingsVisible = false
                onProfileClick()
            },

            onLogoutClick = {
                settingsVisible = false
                onLogoutClick()
            },

            onDismiss = {
                settingsVisible = false
            }
        )
    }

    // =============================================================
    // PINNED NOTE
    // =============================================================

    if (editingNote) {

        NoteDialog(
            currentNote = note ?: "",

            onSave = { newNote ->

                note = newNote

                prefs.edit()
                    .putString(
                        "dashboard_note",
                        newNote
                    )
                    .apply()

                editingNote = false
            },

            onDismiss = {
                editingNote = false
            }
        )
    }

    // =============================================================
    // PAYMENT
    // =============================================================

    paymentBill?.let { bill ->
        AlertDialog(
            onDismissRequest = { paymentBill = null },
            title = { Text("Payment instructions") },
            text = { Text("Your share is ${bill.split}. Pay your household manager using an agreed method. The manager must confirm receipt before this bill is marked paid. RentMate does not collect card details or process live payments.") },
            confirmButton = { TextButton(onClick = { paymentBill = null }) { Text("OK") } }
        )
    }

}


// =================================================================
// CIRCULAR DASHBOARD ITEM
// =================================================================

@Composable
private fun CircularDashboardItem(
    title: String,
    icon: String,
    badge: String,
    background: Color,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .width(145.dp)
            .clickable {
                onClick()
            },
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(132.dp)
                .clip(CircleShape)
                .background(background),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = icon,
                    fontSize = 29.sp
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(
                            horizontal = 8.dp,
                            vertical = 3.dp
                        )
                ) {

                    Text(
                        text = badge,
                        color = background,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )
    }
}


// =================================================================
// MODE BUTTON
// =================================================================

@Composable
private fun ModeButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor =
                if (selected) {
                    SAGreen
                } else {
                    Color.White
                },
            contentColor =
                if (selected) {
                    Color.White
                } else {
                    Color.Black
                }
        )
    ) {

        Text(
            text = text,
            fontSize = 12.sp
        )
    }
}


// =================================================================
// PAYMENT METHOD BUTTON
// =================================================================

@Composable
private fun PaymentMethodButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {

        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
        )
    }
}


// =================================================================
// SETTINGS DIALOG
// =================================================================

@Composable
private fun SettingsDialog(
    notificationsEnabled: Boolean,
    soundEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDismiss: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Settings,
                    contentDescription = null,
                    tint = SAGreen
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text = "Settings",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        },

        text = {

            Column {

                Text(
                    text = "Account",
                    fontWeight =
                        FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                OutlinedButton(
                    onClick = onProfileClick,
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Person,
                        contentDescription = null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text("Open Profile")
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text = "Notifications",
                    fontWeight =
                        FontWeight.Bold,
                    fontSize = 14.sp
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Notifications,
                        contentDescription = null,
                        tint = SAGreen
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text = "Enable notifications",
                        modifier =
                            Modifier.weight(1f)
                    )

                    Checkbox(
                        checked =
                            notificationsEnabled,
                        onCheckedChange =
                            onNotificationsChange
                    )
                }

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Notifications,
                        contentDescription = null,
                        tint = SAGold
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text = "Notification sound",
                        modifier =
                            Modifier.weight(1f)
                    )

                    Checkbox(
                        checked = soundEnabled,
                        onCheckedChange =
                            onSoundChange
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text = "Security",
                    fontWeight =
                        FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Your RentMate account and payment information should be protected with your normal device and account security.",
                    color = TextSub,
                    fontSize = 12.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text("Logout")
                }
            }
        },

        confirmButton = {

            Button(
                onClick = onDismiss,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = SAGreen
                    )
            ) {

                Text("Done")
            }
        }
    )
}


// =================================================================
// CHORES DIALOG
// =================================================================

@Composable
private fun ChoresDialog(
    chores: List<ChoreItem>,
    onAdd: (String, String, String) -> Unit,
    onToggle: (ChoreItem) -> Unit,
    onDelete: (ChoreItem) -> Unit,
    onDismiss: () -> Unit
) {

    var title by remember {
        mutableStateOf("")
    }

    var assignedTo by remember {
        mutableStateOf("")
    }

    var time by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("🧹 Commune Chores")
        },

        text = {

            Column {

                chores.forEach { chore ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),

                        colors = CardDefaults.cardColors(
                            containerColor =
                                Color(0xFFF7F7F7)
                        )
                    ) {

                        Row(
                            modifier =
                                Modifier.padding(8.dp),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked = chore.done,

                                onCheckedChange = {
                                    onToggle(chore)
                                }
                            )

                            Column(
                                modifier =
                                    Modifier.weight(1f)
                            ) {

                                Text(
                                    text = chore.title,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        "${chore.assignedTo} • ${chore.time}",
                                    fontSize = 11.sp,
                                    color = TextSub
                                )
                            }

                            IconButton(
                                onClick = {
                                    onDelete(chore)
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Delete,
                                    contentDescription =
                                        "Delete"
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = title,

                    onValueChange = {
                        title = it
                    },

                    label = {
                        Text("Chore")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                OutlinedTextField(
                    value = assignedTo,

                    onValueChange = {
                        assignedTo = it
                    },

                    label = {
                        Text("Assigned to")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                OutlinedTextField(
                    value = time,

                    onValueChange = {
                        time = it
                    },

                    label = {
                        Text("Time e.g. 18:00")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    if (title.isNotBlank()) {

                        onAdd(
                            title,
                            assignedTo,
                            time
                        )

                        title = ""
                        assignedTo = ""
                        time = ""
                    }
                }
            ) {

                Text("Add Chore")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Close")
            }
        }
    )
}


// =================================================================
// MEALS DIALOG
// =================================================================

@Composable
private fun MealsDialog(
    meals: List<MealItem>,
    onAdd: (String, String, String, String) -> Unit,
    onDelete: (MealItem) -> Unit,
    onDismiss: () -> Unit
) {

    var day by remember {
        mutableStateOf("")
    }

    var meal by remember {
        mutableStateOf("")
    }

    var cook by remember {
        mutableStateOf("")
    }

    var time by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("🔥 Kitchen & Braai")
        },

        text = {

            Column {

                meals.forEach { item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {

                        Row(
                            modifier =
                                Modifier.padding(10.dp),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Column(
                                modifier =
                                    Modifier.weight(1f)
                            ) {

                                Text(
                                    text = item.meal,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        "${item.day} • ${item.cook} • ${item.time}",
                                    fontSize = 11.sp,
                                    color = TextSub
                                )
                            }

                            IconButton(
                                onClick = {
                                    onDelete(item)
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Delete,
                                    contentDescription =
                                        "Delete"
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = day,

                    onValueChange = {
                        day = it
                    },

                    label = {
                        Text("Day")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = meal,

                    onValueChange = {
                        meal = it
                    },

                    label = {
                        Text("Meal")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = cook,

                    onValueChange = {
                        cook = it
                    },

                    label = {
                        Text("Cook")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,

                    onValueChange = {
                        time = it
                    },

                    label = {
                        Text("Time e.g. 18:30")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    if (meal.isNotBlank()) {

                        onAdd(
                            day,
                            meal,
                            cook,
                            time
                        )

                        day = ""
                        meal = ""
                        cook = ""
                        time = ""
                    }
                }
            ) {

                Text("Add Meal")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Close")
            }
        }
    )
}


// =================================================================
// GROCERIES DIALOG
// =================================================================

@Composable
private fun GroceriesDialog(
    groceries: List<GroceryItem>,
    onAdd: (String, String, String) -> Unit,
    onToggle: (GroceryItem) -> Unit,
    onDelete: (GroceryItem) -> Unit,
    onDismiss: () -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var quantity by remember {
        mutableStateOf("")
    }

    var buyer by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("🛒 Shared Groceries")
        },

        text = {

            Column {

                groceries.forEach { grocery ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = grocery.bought,

                            onCheckedChange = {
                                onToggle(grocery)
                            }
                        )

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text = grocery.name,
                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                text =
                                    "${grocery.quantity} • ${grocery.buyer}",
                                fontSize = 11.sp,
                                color = TextSub
                            )
                        }

                        IconButton(
                            onClick = {
                                onDelete(grocery)
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Delete,
                                contentDescription =
                                    "Delete"
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,

                    onValueChange = {
                        name = it
                    },

                    label = {
                        Text("Item")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = quantity,

                    onValueChange = {
                        quantity = it
                    },

                    label = {
                        Text("Quantity")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = buyer,

                    onValueChange = {
                        buyer = it
                    },

                    label = {
                        Text("Buyer")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    if (name.isNotBlank()) {

                        onAdd(
                            name,
                            quantity,
                            buyer
                        )

                        name = ""
                        quantity = ""
                        buyer = ""
                    }
                }
            ) {

                Text("Add Grocery")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Close")
            }
        }
    )
}


// =================================================================
// BILLS DIALOG
// =================================================================

@Composable
private fun BillsDialog(
    bills: List<BillItem>,
    onAdd: (String, String, String) -> Unit,
    onPay: (BillItem) -> Unit,
    onDelete: (BillItem) -> Unit,
    onDismiss: () -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var cost by remember {
        mutableStateOf("")
    }

    var split by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("💰 Rent & Bills")
        },

        text = {

            Column {

                bills.forEach { bill ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),

                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (bill.paid) {
                                    Color(0xFFE9F8EF)
                                } else {
                                    Color(0xFFFFF4ED)
                                }
                        )
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(10.dp)
                        ) {

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Column(
                                    modifier =
                                        Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = bill.name,
                                        fontWeight =
                                            FontWeight.Bold
                                    )

                                    Text(
                                        text =
                                            "${bill.cost} • Your share: ${bill.split}",
                                        fontSize = 11.sp,
                                        color = TextSub
                                    )
                                }

                                Text(
                                    text =
                                        if (bill.paid) {
                                            "PAID"
                                        } else {
                                            "DUE"
                                        },

                                    color =
                                        if (bill.paid) {
                                            Success
                                        } else {
                                            ErrorRed
                                        },

                                    fontWeight =
                                        FontWeight.Bold,

                                    fontSize = 11.sp
                                )
                            }

                            if (!bill.paid) {

                                Spacer(
                                    modifier =
                                        Modifier.height(6.dp)
                                )

                                Button(
                                    onClick = {
                                        onPay(bill)
                                    },

                                    modifier =
                                        Modifier.fillMaxWidth(),

                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor =
                                                SAGreen
                                        )
                                ) {

                                    Text(
                                        "Pay ${bill.split}"
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    onDelete(bill)
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Delete,
                                    contentDescription =
                                        "Delete"
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = name,

                    onValueChange = {
                        name = it
                    },

                    label = {
                        Text("Bill name")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = cost,

                    onValueChange = {
                        cost = it
                    },

                    label = {
                        Text("Total cost")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = split,

                    onValueChange = {
                        split = it
                    },

                    label = {
                        Text("Your share")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    if (
                        name.isNotBlank() &&
                        cost.isNotBlank() &&
                        split.isNotBlank()
                    ) {

                        onAdd(
                            name,
                            cost,
                            split
                        )

                        name = ""
                        cost = ""
                        split = ""
                    }
                }
            ) {

                Text("Add Bill")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Close")
            }
        }
    )
}


// =================================================================
// SCHEDULE DIALOG
// =================================================================

@Composable
private fun ScheduleDialog(
    schedules: List<ScheduleItem>,
    onAdd: (String, String, String) -> Unit,
    onDelete: (ScheduleItem) -> Unit,
    onDismiss: () -> Unit
) {

    var event by remember {
        mutableStateOf("")
    }

    var person by remember {
        mutableStateOf("")
    }

    var time by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("🎓 Campus & Tasks")
        },

        text = {

            Column {

                schedules.forEach { item ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Schedule,
                            contentDescription = null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text = item.event,
                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                text =
                                    "${item.person} • ${item.time}",
                                fontSize = 11.sp,
                                color = TextSub
                            )
                        }

                        IconButton(
                            onClick = {
                                onDelete(item)
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Delete,
                                contentDescription =
                                    "Delete"
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = event,

                    onValueChange = {
                        event = it
                    },

                    label = {
                        Text("Event / Task")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = person,

                    onValueChange = {
                        person = it
                    },

                    label = {
                        Text("Person")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,

                    onValueChange = {
                        time = it
                    },

                    label = {
                        Text("Time e.g. 14:00")
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    if (event.isNotBlank()) {

                        onAdd(
                            event,
                            person,
                            time
                        )

                        event = ""
                        person = ""
                        time = ""
                    }
                }
            ) {

                Text("Add Task")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Close")
            }
        }
    )
}


// =================================================================
// LOAD-SHEDDING DIALOG
// =================================================================

@Composable
private fun LoadsheddingDialog(
    information: LoadsheddingInfo,
    onUpdate: (LoadsheddingInfo) -> Unit,
    onDismiss: () -> Unit
) {

    var status by remember {
        mutableStateOf(
            information.status
        )
    }

    var nextSlot by remember {
        mutableStateOf(
            information.nextSlot
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("⚡ Load-shedding")
        },

        text = {

            Column {

                Text(
                    text = "Current status",
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                OutlinedTextField(
                    value = status,

                    onValueChange = {
                        status = it
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Next scheduled slot",
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                OutlinedTextField(
                    value = nextSlot,

                    onValueChange = {
                        nextSlot = it
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    onUpdate(
                        LoadsheddingInfo(
                            status = status,
                            nextSlot = nextSlot
                        )
                    )

                    onDismiss()
                }
            ) {

                Text("Save")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Close")
            }
        }
    )
}


// =================================================================
// NOTE DIALOG
// =================================================================

@Composable
private fun NoteDialog(
    currentNote: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {

    var note by remember {
        mutableStateOf(currentNote)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("📌 Edit Pinned Note")
        },

        text = {

            OutlinedTextField(
                value = note,

                onValueChange = {
                    note = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                minLines = 4
            )
        },

        confirmButton = {

            Button(
                onClick = {
                    onSave(note)
                }
            ) {

                Text("Save")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Cancel")
            }
        }
    )
}


// =================================================================
// ALARM INFO DIALOG
// =================================================================

@Composable
private fun AlarmInfoDialog(
    onDismiss: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("🔔 RentMate Alarms")
        },

        text = {

            Column {

                Text(
                    text =
                        "Your RentMate alarms are scheduled automatically when you add:",
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text("• Chores")
                Text("• Meals")
                Text("• Campus tasks")

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Make sure notifications are allowed on your phone.",
                    color = TextSub,
                    fontSize = 12.sp
                )
            }
        },

        confirmButton = {

            Button(
                onClick = onDismiss
            ) {

                Text("Done")
            }
        }
    )
}


// =================================================================
// PAYMENT DIALOG
// =================================================================


@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {

    MaterialTheme {

        DashboardScreen(
            onProfileClick = {},
            onSettingsClick = {},
            onLogoutClick = {}
        )
    }
}

