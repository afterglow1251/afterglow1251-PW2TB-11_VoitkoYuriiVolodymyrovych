package com.example.calculator2_

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.calculator2_.ui.theme.Calculator2_Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Calculator2_Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Main(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Main(modifier: Modifier = Modifier) {
    var inputValues by remember { mutableStateOf(mapOf<String, String>()) }
    var inputErrors by remember { mutableStateOf(mapOf<String, String>()) }
    var resultText by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }

    // Function to validate input
    fun validateInput(value: String): String {
        return when {
            value.isBlank() -> "Enter a non-negative number"
            value.toDoubleOrNull() == null -> "Enter a non-negative number"
            value.toDouble() < 0 -> "Enter a non-negative number"
            else -> ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
            verticalArrangement = Arrangement.Top
    ) {
        // Input Fields
        val inputs = listOf("Q_i_r", "a_vun", "A_r", "G_vun", "eta_z_y", "k_tv_s", "B")

        inputs.forEach { label ->
            val inputValue = inputValues[label] ?: ""
            val inputError = inputErrors[label] ?: ""

            OutlinedTextField(
                value = inputValue,
                onValueChange = { newValue ->
                    inputValues = inputValues + (label to newValue) // Update input value
                    val error = validateInput(newValue)
                    inputErrors = inputErrors + (label to error) // Update error for this specific field
                    showResult = false
                },
                label = { Text(label) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                ),
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "Enter a number", color = Color.Gray)
                },
                supportingText = {
                    if (inputError.isNotEmpty()) {
                        Text(text = inputError, color = Color.Red)
                    }
                },
            )
        }

        // Calculate button
        Button(
            onClick = {
                // Reset all error messages
                val errors = inputs.associateWith { validateInput(inputValues[it] ?: "") }
                inputErrors = errors

                // Check if there are any errors
                if (errors.values.all { it.isEmpty() }) {
                    // If no errors, perform calculations
                    val Q_i_r = inputValues["Q_i_r"]?.toDouble()
                    val a_vun = inputValues["a_vun"]?.toDouble()
                    val A_r = inputValues["A_r"]?.toDouble()
                    val G_vun = inputValues["G_vun"]?.toDouble()
                    val eta_z_y = inputValues["eta_z_y"]?.toDouble()
                    val k_tv_s = inputValues["k_tv_s"]?.toDouble()
                    val B = inputValues["B"]?.toDouble()

                    val k_tv =
                        (1e6 / Q_i_r!!) *
                                (a_vun!! * (A_r!! / (100 - G_vun!!)) * (1 - eta_z_y!!)) +
                                k_tv_s!!

                    val E_tv = 1e-6 * k_tv * Q_i_r * B!!

                    resultText = """
                    Emission index = ${"%.2f".format(k_tv)} (g/GJ)
                    Gross emission = ${"%.2f".format(E_tv)} (t)
                """.trimIndent()

                    showResult = true
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            )
        ) {
            Text(text = "Calculate")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // If no errors, display the result
        if (showResult) {
            Text(text = resultText, color = Color.Black)
        }
    }
}
