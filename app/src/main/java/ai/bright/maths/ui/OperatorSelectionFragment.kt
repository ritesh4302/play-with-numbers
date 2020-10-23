package ai.bright.maths.ui

import ai.bright.maths.R
import ai.bright.maths.databinding.FragmentOperatorSelectionBinding
import ai.bright.maths.domain.model.Operator
import ai.bright.maths.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

class OperatorSelectionFragment : Fragment(R.layout.fragment_operator_selection) {

    private val binding by viewBinding(FragmentOperatorSelectionBinding::bind)

    private val questionViewModel: QuestionViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.additionSubtraction.setOnClickListener {
            val operators = listOf(Operator.Addition, Operator.Subtraction)
            questionViewModel.setOperatorList(operators)
            navigateToModeSelectionFragment()
        }

        binding.multiplication.setOnClickListener {
            val operators = listOf(Operator.Multiplication)
            questionViewModel.setOperatorList(operators)
            when(questionViewModel.getGameLevel()) {
                4 -> navigateToEquationTypeSelectionFragment()
                else -> navigateToQuestionFragment()
            }
        }
    }

    private fun navigateToEquationTypeSelectionFragment() {
        val action =
            OperatorSelectionFragmentDirections.actionOperatorSelectionFragmentToEquationConfigSelectionFragment()
        navController.navigate(action)
    }

    private fun navigateToModeSelectionFragment() {
        val action =
            OperatorSelectionFragmentDirections.actionOperatorSelectionFragmentToModeSelectionFragment()
        navController.navigate(action)
    }

    private fun navigateToQuestionFragment() {
        val action =
            OperatorSelectionFragmentDirections.actionOperatorSelectionFragmentToQuestionFragment()
        navController.navigate(action)
    }
}
