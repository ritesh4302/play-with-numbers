package ai.bright.maths.ui

import ai.bright.maths.R
import ai.bright.maths.databinding.FragmentQuestionBinding
import ai.bright.maths.utils.hideProgressBar
import ai.bright.maths.utils.showProgressBar
import ai.bright.maths.utils.viewBinding
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionFragment : Fragment(R.layout.fragment_question) {

    private val binding by viewBinding(FragmentQuestionBinding::bind)

    private val questionViewModel: QuestionViewModel by activityViewModels()

    private val args: QuestionFragmentArgs by navArgs()

    private var navController: NavController? = null

    private var questionCounter: Int = 0
    private var answerInt: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val gameLevel = args.gameLevel
        questionViewModel.prepareQuestions(gameLevel)

        questionViewModel.apply {

            timeLeft.observe(viewLifecycleOwner, {
                binding.timeLeft.text = it
            })

            showLoading.observe(viewLifecycleOwner, {
                if (it) {
                    this@QuestionFragment.showProgressBar()
                } else {
                    this@QuestionFragment.hideProgressBar()
                    startGame()
                }
            })
        }

        binding.apply {

            level.text = "Level $gameLevel"

            submitBtn.setOnClickListener {
                val currentQuestion = questionViewModel.equationList[questionCounter]
                currentQuestion.isResponseCorrect = answerInt == currentQuestion.answer
                if (questionCounter < questionViewModel.totalNumberOfQuestions - 1) {
                    questionCounter++
                    val nextQuestion = questionViewModel.equationList[questionCounter]
                    equation.text = nextQuestion.equationString
                    questionNumber.text = getString(
                        R.string.question_number,
                        questionCounter + 1,
                        questionViewModel.totalNumberOfQuestions
                    )
                    clearAnswer()
                } else {
                    endGame()
                }
            }

            keyClear.setOnClickListener {
                clearAnswer()
            }

            keyDelete.setOnClickListener {
                answerInt /= 10
                if (answerInt > 0) {
                    answer.text = answerInt.toString()
                } else {
                    answer.text = "?"
                }
            }

            skipButton.setOnClickListener {
                val currentQuestion = questionViewModel.equationList[questionCounter]
                currentQuestion.isSkipped = true
                if (questionCounter < questionViewModel.totalNumberOfQuestions - 1) {
                    questionCounter++
                    val nextQuestion = questionViewModel.equationList[questionCounter]
                    equation.text = nextQuestion.equationString
                    questionNumber.text = getString(
                        R.string.question_number,
                        questionCounter + 1,
                        questionViewModel.totalNumberOfQuestions
                    )
                    clearAnswer()
                } else {
                    endGame()
                }
            }
        }

        binding.apply {
            arrayOf(key0, key1, key2, key3, key4, key5, key6, key7, key8, key9).forEach { key ->
                key.setOnClickListener {
                    answerInt = 10 * answerInt + it.tag.toString().toInt()
                    answer.text = answerInt.toString()
                    binding.submitBtn.isEnabled = true
                }
            }
        }
    }

    private fun endGame() {
        val timeTaken =
            questionViewModel.quizDuration - (questionViewModel._timeLeft.value ?: 0L)
        questionViewModel.endTimer()
        questionViewModel.timeTaken = (timeTaken / DateUtils.SECOND_IN_MILLIS).toInt()
        val action = QuestionFragmentDirections.actionQuestionFragmentToReportFragment()
        navController?.navigate(action)
    }

    private fun startGame() {
        questionViewModel.apply {
            startTimer()
            val equation = equationList[questionCounter]
            binding.equation.text = equation.equationString
            binding.questionNumber.text = getString(
                R.string.question_number,
                questionCounter + 1,
                questionViewModel.totalNumberOfQuestions
            )
        }
    }

    private fun clearAnswer() {
        binding.answer.text = "?"
        answerInt = 0
        binding.submitBtn.isEnabled = false
    }
}
