package ai.bright.maths.ui

import ai.bright.maths.databinding.ActivityMainBinding
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)
    }

    fun showProgressBar() {
        binding?.progressBar?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        binding?.progressBar?.visibility = View.GONE
    }
}
