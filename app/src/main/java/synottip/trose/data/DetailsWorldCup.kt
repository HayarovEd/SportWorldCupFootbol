package synottip.trose.data

data class DetailsWorldCup(
    val year: Int,
    val country: String,
    val flagWinner: Int,
    val dateStart: String,
    val dateEnd: String,
    val participatingCountries: Int,
    val finalStageCountry: Int,
    val winnerCountry: String,
    val mascot: Int,
    val description: String
)
