package com.example.rentmate

import android.content.Context
import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val Orange = Color(0xFFF56038)
private val Pink = Color(0xFFEC4899)
private val TextMain = Color(0xFF2D3142)
private val TextSub = Color(0xFF8D99AE)
private val BackgroundLight = Color(0xFFF4F5F7)
private val CardBackground = Color.White

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var accountType by remember {
        mutableStateOf("University Student")
    }

    var studentNumber by remember { mutableStateOf("") }
    var emergencyName by remember { mutableStateOf("") }
    var emergencyPhone by remember { mutableStateOf("") }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var agreedToTerms by remember { mutableStateOf(false) }

    var proofOfRegistration by remember {
        mutableStateOf<Uri?>(null)
    }

    var idDocument by remember {
        mutableStateOf<Uri?>(null)
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var signUpError by remember {
        mutableStateOf<String?>(null)
    }

    val proofPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            proofOfRegistration = uri
        }

    val idPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            idDocument = uri
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "RentMate",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Orange
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Create your account",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Find your perfect housemate and make renting easier.",
            fontSize = 14.sp,
            color = TextSub
        )

        Spacer(modifier = Modifier.height(25.dp))

        // PERSONAL DETAILS
        SignUpSection(
            title = "Personal Details"
        ) {

            SignUpTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                },
                label = "Full Names",
                placeholder = "Enter your full name"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SignUpTextField(
                value = idNumber,
                onValueChange = {
                    if (it.length <= 13 && it.all(Char::isDigit)) {
                        idNumber = it
                    }
                },
                label = "South African ID Number",
                placeholder = "13 digit ID number"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SignUpTextField(
                value = phoneNumber,
                onValueChange = {
                    if (it.length <= 10 && it.all(Char::isDigit)) {
                        phoneNumber = it
                    }
                },
                label = "Phone Number",
                placeholder = "e.g. 0821234567"
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Account Type",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = accountType == "University Student",
                    onClick = {
                        accountType = "University Student"
                    }
                )

                Text(
                    text = "University Student",
                    color = TextMain
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = accountType == "Non-Student Housemate",
                    onClick = {
                        accountType = "Non-Student Housemate"
                    }
                )

                Text(
                    text = "Non-Student Housemate",
                    color = TextMain
                )
            }
        }

        // STUDENT DETAILS
        if (accountType == "University Student") {

            SignUpSection(
                title = "Academic Details"
            ) {

                SignUpTextField(
                    value = studentNumber,
                    onValueChange = {
                        if (it.length <= 9 && it.all(Char::isDigit)) {
                            studentNumber = it
                        }
                    },
                    label = "Student Number",
                    placeholder = "9 digit student number"
                )

                Spacer(modifier = Modifier.height(12.dp))

                SignUpTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    label = "Official Student Email",
                    placeholder = "Enter your student email"
                )
            }

        } else {

            SignUpSection(
                title = "Account Details"
            ) {

                SignUpTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    label = "Personal Email",
                    placeholder = "Enter your email"
                )
            }
        }

        // EMERGENCY CONTACT
        SignUpSection(
            title = "Emergency Contact"
        ) {

            SignUpTextField(
                value = emergencyName,
                onValueChange = {
                    emergencyName = it
                },
                label = "Emergency Contact Name",
                placeholder = "Full name"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SignUpTextField(
                value = emergencyPhone,
                onValueChange = {
                    if (it.length <= 10 && it.all(Char::isDigit)) {
                        emergencyPhone = it
                    }
                },
                label = "Emergency Contact Phone",
                placeholder = "e.g. 0821234567"
            )
        }

        // ACCOUNT CREDENTIALS
        SignUpSection(
            title = "Account Credentials"
        ) {

            SignUpTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = "Password",
                placeholder = "Create a strong password",
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = {
                    passwordVisible = !passwordVisible
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Password must contain:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )

            PasswordRule(
                text = "At least 8 characters",
                valid = password.length >= 8
            )

            PasswordRule(
                text = "Uppercase letter",
                valid = password.any { it.isUpperCase() }
            )

            PasswordRule(
                text = "Lowercase letter",
                valid = password.any { it.isLowerCase() }
            )

            PasswordRule(
                text = "Number",
                valid = password.any { it.isDigit() }
            )

            PasswordRule(
                text = "Special character",
                valid = password.any {
                    "@$!%*?&".contains(it)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SignUpTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                },
                label = "Confirm Password",
                placeholder = "Re-enter your password",
                isPassword = true
            )

            if (
                confirmPassword.isNotEmpty() &&
                password != confirmPassword
            ) {

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Passwords do not match",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }

        // DOCUMENT UPLOADS
        if (accountType == "University Student") {

            SignUpSection(
                title = "Verification Uploads"
            ) {

                Text(
                    text = "Proof of Registration (PDF / JPG)",
                    color = TextMain,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                DocumentUploadBox(
                    title = "Upload Proof of Registration",
                    subtitle = "Official current academic year document",
                    fileName = getFileName(proofOfRegistration),
                    onClick = {

                        proofPicker.launch(
                            arrayOf(
                                "application/pdf",
                                "image/*"
                            )
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = "ID Document / Smart Card Copy",
                    color = TextMain,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                DocumentUploadBox(
                    title = "Upload Certified ID / Passport",
                    subtitle = "Clear scan or photograph",
                    fileName = getFileName(idDocument),
                    onClick = {

                        idPicker.launch(
                            arrayOf(
                                "application/pdf",
                                "image/*"
                            )
                        )
                    }
                )
            }
        }

        // TERMS
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = agreedToTerms,
                onCheckedChange = {
                    agreedToTerms = it
                }
            )

            Text(
                text = "I agree to the Terms & Conditions",
                color = TextMain,
                fontSize = 13.sp
            )
        }

        if (signUpError != null) {

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = signUpError ?: "",
                color = Color.Red,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // REGISTER BUTTON
        Button(
            onClick = {

                if (validateAndRegister(
                        fullName = fullName,
                        idNumber = idNumber,
                        phoneNumber = phoneNumber,
                        accountType = accountType,
                        studentNumber = studentNumber,
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        emergencyName = emergencyName,
                        emergencyPhone = emergencyPhone,
                        agreedToTerms = agreedToTerms,
                        proofOfRegistration = proofOfRegistration,
                        idDocument = idDocument
                    )
                ) {

                    isLoading = true
                    signUpError = null

                    scope.launch {
                        try {
                            val response = ApiClient.authApi.signUp(
                                SignUpRequest(
                                    fullName = fullName,
                                    email = email,
                                    password = password,
                                    idNumber = idNumber,
                                    phoneNumber = phoneNumber,
                                    accountType = accountType,
                                    studentNumber = if (accountType == "University Student") studentNumber else null,
                                    emergencyName = emergencyName,
                                    emergencyPhone = emergencyPhone
                                )
                            )

                            val prefs = context.getSharedPreferences(
                                "rentmate_prefs",
                                Context.MODE_PRIVATE
                            )

                            prefs.edit()
                                .putString("auth_token", response.token)
                                .putString("current_user", response.user.fullName)
                                .putString("current_user_id", response.user.id)
                                .apply()

                            isLoading = false
                            onSignUpSuccess()

                        } catch (e: Exception) {
                            isLoading = false
                            signUpError = "Sign up failed. That email or ID number may already be registered."
                        }
                    }

                } else {

                    signUpError = "Please check all fields are filled in correctly."
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Orange
            ),
            shape = RoundedCornerShape(14.dp)
        ) {

            if (isLoading) {

                CircularProgressIndicator(
                    modifier = Modifier.height(22.dp),
                    color = Color.White
                )

            } else {

                Text(
                    text = "Create Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Already registered?",
                color = TextSub,
                fontSize = 13.sp
            )

            TextButton(
                onClick = onLoginClick
            ) {

                Text(
                    text = "Log In",
                    color = Orange,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun SignUpSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        HorizontalDivider()

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        content()
    }
}

@Composable
fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChange: (() -> Unit)? = null
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = label,
            color = TextMain,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextSub
                )
            },
            singleLine = true,
            visualTransformation =
                if (isPassword && !passwordVisible) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
            trailingIcon = {

                if (isPassword && onPasswordVisibilityChange != null) {

                    TextButton(
                        onClick = onPasswordVisibilityChange
                    ) {

                        Text(
                            text = if (passwordVisible) {
                                "Hide"
                            } else {
                                "Show"
                            },
                            color = Orange,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun PasswordRule(
    text: String,
    valid: Boolean
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = if (valid) "✓" else "○",
            color = if (valid) {
                Color(0xFF168A4A)
            } else {
                TextSub
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.width(6.dp)
        )

        Text(
            text = text,
            color = TextSub,
            fontSize = 11.sp
        )
    }
}

@Composable
fun DocumentUploadBox(
    title: String,
    subtitle: String,
    fileName: String?,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = Orange
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onClick()
            }
            .padding(15.dp)
    ) {

        Column {

            Text(
                text = "📄  $title",
                color = Orange,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = subtitle,
                color = TextSub,
                fontSize = 11.sp
            )

            if (!fileName.isNullOrEmpty()) {

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Selected: $fileName",
                    color = Color(0xFF168A4A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun getFileName(uri: Uri?): String? {

    return uri?.lastPathSegment
}

fun validateSouthAfricanId(
    id: String
): Boolean {

    return id.length == 13 &&
            id.all { it.isDigit() }
}

fun validateAndRegister(
    fullName: String,
    idNumber: String,
    phoneNumber: String,
    accountType: String,
    studentNumber: String,
    email: String,
    password: String,
    confirmPassword: String,
    emergencyName: String,
    emergencyPhone: String,
    agreedToTerms: Boolean,
    proofOfRegistration: Uri?,
    idDocument: Uri?
): Boolean {

    if (fullName.isBlank()) {
        return false
    }

    if (!validateSouthAfricanId(idNumber)) {
        return false
    }

    if (
        phoneNumber.length != 10 ||
        !phoneNumber.startsWith("0")
    ) {
        return false
    }

    if (emergencyName.isBlank()) {
        return false
    }

    if (
        emergencyPhone.length != 10 ||
        !emergencyPhone.startsWith("0")
    ) {
        return false
    }

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return false
    }

    if (
        accountType == "University Student" &&
        (
                studentNumber.length != 9 ||
                        !studentNumber.all { it.isDigit() }
                )
    ) {
        return false
    }

    if (password.length < 8) {
        return false
    }

    if (!password.any { it.isUpperCase() }) {
        return false
    }

    if (!password.any { it.isLowerCase() }) {
        return false
    }

    if (!password.any { it.isDigit() }) {
        return false
    }

    if (!password.any { "@$!%*?&".contains(it) }) {
        return false
    }

    if (password != confirmPassword) {
        return false
    }

    if (
        accountType == "University Student" &&
        (
                proofOfRegistration == null ||
                        idDocument == null
                )
    ) {
        return false
    }

    if (!agreedToTerms) {
        return false
    }

    return true
}

@Preview(
    name = "RentMate Sign Up",
    showBackground = true,
    showSystemUi = false,
    widthDp = 410,
    heightDp = 840
)
@Composable
fun SignUpScreenPreview() {
    MaterialTheme {
        SignUpScreen()
    }
}