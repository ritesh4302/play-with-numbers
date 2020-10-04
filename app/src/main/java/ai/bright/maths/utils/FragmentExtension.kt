package ai.bright.maths.utils

import ai.bright.maths.ui.MainActivity
import androidx.fragment.app.Fragment

fun Fragment.showProgressBar() {
    (requireActivity() as MainActivity).showProgressBar()
}

fun Fragment.hideProgressBar() {
    (requireActivity() as MainActivity).hideProgressBar()
}