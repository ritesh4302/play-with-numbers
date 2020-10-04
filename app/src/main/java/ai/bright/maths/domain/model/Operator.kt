package ai.bright.maths.domain.model

enum class Operator(val priority: Int) {
    Addition(1),
    Subtraction(1),
    Multiplication(2),
    Division(2)
}