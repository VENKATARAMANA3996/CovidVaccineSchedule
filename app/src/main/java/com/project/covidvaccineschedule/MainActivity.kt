package com.project.covidvaccineschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.covidvaccineschedule.ui.theme.CovidVaccineScheduleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CovidVaccineScheduleTheme {
                App()
            }
        }
    }
}

@Composable
fun App(){
    val navController =  rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable(route = "login" ){
            LoginScreen(navController)
        }
        composable(route = "register" ){
            RegisterScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController){

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00a57a)),
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, start = 0.dp, end = 0.dp)
            .verticalScroll(rememberScrollState())
            .background(
                Color(0xFFf8f8f8),
                shape = RoundedCornerShape(
                    topStart = 40.dp,
                    topEnd = 40.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
            .padding(20.dp)
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.Center) {

                Text(
                    text = "Log in",
                    color = Color(0xFF00a57a),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                Text(
                    text = " to your account",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

            }

            Column(
                modifier = Modifier.padding(top = 30.dp)
            ) {
                Text(
                    text = "Enter your email address",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedBorderColor = Color(0xFF00a57a),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Text(
                    text = "Enter your password",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 15.dp)
                )

                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    placeholder = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedBorderColor = Color(0xFF00a57a),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00a57a) 
                    )
                ) {
                    Text(
                        text = "Login",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.Center) {

                    Text(
                        text = "Already have an account?",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,

                    )
                    Text(
                        text = " Sign up",
                        color = Color(0xFF00a57a),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable{
                            navController.navigate("register")
                        }
                    )

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun RegisterScreen(){
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00a57a)),
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, start = 0.dp, end = 0.dp)
            .verticalScroll(rememberScrollState())
            .background(
                Color(0xFFf8f8f8),
                shape = RoundedCornerShape(
                    topStart = 40.dp,
                    topEnd = 40.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
            .padding(20.dp)
        ) {
            Column {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.Center) {

                    Text(
                        text = "Create",
                        color = Color(0xFF00a57a),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = " your new account",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                }

                Column(
                    modifier = Modifier.padding(top = 30.dp)
                ) {
                    Text(
                        text = "Username",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        textStyle = TextStyle.Default.copy(color = Color.Black),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White,
                            focusedBorderColor = Color(0xFF00a57a), // Customize focused border color if needed
                            unfocusedBorderColor = Color.LightGray // Customize unfocused border color if needed
                        ),
                        singleLine = true, // Set to false if multiline input is needed
                        shape = RoundedCornerShape(8.dp)
                    )


                    Text(
                        text = "Email address",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White,
                            focusedBorderColor = Color(0xFF00a57a), // Customize focused border color if needed
                            unfocusedBorderColor = Color.LightGray // Customize unfocused border color if needed
                        ),
                        singleLine = true, // Set to false if multiline input is needed
                        shape = RoundedCornerShape(8.dp)
                    )

                    Text(
                        text = "Password",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 15.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White,
                            focusedBorderColor = Color(0xFF00a57a), // Customize focused border color if needed
                            unfocusedBorderColor = Color.LightGray // Customize unfocused border color if needed
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true, // Set to false if multiline input is needed
                        shape = RoundedCornerShape(8.dp)
                    )
                    Text(
                        text = "Confirm Password",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 15.dp)
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = { Text("Confirm Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White,
                            focusedBorderColor = Color(0xFF00a57a), // Customize focused border color if needed
                            unfocusedBorderColor = Color.LightGray // Customize unfocused border color if needed
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true, // Set to false if multiline input is needed
                        shape = RoundedCornerShape(8.dp)
                    )


                    Button(
                        onClick = {

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00a57a) // Green color for the button background
                        )
                    ) {
                        Text(
                            text = "Register",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}