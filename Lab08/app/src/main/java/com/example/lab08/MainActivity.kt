@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.lab08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment  // Importar esto para usar Alignment.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.lab08.dao.TaskDao
import com.example.lab08.db.TaskDatabase
import com.example.lab08.model.Task
import kotlinx.coroutines.launch
import com.example.lab08.ui.theme.Lab08Theme
import com.example.lab08.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab08Theme {
                // Crear la base de datos
                val db = Room.databaseBuilder(
                    applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                ).build()

                // Crear el DAO y el ViewModel
                val taskDao = db.TaskDao()
                val viewModel = TaskViewModel(taskDao)

                // Mostrar la pantalla de tareas
                TaskScreen(viewModel)
            }
        }
    }
}

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
                        verticalAlignment = Alignment.CenterVertically // Corregido aquí
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
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                val newDescription = "Tarea editada"
                                viewModel.editTask(task, newDescription)
                            }) {
                                Text("Editar")
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

@Preview(showBackground = true)
@Composable
fun PreviewTaskScreen() {
    Lab08Theme {
        TaskScreen(TaskViewModel(object : TaskDao {
            override suspend fun getAllTasks() = listOf(
                Task(description = "Prueba 1", isCompleted = false),
                Task(description = "Prueba 2", isCompleted = true)
            )

            override suspend fun insertTask(task: Task) {}
            override suspend fun updateTask(task: Task) {}
            override suspend fun deleteTask(task: Task) {}
            override suspend fun deleteAllTasks() {}
        }))
    }
}
