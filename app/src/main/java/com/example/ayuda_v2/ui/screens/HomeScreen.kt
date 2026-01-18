package com.example.ayuda_v2.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ayuda_v2.ui.components.HelpItem
import com.example.ayuda_v2.ui.components.AddEditServiceDialog
import com.example.ayuda_v2.viewmodel.ServicesViewModel

private val sampleData = List(8) { index -> HelpModel(id = index.toString(), title = "Servicio #${index + 1}", subtitle = "Técnico disponible") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onItemClick: (String) -> Unit, viewModel: ServicesViewModel = viewModel()) {
    val showDialog = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(text = "¿Necesitas ayuda?", style = MaterialTheme.typography.titleLarge) })

        Surface(modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.surface) {
            LazyColumn(modifier = Modifier.padding(4.dp)) {
                items(viewModel.items) { item ->
                    HelpItem(item = item, onClick = { onItemClick(item.id) })
                }
            }
        }

        FloatingActionButton(onClick = { showDialog.value = true }, modifier = Modifier.padding(16.dp)) {
            Text(text = "+", style = MaterialTheme.typography.titleLarge)
        }
    }

    if (showDialog.value) {
        AddEditServiceDialog(onConfirm = { title, subtitle ->
            viewModel.add(title, subtitle)
            showDialog.value = false
        }, onDismiss = {
            showDialog.value = false
        })
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(onItemClick = {}, viewModel = ServicesViewModel(android.app.Application()))
}

data class HelpModel(val id: String, val title: String, val subtitle: String)
