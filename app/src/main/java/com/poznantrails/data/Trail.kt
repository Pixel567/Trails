package com.poznantrails.data

enum class TrailType { HIKING, CYCLING }

enum class Difficulty(val label: String) {
    EASY("Łatwa"),
    MEDIUM("Średnia"),
    HARD("Trudna")
}

data class Trail(
    val id: String,
    val name: String,
    val type: TrailType,
    val distance: String,
    val difficulty: Difficulty,
    val wikipediaTitle: String
)

val HIKING_TRAILS = listOf(
    Trail("h1", "Szlak Wokół Jeziora Maltańskiego", TrailType.HIKING, "5.2 km", Difficulty.EASY,  "Jezioro_Maltańskie"),
    Trail("h2", "Ścieżka Przez Ławicę",             TrailType.HIKING, "8.4 km", Difficulty.MEDIUM, "Ławica_(Poznań)"),
    Trail("h3", "Szlak Cytadeli Poznańskiej",        TrailType.HIKING, "4.1 km", Difficulty.EASY,  "Cytadela_w_Poznaniu"),
    Trail("h4", "Trasa Przez Las Piątkowski",        TrailType.HIKING, "11.7 km",Difficulty.MEDIUM, "Piątkowo_(Poznań)"),
    Trail("h5", "Ścieżka Wzdłuż Warty",             TrailType.HIKING, "15.3 km",Difficulty.EASY,  "Warta")
)

val CYCLING_TRAILS = listOf(
    Trail("c1", "Trasa Rowerowa Wzdłuż Warty",      TrailType.CYCLING, "22.5 km", Difficulty.EASY,   "Warta"),
    Trail("c2", "Pętla Rowerowa Jezioro Góreckie",  TrailType.CYCLING, "18.0 km", Difficulty.EASY,   "Jezioro_Góreckie"),
    Trail("c3", "Szlak Rowerowy Maltanka",           TrailType.CYCLING, "12.3 km", Difficulty.EASY,   "Jezioro_Maltańskie"),
    Trail("c4", "Trasa Rowerowa Dębina–Luboń",      TrailType.CYCLING, "25.6 km", Difficulty.MEDIUM, "Dębina_(Poznań)"),
    Trail("c5", "Szlak MTB Morasko",                TrailType.CYCLING, "19.8 km", Difficulty.HARD,   "Moraska_Góra")
)
