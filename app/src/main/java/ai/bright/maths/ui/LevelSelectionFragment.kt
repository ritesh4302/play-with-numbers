package ai.bright.maths.ui

import ai.bright.maths.R
import ai.bright.maths.databinding.FragmentLevelSelectionBinding
import ai.bright.maths.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

class LevelSelectionFragment : Fragment(R.layout.fragment_level_selection) {

    private val binding by viewBinding(FragmentLevelSelectionBinding::bind)

    private var navController: NavController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.apply {
            arrayOf(level1, level2, level3, level4).forEach { level ->
                level.setOnClickListener {
                    val gameLevel = it.tag.toString().toInt()
                    val action =
                        LevelSelectionFragmentDirections.actionLevelSelectionFragmentToQuestionFragment2(
                            gameLevel
                        )
                    navController?.navigate(action)
                }
            }
        }
    }
}