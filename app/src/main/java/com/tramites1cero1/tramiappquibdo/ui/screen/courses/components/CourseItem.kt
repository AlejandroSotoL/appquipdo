package com.tramites1cero1.tramiappquibdo.ui.screen.courses.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tramites1cero1.tramiappquibdo.domain.model.Course

@Composable
fun CourseItem(course: Course, onButtonClick: () -> Unit) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(text = course.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
            if (course.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(course.imageUrl),
                    contentDescription = course.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 10.dp).clip(MaterialTheme.shapes.medium)
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(course.description)
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = onButtonClick, modifier = Modifier.align(Alignment.End)) {
                    Text("Inscribirse")
                }
            }
        }
    }
}

