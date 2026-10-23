# Parcours Doom

Petite appli Android perso : un parcours d'écoute chronologique du doom metal, un album à la fois, de Black Sabbath (1970) à Khemmis (2018), plus une branche bonus doom-death.

- **Aujourd'hui** : l'album suivant, son contexte, le morceau à écouter en priorité, un bouton vers Spotify et un bouton « Écouté » (note sur 5 + une ligne d'avis).
- **Frise** : les 5 époques qui se remplissent au fil de l'écoute, et le + pour ajouter tes propres albums.

Les données restent sur le téléphone (fichier JSON local, aucune connexion requise).

## Récupérer l'APK

Chaque push sur `main` compile l'appli via GitHub Actions et publie l'APK dans les [Releases](../../releases/latest).
Sur le téléphone : télécharger `parcours-doom.apk`, l'ouvrir, autoriser l'installation depuis le navigateur la première fois.

Les mises à jour s'installent par-dessus l'ancienne version sans perdre les notes (clé de signature fixe dans `app/`).
