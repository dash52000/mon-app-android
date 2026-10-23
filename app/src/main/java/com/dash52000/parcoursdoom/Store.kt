package com.dash52000.parcoursdoom

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDate

/**
 * Tout est stocké dans un petit fichier JSON sur le téléphone.
 * Les listes « state » de Compose rafraîchissent l'écran à chaque changement.
 */
class Store(private val file: File) {

    val listened = mutableStateMapOf<String, Listen>()
    val custom = mutableStateListOf<Album>()

    init {
        load()
    }

    // ── Lecture du parcours ──

    fun allAlbums(): List<Album> =
        (SEED_ALBUMS + custom).sortedWith(compareBy<Album>({ it.era }, { it.year }, { it.order }))

    fun mainPath(): List<Album> = allAlbums().filter { !ERAS[it.era].bonus }

    fun bonusPath(): List<Album> = allAlbums().filter { ERAS[it.era].bonus }

    fun mainDone(): Boolean = mainPath().all { it.id in listened }

    fun nextAlbum(): Album? {
        val nextMain = mainPath().firstOrNull { it.id !in listened }
        if (nextMain != null) return nextMain
        return bonusPath().firstOrNull { it.id !in listened }
    }

    fun lastListened(): Pair<Album, Listen>? {
        val byId = allAlbums().associateBy { it.id }
        return listened.entries
            .mapNotNull { entry -> byId[entry.key]?.let { album -> album to entry.value } }
            .maxByOrNull { it.second.ts }
    }

    // ── Modifications ──

    fun saveListen(id: String, rating: Int, note: String) {
        val old = listened[id]
        listened[id] = Listen(
            date = old?.date ?: LocalDate.now().toString(),
            rating = rating,
            note = note,
            ts = old?.ts ?: System.currentTimeMillis(),
        )
        save()
    }

    fun unmark(id: String) {
        listened.remove(id)
        save()
    }

    fun addCustom(artist: String, title: String, year: Int, era: Int, track: String, context: String) {
        val now = System.currentTimeMillis()
        custom.add(
            Album(
                id = "c$now", artist = artist, title = title, year = year, era = era.coerceIn(0, 4),
                track = track, context = context, custom = true, order = now,
            )
        )
        save()
    }

    fun deleteCustom(id: String) {
        custom.removeAll { it.id == id }
        listened.remove(id)
        save()
    }

    // ── Fichier ──

    private fun load() {
        if (!file.exists()) {
            // Premier lancement : Black Sabbath et Pentagram sont déjà écoutés.
            val today = LocalDate.now().toString()
            listened["sabbath"] = Listen(today, 0, "", 1L)
            listened["pentagram"] = Listen(today, 0, "", 2L)
            save()
            return
        }
        try {
            val root = JSONObject(file.readText())
            root.optJSONObject("listened")?.let { obj ->
                for (key in obj.keys()) {
                    val o = obj.getJSONObject(key)
                    listened[key] = Listen(
                        date = o.optString("date"),
                        rating = o.optInt("rating"),
                        note = o.optString("note"),
                        ts = o.optLong("ts"),
                    )
                }
            }
            root.optJSONArray("custom")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    custom.add(
                        Album(
                            id = o.getString("id"),
                            artist = o.optString("artist"),
                            title = o.optString("title"),
                            year = o.optInt("year"),
                            era = o.optInt("era").coerceIn(0, 4),
                            track = o.optString("track"),
                            context = o.optString("context"),
                            custom = true,
                            order = o.optLong("order"),
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Fichier illisible : on le met de côté plutôt que de l'écraser.
            runCatching { file.copyTo(File(file.parentFile, file.name + ".corrompu"), overwrite = true) }
        }
    }

    private fun save() {
        val listenedJson = JSONObject()
        for ((id, l) in listened) {
            listenedJson.put(
                id,
                JSONObject()
                    .put("date", l.date)
                    .put("rating", l.rating)
                    .put("note", l.note)
                    .put("ts", l.ts),
            )
        }
        val customJson = JSONArray()
        for (a in custom) {
            customJson.put(
                JSONObject()
                    .put("id", a.id)
                    .put("artist", a.artist)
                    .put("title", a.title)
                    .put("year", a.year)
                    .put("era", a.era)
                    .put("track", a.track)
                    .put("context", a.context)
                    .put("order", a.order),
            )
        }
        val text = JSONObject()
            .put("version", 1)
            .put("listened", listenedJson)
            .put("custom", customJson)
            .toString(2)

        val tmp = File(file.parentFile, file.name + ".tmp")
        tmp.writeText(text)
        if (!tmp.renameTo(file)) {
            file.writeText(text)
            tmp.delete()
        }
    }
}
