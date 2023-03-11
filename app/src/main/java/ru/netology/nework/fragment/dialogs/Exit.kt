package ru.netology.nework.fragment.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.netology.nework.databinding.FragmentExitBinding
import kotlin.system.exitProcess

class Exit : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentExitBinding.inflate(inflater, container, false)

        binding.button.setOnClickListener {
            finishAffinity(requireActivity())
            exitProcess(0)
        }

        binding.button2.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }
}