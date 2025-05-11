@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.lab08.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lab08.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var newTaskDescription by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Tareas", fontWeight = FontWeight.Bold) }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Barra de búsqueda
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium)
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text("Buscar tareas...", color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            innerTextField()
                        }
                    }
                )

                // Entrada para nueva tarea
                BasicTextField(
                    value = newTaskDescription,
                    onValueChange = { newTaskDescription = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium)
                        ) {
                            if (newTaskDescription.isEmpty()) {
                                Text("Nueva tarea...", color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                            innerTextField()
                        }
                    }
                )

                // Botón para agregar tarea
                Button(
                    onClick = {
                        if (newTaskDescription.isNotEmpty()) {
                            viewModel.addTask(newTaskDescription)
                            newTaskDescription = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text("Agregar tarea")
                }

                // Mostrar tareas filtradas
                val filteredTasks = tasks.filter { it.description.contains(searchQuery, ignoreCase = true) }
                filteredTasks.forEach { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = task.description)
                        Row {
                            Button(onClick = { viewModel.toggleTaskCompletion(task) }) {
                                Text(if (task.isCompleted) "Completada" else "Pendiente")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { viewModel.deleteTask(task) }) {
                                Text("Eliminar")
                            }
                        }
                    }
                }

                // Botón para eliminar todas las tareas
                Button(
                    onClick = { coroutineScope.launch { viewModel.deleteAllTasks() } },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Eliminar todas las tareas")
                }
            }
        }
    )
}
