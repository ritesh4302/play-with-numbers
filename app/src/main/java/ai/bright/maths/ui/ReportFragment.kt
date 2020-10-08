package ai.bright.maths.ui

import ai.bright.maths.R
import ai.bright.maths.databinding.FragmentReportBinding
import ai.bright.maths.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

class ReportFragment : Fragment(R.layout.fragment_report) {

    private val binding by viewBinding(FragmentReportBinding::bind)

    private val questionViewModel: QuestionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        questionViewModel.endTimer()

        val totalQuestionCount = questionViewModel.getTotalNumberOfQuestions()
        val skippedQuestionCount = questionViewModel.equationList.filter { it.isSkipped }.count()
        val correctAnswerCount =
            questionViewModel.equationList.filter { it.isResponseCorrect }.count()
        val incorrectAnswerCount = totalQuestionCount - skippedQuestionCount - correctAnswerCount
        val attemptedQuestionCount = totalQuestionCount - skippedQuestionCount
        val gameDuration = "${questionViewModel.timeTaken} sec"

        binding.apply {
            totalQuestion.text = totalQuestionCount.toString()
            skippedQuestion.text = skippedQuestionCount.toString()
            correctAnswer.text = correctAnswerCount.toString()
            incorrectAnswer.text = incorrectAnswerCount.toString()
            totalMarks.text = "$correctAnswerCount / $totalQuestionCount"
            attemptedQuestion.text = attemptedQuestionCount.toString()
            timeTaken.text = gameDuration
        }

        binding.retryBtn.setOnClickListener {
            questionViewModel.resetEquations()
            findNavController().popBackStack(R.id.levelSelectionFragment, false)
        }
    }
}
