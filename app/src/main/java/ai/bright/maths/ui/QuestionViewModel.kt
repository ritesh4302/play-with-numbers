package ai.bright.maths.ui

import ai.bright.maths.domain.model.Equation
import ai.bright.maths.domain.model.Operator
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

    val quizDuration = 90 * DateUtils.SECOND_IN_MILLIS
    val totalNumberOfQuestions = 10

    val equationList = mutableListOf<Equation>()

    var timeTaken: Int = 0

    val showLoading = MutableLiveData(true)

    fun prepareQuestions(gameLevel: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            showLoading.postValue(true)
            while (equationList.size < totalNumberOfQuestions) {
                val firstOperand = getOperand(gameLevel)
                val secondOperand = getOperand(gameLevel)
                val thirdOperand = getOperand(gameLevel)
                val firstOperator = getOperator(gameLevel)
                val secondOperator = getOperator(gameLevel)
                val operands = mutableListOf<Float>(
                    firstOperand.toFloat(),
                    secondOperand.toFloat(),
                    thirdOperand.toFloat()
                )
                val operators = mutableListOf(firstOperator, secondOperator)
                val output = computeQuestion(operators, operands)
                if (output < 0 || (output - output.toInt() > 0)) {
                    continue
                }
                val equation =
                    getEquation(
                        firstOperand,
                        secondOperand,
                        thirdOperand,
                        firstOperator,
                        secondOperator,
                        output.toInt()
                    )
                equationList.add(equation)
            }
            showLoading.postValue(false)
        }
    }

    private fun getOperand(gameLevel: Int): Int {
        val upperLimit = when (gameLevel) {
            1 -> 10
            2 -> 25
            3 -> 15
            else -> 40
        }
        return Random.nextInt(1, upperLimit)
    }

    private fun getOperator(gameLevel: Int): Operator =
        when (gameLevel) {
            1 -> Operator.Addition
            2 -> arrayOf(Operator.Addition, Operator.Subtraction)[Random.nextInt(2)]
            3 -> arrayOf(
                Operator.Addition,
                Operator.Subtraction,
                Operator.Multiplication
            )[Random.nextInt(3)]
            else -> arrayOf(
                Operator.Addition,
                Operator.Subtraction,
                Operator.Multiplication,
                Operator.Division
            )[Random.nextInt(4)]
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
            Operator.Division -> operand1 / operand2
        }

    private fun getHigherPriorityOperator(operatorList: List<Operator>) =
        operatorList.maxByOrNull { it.priority } ?: operatorList[0]

    private fun getEquation(
        operand1: Int,
        operand2: Int,
        operand3: Int,
        operator1: Operator,
        operator2: Operator,
        answer: Int
    ): Equation {
        val equationString =
            "$operand1 ${getOperatorString(operator1)} $operand2 ${getOperatorString(operator2)} $operand3"
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

    fun startTimer(timeLeftInMillis: Long = quizDuration) {
        timer = object : CountDownTimer(timeLeftInMillis, TimeUnit.SECONDS.toMillis(1)) {
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