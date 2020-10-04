package ai.bright.maths.domain.model

data class Equation(
    val equationString: String,
    val answer: Int,
    var isResponseCorrect: Boolean = false,
    var isSkipped: Boolean = false
)