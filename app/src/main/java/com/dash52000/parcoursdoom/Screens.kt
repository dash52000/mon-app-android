package com.dash52000.parcoursdoom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
private fun VSpace(height: Int) = Spacer(Modifier.height(height.dp))

private fun formatDate(iso: String): String =
    runCatching { LocalDate.parse(iso).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) }.getOrDefault(iso)

// ─────────────────────────── App ───────────────────────────

@Composable
fun DoomApp(store: Store) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<Album?>(null) }
    var adding by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    icon = { Icon(Icons.Filled.PlayArrow, contentDescription = null) },
                    label = { Text("Aujourd'hui") },
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text("Frise") },
                )
            }
        },
        floatingActionButton = {
            if (tab == 1) {
                FloatingActionButton(
                    onClick = { adding = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Ajouter un album")
                }
            }
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (tab == 0) {
                TodayScreen(store, onListen = { selected = it })
            } else {
                TimelineScreen(store, onOpen = { selected = it })
            }
        }
    }

    val current = selected
    if (current != null) {
        AlbumDialog(
            album = current,
            listen = store.listened[current.id],
            onDismiss = { selected = null },
            onSave = { rating, note ->
                store.saveListen(current.id, rating, note)
                selected = null
            },
            onUnmark = {
                store.unmark(current.id)
                selected = null
            },
            onDelete = if (current.custom) ({
                store.deleteCustom(current.id)
                selected = null
            }) else null,
        )
    }

    if (adding) {
        AddAlbumDialog(
            onDismiss = { adding = false },
            onAdd = { artist, title, year, era, track, why ->
                store.addCustom(artist, title, year, era, track, why)
                adding = false
            },
        )
    }
}

// ─────────────────────── Album du jour ───────────────────────

@Composable
fun TodayScreen(store: Store, onListen: (Album) -> Unit) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val type = MaterialTheme.typography

    val main = store.mainPath()
    val done = main.count { it.id in store.listened }
    val next = store.nextAlbum()
    val last = store.lastListened()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Text("PARCOURS DOOM", style = type.labelLarge, color = colors.onSurfaceVariant, letterSpacing = 4.sp)
        VSpace(12)
        LinearProgressIndicator(
            progress = { if (main.isEmpty()) 0f else done.toFloat() / main.size },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = colors.primary,
            trackColor = colors.surfaceVariant,
        )
        VSpace(8)
        Text("$done / ${main.size} albums écoutés", style = type.bodySmall, color = colors.onSurfaceVariant)
        VSpace(36)

        if (next == null) {
            Text("Parcours terminé.", style = type.displaySmall, color = colors.onBackground)
            VSpace(12)
            Text(
                "Tout est écouté, branche bonus comprise. Ajoute tes propres trouvailles dans la frise pour prolonger le voyage.",
                style = type.bodyLarge,
                color = colors.onBackground,
            )
        } else {
            val era = ERAS[next.era]
            if (era.bonus) {
                Text("BRANCHE BONUS DÉBLOQUÉE", style = type.labelMedium, color = colors.secondary, letterSpacing = 2.sp)
                VSpace(8)
            }
            Text(
                "${era.name.uppercase()} · ${next.year}",
                style = type.labelMedium,
                color = colors.primary,
                letterSpacing = 2.sp,
            )
            VSpace(10)
            Text(next.title, style = type.displaySmall, color = colors.onBackground)
            VSpace(6)
            Text(next.artist, style = type.titleLarge, color = colors.onSurfaceVariant)
            VSpace(24)

            if (next.context.isNotBlank()) {
                Text(next.context, style = type.bodyLarge, color = colors.onBackground)
                VSpace(24)
            }

            if (next.track.isNotBlank()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(colors.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text("À écouter en priorité", style = type.labelMedium, color = colors.onSurfaceVariant)
                    VSpace(4)
                    Text("« ${next.track} »", style = type.titleMedium, color = colors.onBackground)
                }
                VSpace(28)
            }

            Button(
                onClick = { openSpotify(context, next) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Ouvrir dans Spotify")
            }
            VSpace(12)
            OutlinedButton(
                onClick = { onListen(next) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Écouté", color = colors.onBackground)
            }
        }

        if (last != null) {
            val (album, listen) = last
            VSpace(40)
            HorizontalDivider(color = colors.outline.copy(alpha = 0.5f))
            VSpace(20)
            Text("DERNIÈRE ÉCOUTE", style = type.labelMedium, color = colors.onSurfaceVariant, letterSpacing = 2.sp)
            VSpace(8)
            Text("${album.artist} — ${album.title}", style = type.titleMedium, color = colors.onBackground)
            if (listen.rating > 0) {
                VSpace(4)
                Stars(listen.rating)
            }
            if (listen.note.isNotBlank()) {
                VSpace(4)
                Text(
                    "« ${listen.note} »",
                    style = type.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}

// ─────────────────────────── Frise ───────────────────────────

@Composable
fun TimelineScreen(store: Store, onOpen: (Album) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val type = MaterialTheme.typography
    val all = store.allAlbums()
    val next = store.nextAlbum()
    val bonusOpen = store.mainDone()

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 104.dp),
    ) {
        item(key = "intro") {
            Column(Modifier.padding(horizontal = 8.dp)) {
                Text("La frise", style = type.headlineMedium, color = colors.onBackground)
                VSpace(4)
                Text(
                    "Touche un album pour le noter, le revoir ou l'ouvrir dans Spotify. Le + ajoute un album à toi.",
                    style = type.bodyMedium,
                    color = colors.onSurfaceVariant,
                )
            }
        }
        for (era in ERAS) {
            val albums = all.filter { it.era == era.index }
            val locked = era.bonus && !bonusOpen
            item(key = "era-${era.index}") {
                EraHeader(
                    era = era,
                    done = albums.count { it.id in store.listened },
                    total = albums.size,
                    locked = locked,
                )
            }
            items(albums, key = { it.id }) { album ->
                AlbumRow(
                    album = album,
                    listen = store.listened[album.id],
                    isNext = album.id == next?.id,
                    locked = locked,
                    onClick = { onOpen(album) },
                )
            }
        }
    }
}

@Composable
private fun EraHeader(era: Era, done: Int, total: Int, locked: Boolean) {
    val colors = MaterialTheme.colorScheme
    val type = MaterialTheme.typography
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 28.dp, bottom = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (locked) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = "Verrouillé",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
            }
            Text(
                era.name.uppercase(),
                style = type.labelLarge,
                color = if (era.bonus) colors.secondary else colors.primary,
                letterSpacing = 3.sp,
                modifier = Modifier.weight(1f),
            )
            Text("$done/$total", style = type.labelMedium, color = colors.onSurfaceVariant)
        }
        VSpace(2)
        Text(
            if (locked) "Se débloque à la fin du parcours" else era.period,
            style = type.bodySmall,
            color = colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun AlbumRow(album: Album, listen: Listen?, isNext: Boolean, locked: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val type = MaterialTheme.typography
    val alpha = if (locked) 0.35f else 1f

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isNext) colors.surfaceVariant else Color.Transparent)
            .clickable(enabled = !locked, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
    ) {
        Text(
            album.year.toString(),
            style = type.labelLarge,
            color = colors.onSurfaceVariant.copy(alpha = alpha),
            modifier = Modifier
                .width(52.dp)
                .padding(top = 2.dp),
        )
        Column(Modifier.weight(1f)) {
            Text(album.title, style = type.titleMedium, color = colors.onBackground.copy(alpha = alpha))
            Text(
                album.artist + (if (album.custom) " · ajouté" else ""),
                style = type.bodyMedium,
                color = colors.onSurfaceVariant.copy(alpha = alpha),
            )
            if (listen != null) {
                if (listen.rating > 0) {
                    VSpace(4)
                    Stars(listen.rating, size = 14)
                }
                if (listen.note.isNotBlank()) {
                    VSpace(2)
                    Text(
                        "« ${listen.note} »",
                        style = type.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = colors.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            } else if (isNext) {
                VSpace(4)
                Text("À écouter maintenant", style = type.labelMedium, color = colors.primary)
            }
        }
        if (listen != null) {
            Icon(
                Icons.Filled.Check,
                contentDescription = "Écouté",
                tint = colors.secondary,
                modifier = Modifier
                    .padding(start = 8.dp, top = 2.dp)
                    .size(20.dp),
            )
        }
    }
}

// ─────────────────────────── Étoiles ───────────────────────────

@Composable
fun Stars(rating: Int, size: Int = 16) {
    Text(
        "★".repeat(rating) + "☆".repeat(5 - rating),
        color = MaterialTheme.colorScheme.secondary,
        fontSize = size.sp,
        letterSpacing = 2.sp,
    )
}

@Composable
private fun StarPicker(rating: Int, onChange: (Int) -> Unit) {
    Row {
        for (i in 1..5) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { onChange(if (rating == i) 0 else i) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (i <= rating) "★" else "☆",
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

// ─────────────────────────── Dialogues ───────────────────────────

@Composable
private fun AlbumDialog(
    album: Album,
    listen: Listen?,
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit,
    onUnmark: () -> Unit,
    onDelete: (() -> Unit)?,
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val type = MaterialTheme.typography
    var rating by remember(album.id) { mutableIntStateOf(listen?.rating ?: 0) }
    var note by remember(album.id) { mutableStateOf(listen?.note ?: "") }
    var confirmDelete by remember(album.id) { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceContainerHigh,
        title = {
            Column {
                Text(album.title, style = type.headlineSmall, color = colors.onBackground)
                VSpace(4)
                Text("${album.artist} · ${album.year}", style = type.bodyMedium, color = colors.onSurfaceVariant)
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                if (album.context.isNotBlank()) {
                    Text(album.context, style = type.bodyMedium, color = colors.onBackground)
                    VSpace(12)
                }
                if (album.track.isNotBlank()) {
                    Text("À écouter : « ${album.track} »", style = type.bodyMedium, color = colors.onSurfaceVariant)
                    VSpace(16)
                }
                Text("Ta note", style = type.labelLarge, color = colors.onBackground)
                StarPicker(rating) { rating = it }
                VSpace(8)
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it.take(200) },
                    label = { Text("Ton avis en une ligne") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
                VSpace(8)
                TextButton(onClick = { openSpotify(context, album) }) {
                    Text("Ouvrir dans Spotify")
                }
                if (listen != null) {
                    TextButton(onClick = onUnmark) {
                        Text("Remettre à écouter")
                    }
                }
                if (onDelete != null) {
                    TextButton(onClick = {
                        if (confirmDelete) onDelete()
                        confirmDelete = true
                    }) {
                        Text(
                            if (confirmDelete) "Confirmer la suppression" else "Supprimer cet album",
                            color = colors.error,
                        )
                    }
                }
                if (listen != null) {
                    VSpace(4)
                    Text(
                        "Écouté le ${formatDate(listen.date)}",
                        style = type.bodySmall,
                        color = colors.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(rating, note.trim()) }) {
                Text(if (listen == null) "Marquer écouté" else "Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
    )
}

@Composable
private fun AddAlbumDialog(
    onDismiss: () -> Unit,
    onAdd: (artist: String, title: String, year: Int, era: Int, track: String, why: String) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val type = MaterialTheme.typography
    var artist by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var yearText by remember { mutableStateOf("") }
    var era by remember { mutableIntStateOf(4) }
    var eraTouched by remember { mutableStateOf(false) }
    var track by remember { mutableStateOf("") }
    var why by remember { mutableStateOf("") }

    val year = yearText.toIntOrNull()
    val valid = artist.isNotBlank() && title.isNotBlank() && year != null && year in 1960..2100

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceContainerHigh,
        title = { Text("Ajouter un album", style = type.headlineSmall) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text("Groupe") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Album") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = yearText,
                    onValueChange = { input ->
                        yearText = input.filter { c -> c.isDigit() }.take(4)
                        val y = yearText.toIntOrNull()
                        if (!eraTouched && y != null && yearText.length == 4) era = eraForYear(y)
                    },
                    label = { Text("Année") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                VSpace(12)
                Text("Époque", style = type.labelLarge, color = colors.onBackground)
                for (e in ERAS.filter { !it.bonus }) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                era = e.index
                                eraTouched = true
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = era == e.index,
                            onClick = {
                                era = e.index
                                eraTouched = true
                            },
                        )
                        Text("${e.name}  ·  ${e.period}", style = type.bodyMedium, color = colors.onBackground)
                    }
                }
                VSpace(8)
                OutlinedTextField(
                    value = track,
                    onValueChange = { track = it },
                    label = { Text("Morceau à écouter (facultatif)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = why,
                    onValueChange = { why = it },
                    label = { Text("Pourquoi cet album ? (facultatif)") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(artist.trim(), title.trim(), year!!, era, track.trim(), why.trim()) },
                enabled = valid,
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
    )
}
