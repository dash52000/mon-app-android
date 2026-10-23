package com.dash52000.parcoursdoom

data class Era(
    val index: Int,
    val name: String,
    val period: String,
    val bonus: Boolean = false,
)

data class Album(
    val id: String,
    val artist: String,
    val title: String,
    val year: Int,
    val era: Int,
    val track: String,
    val context: String,
    val custom: Boolean = false,
    val order: Long = 0,
)

data class Listen(
    val date: String,
    val rating: Int,
    val note: String,
    val ts: Long,
)

val ERAS = listOf(
    Era(0, "Racines", "1970 – 1985"),
    Era(1, "Fondateurs", "1982 – 1987"),
    Era(2, "Âge épique", "1988 – 2000"),
    Era(3, "Renouveau", "2000 – 2010"),
    Era(4, "Vague actuelle", "2010 – aujourd'hui"),
    Era(5, "Branche doom-death", "Angleterre, années 90", bonus = true),
)

/** Époque proposée par défaut quand tu ajoutes un album. */
fun eraForYear(year: Int): Int = when {
    year < 1982 -> 0
    year < 1988 -> 1
    year < 2000 -> 2
    year < 2010 -> 3
    else -> 4
}

val SEED_ALBUMS: List<Album> = listOf(
    // ── Racines ──
    Album(
        id = "sabbath", artist = "Black Sabbath", title = "Black Sabbath", year = 1970, era = 0,
        track = "Black Sabbath",
        context = "L'acte de naissance du genre. Sorti un vendredi 13 de février 1970, enregistré en une journée, presque en live. Tony Iommi, qui a perdu le bout de deux doigts dans un accident d'usine, passe à des cordes plus souples : le son lourd vient en partie de là. Le morceau-titre, avec la pluie, la cloche et le riff sur le triton, contient tout le doom en germe.",
    ),
    Album(
        id = "pentagram", artist = "Pentagram", title = "Relentless", year = 1985, era = 0,
        track = "Sign of the Wolf (Pentagram)",
        context = "Le doom américain le plus brut : son garage, riffs à la Sabbath, esprit heavy rock 70s. Le groupe existe depuis 1971 et l'album reprend surtout des morceaux de cette décennie. Sorti en 1985 sous le titre Pentagram, réédité en 1993 sous le nom Relentless. Le dernier morceau, « 20 Buck Spin », a donné son nom au label des premiers Khemmis.",
    ),

    // ── Fondateurs ──
    Album(
        id = "witchfinder", artist = "Witchfinder General", title = "Death Penalty", year = 1982, era = 1,
        track = "Witchfinder General",
        context = "Stourbridge, en pleine NWOBHM. Quand tout le monde accélère, eux restent fidèles au tempo de Sabbath. Production fauchée, riffs plombés, et une pochette qui a fait scandale à l'époque.",
    ),
    Album(
        id = "trouble", artist = "Trouble", title = "Psalm 9", year = 1984, era = 1,
        track = "The Tempter",
        context = "Chicago. Deux guitares jumelles, Bruce Franklin et Rick Wartell, et la voix haut perchée d'Eric Wagner. À cause de paroles très spirituelles, leur label les a vendus comme du « white metal ». C'est un des piliers du doom américain avec Saint Vitus.",
    ),
    Album(
        id = "candlemass", artist = "Candlemass", title = "Epicus Doomicus Metallicus", year = 1986, era = 1,
        track = "Solitude",
        context = "Stockholm. Le bassiste Leif Edling compose tout, et le chanteur Johan Längquist, simple invité, enregistre puis s'en va. L'album donne son nom à l'epic doom : lenteur, grandeur, chant lyrique.",
    ),
    Album(
        id = "saintvitus", artist = "Saint Vitus", title = "Born Too Late", year = 1986, era = 1,
        track = "Born Too Late",
        context = "Los Angeles, chez SST, le label punk de Black Flag : le doom des marginaux, sale et têtu. Premier album avec Scott « Wino » Weinrich au chant. Le morceau-titre est l'hymne de ceux qui se sentent nés à la mauvaise époque.",
    ),

    // ── Âge épique ──
    Album(
        id = "solitude", artist = "Solitude Aeturnus", title = "Into the Depths of Sorrow", year = 1991, era = 2,
        track = "Opaque Divinity",
        context = "Texas, premier album chez Roadrunner. La voix puissante et dramatique de Robert Lowe tire le doom vers le lyrique. Il chantera plus tard chez Candlemass.",
    ),
    Album(
        id = "paradiselost", artist = "Paradise Lost", title = "Gothic", year = 1991, era = 2,
        track = "Gothic",
        context = "Le détour doom-death. Halifax, chez Peaceville. Les growls de Nick Holmes croisent des claviers et des chœurs féminins : l'album invente au passage le gothic metal. C'est de cette branche que viennent les growls de Khemmis.",
    ),
    Album(
        id = "countraven", artist = "Count Raven", title = "Destruction of the Void", year = 1992, era = 2,
        track = "Leaving the Warzone",
        context = "Stockholm. Dan « Fodde » Fondelius prend le micro, avec une voix très proche d'Ozzy. Du doom traditionnel hypnotique, qui alterne riffs massifs et passages planants.",
    ),

    // ── Renouveau ──
    Album(
        id = "reverend", artist = "Reverend Bizarre", title = "In the Rectory of the Bizarre Reverend", year = 2002, era = 3,
        track = "Burn in Hell!",
        context = "Finlande. Au moment où le doom semble mort, trois Finlandais décident de jouer plus lent et plus long que tout le monde, avec des morceaux qui dépassent souvent les dix minutes. Sans compromis, et parfois avec humour.",
    ),
    Album(
        id = "whileheaven", artist = "While Heaven Wept", title = "Vast Oceans Lachrymose", year = 2009, era = 3,
        track = "Vessel",
        context = "Virginie. Le groupe de Tom Phillips existe depuis la fin des années 80 mais sort très peu de disques. La voix de Rain Irving et les claviers rendent le doom mélancolique, presque romantique.",
    ),
    Album(
        id = "atlantean", artist = "Atlantean Kodex", title = "The Golden Bough", year = 2010, era = 3,
        track = "Pilgrim",
        context = "Allemagne, premier album chez Cruz del Sur. Un doom épique nourri de Manilla Road et de Bathory, avec des chœurs guerriers et des textes sur les mythes européens. Le titre vient du célèbre livre de Frazer sur la magie et les religions.",
    ),

    // ── Vague actuelle ──
    Album(
        id = "pallbearer", artist = "Pallbearer", title = "Sorrow and Extinction", year = 2012, era = 4,
        track = "Foreigner",
        context = "Little Rock, Arkansas, premier album chez Profound Lore, salué partout. Le chant clair de Brett Campbell et des morceaux longs qui prennent le temps de monter : le doom redevient bouleversant.",
    ),
    Album(
        id = "cryptsermon", artist = "Crypt Sermon", title = "Out of the Garden", year = 2015, era = 4,
        track = "Temple Doors",
        context = "Philadelphie. Un retour assumé à l'epic doom de Candlemass et de Solitude Aeturnus, avec des textes bibliques et la voix de Brooks Wilson au centre de tout.",
    ),
    Album(
        id = "khemmis", artist = "Khemmis", title = "Desolation", year = 2018, era = 4,
        track = "Isolation",
        context = "Denver. Ce qu'ils appellent eux-mêmes du « doomed heavy metal » : guitares jumelles harmonisées, chant clair de Phil Pendergast et growls de Ben Hutcherson. Leur album le plus heavy metal classique, et la boucle est bouclée avec Sabbath.",
    ),

    // ── Branche bonus : doom-death ──
    Album(
        id = "mydyingbride", artist = "My Dying Bride", title = "Turn Loose the Swans", year = 1993, era = 5,
        track = "The Crown of Sympathy",
        context = "Bradford, chez Peaceville. Le violon de Martin Powell, les growls et le chant clair d'Aaron Stainthorpe : le doom-death dans sa version la plus romantique et la plus désespérée.",
    ),
    Album(
        id = "anathema", artist = "Anathema", title = "The Silent Enigma", year = 1995, era = 5,
        track = "A Dying Wish",
        context = "Liverpool. Premier album avec Vincent Cavanagh au chant, après le départ de Darren White. Le groupe quitte peu à peu le death pour une mélancolie plus atmosphérique.",
    ),
).mapIndexed { i, album -> album.copy(order = i.toLong()) }
