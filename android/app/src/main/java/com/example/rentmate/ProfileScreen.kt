package com.example.rentmate

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.tooling.preview.Preview
import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

// ============================================================
// RENTMATE PROFILE SCREEN
// ============================================================

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,

    // User information
    initialName: String = "",
    email: String = "",
    saId: String = "",
    initialPhone: String = "",

    // Demo password
    currentStoredPassword: String = ""
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var actualEmail by rememberSaveable { mutableStateOf(email) }


    // --------------------------------------------------------
    // EDITABLE INFORMATION
    // --------------------------------------------------------

    var name by rememberSaveable {
        mutableStateOf(initialName)
    }

    var phone by rememberSaveable {
        mutableStateOf(initialPhone)
    }

    // --------------------------------------------------------
    // --------------------------------------------------------
        // PASSWORD INFORMATION
    // --------------------------------------------------------

    var currentPassword by rememberSaveable {
        mutableStateOf("")
    }

    var newPassword by rememberSaveable {
        mutableStateOf("")
    }

    var confirmPassword by rememberSaveable {
        mutableStateOf("")
    }

    // --------------------------------------------------------
    // PREFERENCES
    // --------------------------------------------------------

    var taskAlarms by rememberSaveable {
        mutableStateOf(true)
    }

    var billReminders by rememberSaveable {
        mutableStateOf(true)
    }

    // --------------------------------------------------------
    // VALIDATION MESSAGE
    // --------------------------------------------------------

    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        try {
            val user = ApiClient.profileApi.me().user
            name = user.fullName
            phone = user.phoneNumber ?: ""
            actualEmail = user.email
        } catch (e: Exception) {
            errorMessage = "Could not load your profile. Check your connection."
        }
    }




    // --------------------------------------------------------
    // COLORS
    // --------------------------------------------------------

    val backgroundColor = Color(0xFFF4F5F7)
    val cardColor = Color.White
    val orange = Color(0xFFF56038)
    val textMain = Color(0xFF2D3142)
    val textSub = Color(0xFF8D99AE)
    val successBackground = Color(0xFFD1FAE5)
    val successText = Color(0xFF059669)
    val dangerBackground = Color(0xFFFEE2E2)
    val danger = Color(0xFFEF4444)

    // --------------------------------------------------------
    // MAIN SCREEN
    // --------------------------------------------------------

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1A1C23)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            // =================================================
            // TOP NAVIGATION
            // =================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),

                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {

                    Text(
                        text = "←",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textMain
                    )
                }

                Text(
                    text = "My Profile",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textMain
                )

                Spacer(
                    modifier = Modifier.width(36.dp)
                )
            }


            // =================================================
            // PROFILE CARD
            // =================================================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),

                shape = RoundedCornerShape(20.dp),

                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Avatar
                    val avatarLetter =
                        if (name.isNotBlank()) {
                            name.first().uppercase()
                        } else {
                            "U"
                        }

                    Surface(
                        modifier = Modifier.size(70.dp),
                        shape = CircleShape,
                        color = orange
                    ) {

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            Text(
                                text = avatarLetter,
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = name.ifBlank {
                            "User"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textMain
                    )

                    Text(
                        text = actualEmail,
                        fontSize = 11.sp,
                        color = textSub,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    // Verified badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = successBackground
                    ) {

                        Text(
                            text = "Account registered (verification not confirmed)",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = successText
                        )
                    }
                }
            }


            // =================================================
            // EDITABLE PERSONAL DETAILS
            // =================================================

            ProfileSection(
                title = "EDITABLE PERSONAL DETAILS"
            ) {

                ProfileLabel("Full Name")

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                ProfileLabel("SA Cellphone Number")

                OutlinedTextField(
                    value = phone,
                    onValueChange = { newValue ->

                        // Keep digits only
                        val digitsOnly =
                            newValue.filter {
                                it.isDigit()
                            }

                        // Maximum 10 digits
                        if (digitsOnly.length <= 10) {
                            phone = digitsOnly
                            errorMessage = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Text(
                    text = "Must be 10 digits starting with 0 (e.g. 0821234567)",
                    fontSize = 9.sp,
                    color = textSub,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }


            // =================================================
            // SECURITY & PASSWORD
            // =================================================

            ProfileSection(
                title = "SECURITY & PASSWORD"
            ) {

                ProfileLabel("Current Password")

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation =
                        PasswordVisualTransformation(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                ProfileLabel("New Password")

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation =
                        PasswordVisualTransformation(),
                    placeholder = {
                        Text("Minimum 6 characters")
                    },
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                ProfileLabel("Confirm New Password")

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation =
                        PasswordVisualTransformation(),
                    placeholder = {
                        Text("Re-enter new password")
                    },
                    shape = RoundedCornerShape(10.dp)
                )
            }


            // =================================================
            // APP PREFERENCES
            // =================================================

            ProfileSection(
                title = "APP PREFERENCES"
            ) {

                PreferenceRow(
                    title = "Task & Chore Alarms",
                    checked = taskAlarms,
                    onCheckedChange = {
                        taskAlarms = it
                    },
                    orange = orange
                )

                PreferenceRow(
                    title = "Bill Payment Reminders",
                    checked = billReminders,
                    onCheckedChange = {
                        billReminders = it
                    },
                    orange = orange
                )
            }


            // =================================================
            // VERIFIED IDENTITY
            // =================================================

            ProfileSection(
                title = "VERIFIED IDENTITY (LOCKED)"
            ) {

                ProfileLabel("SA ID Number")

                OutlinedTextField(
                    value = saId,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                ProfileLabel("Student Email")

                OutlinedTextField(
                    value = actualEmail,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }


            // =================================================
            // ERROR MESSAGE
            // =================================================

            if (errorMessage.isNotBlank()) {

                Text(
                    text = errorMessage,
                    color = danger,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        vertical = 8.dp
                    )
                )
            }


            // =================================================
            // SAVE PROFILE BUTTON
            // =================================================

            Button(
                onClick = {

                    // -----------------------------------------
                    // Validate name
                    // -----------------------------------------

                    if (name.trim().isEmpty()) {

                        errorMessage =
                            "Please enter your full name."

                        return@Button
                    }

                    // -----------------------------------------
                    // Validate South African cellphone
                    // -----------------------------------------

                    val saPhoneRegex =
                        Regex("^0[0-9]{9}$")

                    if (phone.isNotBlank() && !saPhoneRegex.matches(phone)) {

                        errorMessage =
                            "Please enter a valid 10-digit South African cellphone number."

                        return@Button
                    }

                    // -----------------------------------------
                    // Password change
                    // -----------------------------------------

                    if (
                        currentPassword.isNotEmpty() ||
                        newPassword.isNotEmpty() ||
                        confirmPassword.isNotEmpty()
                    ) {

                        if (currentPassword.isBlank()) {
                            errorMessage = "Enter your current password."
                            return@Button
                        }
                        if (newPassword.length < 8) {
                            errorMessage = "New password must be at least 8 characters."
                            return@Button
                        }

                        if (
                            newPassword !=
                            confirmPassword
                        ) {

                            errorMessage =
                                "New passwords do not match."

                            return@Button
                        }
                    }

                    // -----------------------------------------
                    scope.launch {
                        try {
                            ApiClient.profileApi.update(ProfileUpdate(name.trim(), phone.trim()))
                            if (newPassword.isNotEmpty()) {
                                ApiClient.profileApi.changePassword(PasswordUpdate(currentPassword, newPassword))
                            }
                            context.getSharedPreferences("rentmate_prefs", Context.MODE_PRIVATE)
                                .edit().putString("current_user", name.trim()).apply()
                            errorMessage = ""
                            currentPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            errorMessage = "Could not save profile: ${e.message ?: "Please try again"}"
                        }
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                shape = RoundedCornerShape(12.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = orange,
                    contentColor = Color.White
                )
            ) {

                Text(
                    text = "Save Profile Changes",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }


            // =================================================
            // LOGOUT BUTTON
            // =================================================

            Button(
                onClick = {

                    Toast.makeText(
                        context,
                        "Logging out...",
                        Toast.LENGTH_SHORT
                    ).show()

                    onLogoutClick()
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .height(50.dp),

                shape = RoundedCornerShape(12.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = dangerBackground,
                    contentColor = danger
                )
            ) {

                Text(
                    text = "Log Out",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}


// ============================================================
// PROFILE SECTION
// ============================================================

@Composable
fun ProfileSection(
    title: String,
    content: @Composable () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF8D99AE),
                modifier = Modifier.padding(
                    bottom = 12.dp
                )
            )

            content()
        }
    }
}


// ============================================================
// FORM LABEL
// ============================================================

@Composable
fun ProfileLabel(
    text: String
) {

    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2D3142),
        modifier = Modifier.padding(
            bottom = 4.dp
        )
    )
}


// ============================================================
// PREFERENCE ROW
// ============================================================

@Composable
fun PreferenceRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    orange: Color
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3142)
        )

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}


// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = true,
    widthDp = 410,
    heightDp = 840
)
@Composable
fun ProfileScreenPreview() {

    ProfileScreen(
        onBackClick = {},
        onLogoutClick = {},

        initialName = "Thabo Mokoena",
        email = "student@myuct.ac.za",
        saId = "0105125800084",
        initialPhone = "0821234567"
    )
}



