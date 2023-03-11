package ru.netology.nework.fragment.wall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.R
import ru.netology.nework.adapter.jobs.MyJobsAdapter
import ru.netology.nework.adapter.jobs.OnClickListener
import ru.netology.nework.databinding.FragmentMyWallJobBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.JobViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MyJobFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModels()
    private val viewModel: JobViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMyWallJobBinding.inflate(inflater, container, false)
        viewModel.getMyJob()

        val adapter = MyJobsAdapter(object : OnClickListener {
            override fun onEditJob(job: Job) {
                viewModel.editJob(job)
                findNavController().navigate(R.id.newJobFragment)
            }

            override fun onRemoveJob(job: Job) {
                viewModel.removeById(job.id)
            }
        })

        authViewModel.data.observe(viewLifecycleOwner) {
            binding.nameUser.text = it.nameUser
            Glide.with(this@MyJobFragment)
                .load(it.avatarUser)
                .error(R.drawable.ic_no_avatar_48)
                .placeholder(R.drawable.ic_no_user_48)
                .timeout(15_000)
                .circleCrop()
                .into(binding.avatar)
        }

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) {
            val newJob = adapter.itemCount < it.size
            adapter.submitList(it) {
                if (newJob) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.newJobFragment)
        }

        binding.menu.setOnClickListener {
            findNavController().navigate(R.id.myPostFragment)
        }

        binding.home.setOnClickListener {
            findNavController().navigate(R.id.feedFragment)
        }

        return binding.root
    }
}