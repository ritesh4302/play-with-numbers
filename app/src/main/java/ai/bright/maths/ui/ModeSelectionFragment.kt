package ai.bright.maths.ui

import ai.bright.maths.R
import ai.bright.maths.databinding.FragmentModeSelectionBinding
import ai.bright.maths.domain.model.GameMode
import ai.bright.maths.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

class ModeSelectionFragment : Fragment(R.layout.fragment_mode_selection) {

    private val binding by viewBinding(FragmentModeSelectionBinding::bind)

    private val questionViewModel: QuestionViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.visual.setOnClickListener {
            questionViewModel.setGameMode(GameMode.VISUAL)
            navigateToQuestionFragment()
        }

        binding.abacus.setOnClickListener {
            questionViewModel.setGameMode(GameMode.ABACUS)
            when (questionViewModel.getGameLevel()) {
                3, 4 -> navigateToEquationTypeSelectionFragment()
                else -> navigateToQuestionFragment()
            }
        }
    }

    private fun navigateToEquationTypeSelectionFragment() {
        val action =
            ModeSelectionFragmentDirections.actionModeSelectionFragmentToEquationConfigSelectionFragment()
        navController.navigate(action)
    }

    private fun navigateToQuestionFragment() {
        val action = ModeSelectionFragmentDirections.actionModeSelectionFragmentToQuestionFragment()
        navController.navigate(action)
    }
}
