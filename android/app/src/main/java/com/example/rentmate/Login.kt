package com.example.rentmate

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
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

private val LoginBackground = Color(0xFFF4F5F7)
private val LoginOrange = Color(0xFFF56038)
private val LoginTextMain = Color(0xFF2D3142)
private val LoginTextSub = Color(0xFF8D99AE)
private val LoginSuccess = Color(0xFF10B981)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var emailError by remember {
        mutableStateOf(false)
    }

    var passwordError by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var loginError by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginBackground)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ------------------------------------------------
        // BRAND ICON
        // ------------------------------------------------

        Text(
            text = "🏠",
            fontSize = 38.sp,
            modifier = Modifier
                .background(
                    color = LoginOrange,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // ------------------------------------------------
        // HEADER
        // ------------------------------------------------

        Text(
            text = "Welcome Back",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = LoginTextMain
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Log in to manage rent, bills, and chores",
            fontSize = 12.sp,
            color = LoginTextSub,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // ------------------------------------------------
        // FORM
        // ------------------------------------------------

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {

            // EMAIL

            Text(
                text = "Email Address",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LoginTextMain
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                    loginError = null
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "student@myuct.ac.za",
                        fontSize = 12.sp,
                        color = LoginTextSub
                    )
                },
                isError = emailError,
                shape = RoundedCornerShape(10.dp)
            )

            if (emailError) {

                Text(
                    text = "Please enter a valid email address",
                    color = Color.Red,
                    fontSize = 11.sp
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // PASSWORD

            Text(
                text = "Password",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LoginTextMain
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                    loginError = null
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "••••••••",
                        fontSize = 12.sp,
                        color = LoginTextSub
                    )
                },
                visualTransformation =
                    if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                trailingIcon = {

                    TextButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {

                        Text(
                            text = if (passwordVisible) {
                                "Hide"
                            } else {
                                "Show"
                            },
                            color = LoginOrange,
                            fontSize = 11.sp
                        )
                    }
                },
                isError = passwordError,
                shape = RoundedCornerShape(10.dp)
            )

            if (passwordError) {

                Text(
                    text = "Password is required",
                    color = Color.Red,
                    fontSize = 11.sp
                )
            }

            if (loginError != null) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = loginError ?: "",
                    color = Color.Red,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // LOGIN BUTTON

            Button(
                onClick = {

                    emailError =
                        email.isBlank() ||
                                !android.util.Patterns.EMAIL_ADDRESS
                                    .matcher(email)
                                    .matches()

                    passwordError =
                        password.isBlank()

                    if (!emailError && !passwordError) {

                        isLoading = true
                        loginError = null

                        scope.launch {
                            try {
                                val response = ApiClient.authApi.login(
                                    LoginRequest(
                                        email = email,
                                        password = password
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
                                onLoginSuccess()

                            } catch (e: Exception) {
                                isLoading = false
                                loginError = "Login failed. Check your email and password."
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LoginOrange
                ),
                shape = RoundedCornerShape(12.dp)
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = Color.White
                    )

                } else {

                    Text(
                        text = "Log In & Go to Dashboard",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ------------------------------------------------
        // SIGN UP
        // ------------------------------------------------

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Don't have an account?",
                color = LoginTextSub,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            TextButton(
                onClick = onSignUpClick
            ) {

                Text(
                    text = "Sign Up & Verify",
                    color = LoginOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // ------------------------------------------------
        // SECURITY BADGE
        // ------------------------------------------------

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = "🛡",
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = "Student FICA & ID Verified Portal",
                color = LoginTextSub,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(
    name = "RentMate Login",
    showBackground = true,
    showSystemUi = false,
    widthDp = 410,
    heightDp = 840
)
@Composable
fun LoginScreenPreview() {

    LoginScreen()
}