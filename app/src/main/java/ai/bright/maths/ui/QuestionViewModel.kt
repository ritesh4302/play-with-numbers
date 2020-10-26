package ai.bright.maths.ui

import ai.bright.maths.domain.model.*
import android.content.Context
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class QuestionViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var gameLevel: Int = 1
    private var totalNumberOfQuestions = 10
    private var gameMode: GameMode = GameMode.VISUAL
    private var equationType: EquationType = EquationType.TYPE_1
    private var operatorList: List<Operator> = listOf(Operator.Addition)
    val gameType: GameType
        get() = if (operatorList.contains(Operator.Multiplication)) {
            GameType.MULTIPLY
        } else if (operatorList.contains(Operator.Division)) {
            GameType.DIVIDE
        } else {
            GameType.ADD_SUBTRACT
        }

    private var gameDuration = 90 * DateUtils.SECOND_IN_MILLIS

    val equationList = mutableListOf<Equation>()

    var timeTaken: Int = 0

    val showLoading = MutableLiveData(true)

    fun setGameLevel(gameLevel: Int) {
        this.gameLevel = gameLevel
    }

    fun getGameLevel(): Int = gameLevel

    fun setOperatorList(operatorList: List<Operator>) {
        this.operatorList = operatorList
    }

    fun getTotalNumberOfQuestions(): Int = totalNumberOfQuestions

    fun setGameMode(gameMode: GameMode) {
        this.gameMode = gameMode
    }

    fun getGameMode(): GameMode = gameMode

    fun setEquationType(equationType: EquationType) {
        this.equationType = equationType
    }

    fun getEquationType(): EquationType = equationType

    private fun setGameDuration() {
        gameDuration = when (gameLevel) {
            2 -> {
                when (gameType) {
                    GameType.MULTIPLY -> 60
                    GameType.ADD_SUBTRACT -> when (gameMode) {
                        GameMode.VISUAL -> 60
                        GameMode.ABACUS -> 180
                    }
                    else -> throw Exception("$gameType not handled")
                }
            }
            3 -> {
                when (gameType) {
                    GameType.MULTIPLY -> 60
                    GameType.ADD_SUBTRACT -> when (gameMode) {
                        GameMode.VISUAL -> 60
                        GameMode.ABACUS -> 180
                    }
                    else -> throw Exception("$gameType not handled")
                }
            }
            4 -> {
                when (gameType) {
                    GameType.MULTIPLY -> 60
                    GameType.ADD_SUBTRACT -> when (gameMode) {
                        GameMode.VISUAL -> 60
                        GameMode.ABACUS -> 180
                    }
                    GameType.DIVIDE -> 60
                    else -> throw Exception("$gameType not handled")
                }
            }
            else -> 180
        }
    }

    fun getGameDuration(): Long = gameDuration

    fun prepareQuestions() {
        viewModelScope.launch(Dispatchers.Default) {
            showLoading.postValue(true)

            setGameDuration()
            setTotalNumberOfQuestions()
            val numberOfRows = getNumberOfRows()

            while (equationList.size < totalNumberOfQuestions) {

                val operandsMutable = mutableListOf<Float>()
                for (i in 0 until numberOfRows) {
                    val prevOperand = if (i > 0) operandsMutable[i - 1] else -1f
                    val operand = getOperand(i, prevOperand)
                    operandsMutable.add(operand)
                }
                val operatorsMutable = mutableListOf<Operator>()
                for (i in 0 until (numberOfRows - 1)) {
                    val operator = getOperator()
                    operatorsMutable.add(operator)
                }

                val operands = operandsMutable.toList()
                val operators = operatorsMutable.toList()
                val output = computeQuestion(operatorsMutable, operandsMutable)
                if (output < 0 || (output - output.toInt() > 0)) {
                    continue
                }

                val equation =
                    getEquation(
                        operands,
                        operators,
                        output.toInt()
                    )
                equationList.add(equation)
            }
            showLoading.postValue(false)
        }
    }

    private fun getNumberOfRows(): Int = when (gameLevel) {
        2 -> {
            when (gameType) {
                GameType.MULTIPLY -> 2
                GameType.ADD_SUBTRACT -> when (gameMode) {
                    GameMode.VISUAL -> 10
                    GameMode.ABACUS -> 5
                }
                else -> throw Exception("$gameType not handled")
            }
        }
        3 -> {
            when (gameType) {
                GameType.MULTIPLY -> 2
                GameType.ADD_SUBTRACT -> when (gameMode) {
                    GameMode.VISUAL -> 20
                    GameMode.ABACUS -> {
                        when (equationType) {
                            EquationType.TYPE_1 -> 7
                            EquationType.TYPE_2 -> 5
                        }
                    }
                }
                else -> throw Exception("$gameType not handled")
            }
        }
        4 -> {
            when (gameType) {
                GameType.ADD_SUBTRACT -> when (gameMode) {
                    GameMode.VISUAL -> 30
                    GameMode.ABACUS -> when (equationType) {
                        EquationType.TYPE_1 -> 10
                        EquationType.TYPE_2 -> 7
                    }
                }
                GameType.MULTIPLY -> 2
                GameType.DIVIDE -> 2
                else -> throw Exception("$gameType not handled")
            }
        }
        else -> 2
    }

    private fun setTotalNumberOfQuestions() {
        totalNumberOfQuestions = when (gameLevel) {
            2 -> {
                when (gameType) {
                    GameType.MULTIPLY -> 30
                    GameType.ADD_SUBTRACT -> 10
                    else -> throw Exception("$gameType not handled")
                }
            }
            else -> 10
        }
    }

    private fun getOperand(index: Int = 0, prevOperand: Float = -1f): Float {
        var upperLimit = 10
        var lowerLimit = 1
        when (gameLevel) {
            2 -> {
                when (gameType) {
                    GameType.MULTIPLY -> {
                        lowerLimit = 2
                        upperLimit = 10
                    }
                    GameType.ADD_SUBTRACT -> {
                        when (gameMode) {
                            GameMode.VISUAL -> {
                                lowerLimit = 1
                                upperLimit = 10
                            }
                            GameMode.ABACUS -> {
                                lowerLimit = 10
                                upperLimit = 100
                            }
                        }
                    }
                    else -> throw Exception("$gameType not handled")
                }
            }
            3 -> {
                when (gameType) {
                    GameType.MULTIPLY -> {
                        if (index == 0) {
                            lowerLimit = 12
                            upperLimit = 100
                        } else {
                            lowerLimit = 1
                            upperLimit = 10
                        }
                    }
                    GameType.ADD_SUBTRACT -> {
                        when (gameMode) {
                            GameMode.VISUAL -> {
                                lowerLimit = 1
                                upperLimit = 10
                            }
                            GameMode.ABACUS -> {
                                when (equationType) {
                                    EquationType.TYPE_1 -> {
                                        lowerLimit = 10
                                        upperLimit = 100
                                    }
                                    EquationType.TYPE_2 -> {
                                        lowerLimit = 100
                                        upperLimit = 1000
                                    }
                                }
                            }
                        }
                    }
                    else -> throw Exception("$gameType not handled")
                }
            }
            4 -> {
                when (gameType) {
                    GameType.MULTIPLY -> when (equationType) {
                        EquationType.TYPE_1 -> {
                            if (index == 0) {
                                lowerLimit = 12
                                upperLimit = 100
                            } else {
                                lowerLimit = 2
                                upperLimit = 10
                            }
                        }
                        EquationType.TYPE_2 -> {
                            if (index == 0) {
                                lowerLimit = 1
                                upperLimit = 10
                            } else {
                                lowerLimit = 12
                                upperLimit = 100
                            }
                        }
                    }
                    GameType.ADD_SUBTRACT -> {
                        when (gameMode) {
                            GameMode.VISUAL -> {
                                lowerLimit = 1
                                upperLimit = 10
                            }
                            GameMode.ABACUS -> {
                                when (equationType) {
                                    EquationType.TYPE_1 -> {
                                        lowerLimit = 10
                                        upperLimit = 100
                                    }
                                    EquationType.TYPE_2 -> {
                                        lowerLimit = 100
                                        upperLimit = 1000
                                    }
                                }
                            }
                        }
                    }
                    GameType.DIVIDE -> {
                        if (index == 0) {
                            lowerLimit = 2
                            upperLimit = 10
                        } else {
                            var operand2 = 0f
                            while (true) {
                                val answer = Random.nextInt(12, 500)
                                operand2 = answer * prevOperand
                                if (getNumberOfDigits(operand2.toInt()) == 3) {
                                    break
                                }
                            }
                            return operand2
                        }
                    }
                    else -> throw Exception("$gameType not handled")
                }
            }
        }
        return Random.nextInt(lowerLimit, upperLimit).toFloat()
    }

    private fun getNumberOfDigits(number: Int): Int {
        var i = 1
        var num = number
        while (num / 10 >= 1) {
            num /= 10
            i++
        }
        return i
    }

    private fun getOperator(): Operator {
        val size = operatorList.size
        val index = Random.nextInt(0, size)
        return operatorList[index]
    }

    private fun computeQuestion(
        operators: MutableList<Operator>,
        operands: MutableList<Float>
    ): Float {
        val higherPriorityOperator = getHigherPriorityOperator(operators)
        val operatorIndex = operators.indexOf(higherPriorityOperator)
        val operand1 = operands[operatorIndex]
        val operand2 = operands[operatorIndex + 1]

        val output = computeEquation(operand1, operand2, higherPriorityOperator)
        operators.removeAt(operatorIndex)
        operands.removeAt(operatorIndex)
        operands.removeAt(operatorIndex)
        operands.add(operatorIndex, output)
        if (operators.size == 0) {
            return output
        }
        return computeQuestion(operators, operands)
    }

    private fun computeEquation(operand1: Float, operand2: Float, operator: Operator) =
        when (operator) {
            Operator.Addition -> operand1 + operand2
            Operator.Subtraction -> operand1 - operand2
            Operator.Multiplication -> operand1 * operand2
            Operator.Division -> operand2 / operand1
        }

    private fun getHigherPriorityOperator(operatorList: List<Operator>) =
        operatorList.maxByOrNull { it.priority } ?: operatorList[0]

    private fun getEquation(
        operands: List<Float>,
        operators: List<Operator>,
        answer: Int
    ): Equation {
        val equationStrBuilder = StringBuilder()
        var i = 0
        while (i < operands.size) {
            val operator = if (i < operators.size) operators[i] else Operator.Addition
            if (operator == Operator.Division) {
                equationStrBuilder.append(
                    "${operands[i + 1].toInt()} ${getOperatorString(operator)} ${operands[i].toInt()}"
                )
                i++
            } else {
                equationStrBuilder.append(
                    if (i < operators.size) {
                        "${operands[i].toInt()} ${getOperatorString(operator)} "
                    } else {
                        "${operands[i].toInt()}"
                    }
                )
            }
            i++
        }
        val equationString = equationStrBuilder.toString()
        return Equation(equationString, answer)
    }

    private fun getOperatorString(operator: Operator) = when (operator) {
        Operator.Addition -> "+"
        Operator.Subtraction -> "-"
        Operator.Multiplication -> "*"
        Operator.Division -> "÷"
    }

    private var timer: CountDownTimer? = null
    val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<String>
        get() {
            return _timeLeft.map { milliSeconds ->
                convertTimeToString(milliSeconds)
            }
        }

    fun startTimer() {
        timer = object : CountDownTimer(
            gameDuration * DateUtils.SECOND_IN_MILLIS,
            TimeUnit.SECONDS.toMillis(1)
        ) {
            override fun onFinish() {
                _timeLeft.postValue(0L)
            }

            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.postValue(millisUntilFinished)
            }
        }
        timer?.start()
    }

    fun endTimer() {
        timer?.cancel()
        timer = null
    }

    fun resetEquations() {
        equationList.clear()
    }

    private fun convertTimeToString(time: Long): String {
        val minutes = time / DateUtils.MINUTE_IN_MILLIS
        val seconds = time / DateUtils.SECOND_IN_MILLIS % 60
        val secondsString = "%02d".format(seconds)
        return if (minutes > 0) {
            "$minutes:$secondsString min"
        } else {
            "$secondsString sec"
        }
    }
}
