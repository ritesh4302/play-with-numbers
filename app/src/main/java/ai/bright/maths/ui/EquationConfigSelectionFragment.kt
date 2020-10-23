package ai.bright.maths.ui

import ai.bright.maths.R
import ai.bright.maths.databinding.FragmentEquationConfigSelectionBinding
import ai.bright.maths.domain.model.EquationType
import ai.bright.maths.domain.model.GameType
import ai.bright.maths.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

class EquationConfigSelectionFragment : Fragment(R.layout.fragment_equation_config_selection) {

    private val binding by viewBinding(FragmentEquationConfigSelectionBinding::bind)

    private val questionViewModel: QuestionViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        var eqType1Text: String = ""
        var eqType2Text: String = ""

        when (questionViewModel.getGameLevel()) {
            3 -> {
                when (questionViewModel.gameType) {
                    GameType.ADD_SUBTRACT -> {
                        eqType1Text = "2 digits  x  7 rows"
                        eqType2Text = "3 digits  x  5 rows"
                    }
                }
            }
            4 -> {
                when (questionViewModel.gameType) {
                    GameType.ADD_SUBTRACT -> {
                        eqType1Text = "2 digits  x  10 rows"
                        eqType2Text = "3 digits  x  7 rows"
                    }
                    GameType.MULTIPLY -> {
                        eqType1Text = "2 digits  x  1 digit"
                        eqType2Text = "1 digit  x  2 digits"
                    }
                }
            }
        }

        binding.apply {
            type1.text = eqType1Text
            type2.text = eqType2Text

            type1.setOnClickListener {
                questionViewModel.setEquationType(EquationType.TYPE_1)
                navigateToQuestionFragment()
            }

            type2.setOnClickListener {
                questionViewModel.setEquationType(EquationType.TYPE_2)
                navigateToQuestionFragment()
            }
        }
    }

    private fun navigateToQuestionFragment() {
        val action =
            EquationConfigSelectionFragmentDirections.actionEquationConfigSelectionFragmentToQuestionFragment()
        navController.navigate(action)
    }
}