package com.dash52000.parcoursdoom

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        val store = Store(File(filesDir, "parcours.json"))
        setContent {
            DoomTheme {
                DoomApp(store)
            }
        }
    }
}

/** Ouvre la recherche de l'album dans l'appli Spotify, ou sur le web si elle n'est pas installée. */
fun openSpotify(context: Context, album: Album) {
    val query = Uri.encode("${album.artist} ${album.title}")
    val attempts = listOf("spotify:search:$query", "https://open.spotify.com/search/$query")
    for (uri in attempts) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
            return
        } catch (e: Exception) {
            // on tente l'adresse suivante
        }
    }
    Toast.makeText(context, "Impossible d'ouvrir Spotify", Toast.LENGTH_SHORT).show()
}
