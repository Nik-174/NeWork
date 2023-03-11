package ru.netology.nework.fragment.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.R
import ru.netology.nework.adapter.listOfUsers.AdapterCallback
import ru.netology.nework.adapter.listOfUsers.ListOfUsersChoiceAdapter
import ru.netology.nework.databinding.FaragmenListOfUsersBinding
import ru.netology.nework.viewmodel.NewEventViewModel


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ListOfUsersAddInEvent : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FaragmenListOfUsersBinding.inflate(inflater, container, false)

        val newEventViewModel: NewEventViewModel by activityViewModels()

        newEventViewModel.getUsers()

        val adapter = ListOfUsersChoiceAdapter(object : AdapterCallback {
            override fun isChecked(id: Int) {
                newEventViewModel.isChecked(id)
            }
            override fun unChecked(id: Int) {
                newEventViewModel.unChecked(id)
            }
        })
        binding.list.adapter = adapter

        newEventViewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.loading){
                Snackbar.make(binding.root, R.string.problem_loading, Snackbar.LENGTH_SHORT).show()
            }
        }

        newEventViewModel.data.observe(viewLifecycleOwner) {
            val newUser = adapter.itemCount < it.size
            adapter.submitList(it) {
                if (newUser) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        }

        binding.enter.setOnClickListener {
            newEventViewModel.addSpeakersIds()
            findNavController().navigateUp()
        }
        return binding.root
    }
}