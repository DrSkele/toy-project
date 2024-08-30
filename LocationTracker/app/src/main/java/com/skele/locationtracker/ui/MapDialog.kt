package com.skele.locationtracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun MapDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(Modifier.padding(16.dp)) {
                Text(modifier = Modifier.padding(8.dp), text = "Delete location?")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(onClick = { onConfirm() }) {
                        Text("Confirm")
                    }
                    Button(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MapDialogPreview() {
    MapDialog(onConfirm = {}, onDismiss = {})
}
