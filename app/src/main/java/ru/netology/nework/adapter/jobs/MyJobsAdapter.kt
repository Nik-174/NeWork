package ru.netology.nework.adapter.jobs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.databinding.CardJobsBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.util.DataConverter

interface OnClickListener {
    fun onEditJob(job: Job) {}
    fun onRemoveJob(job: Job) {}
}

class MyJobsAdapter(private val onClickListener: OnClickListener) : ListAdapter<Job,
        JobMyWallViewHolder>(JobMyWallDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobMyWallViewHolder {
        val binding = CardJobsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobMyWallViewHolder(binding, onClickListener)
    }

    override fun onBindViewHolder(holder: JobMyWallViewHolder, position: Int) {
        val job = getItem(position)
        holder.bind(job)
    }
}

class JobMyWallViewHolder(
    private val binding: CardJobsBinding,
    private val onClickListener: OnClickListener
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("NewApi")
    fun bind(job: Job) {
        binding.apply {
            nameOrganization.text = job.name
            position.text = job.position
            start.text = DataConverter.convertDataTimeJob(job.start)
            finish.text = job.finish?.let { DataConverter.convertDataTimeJob(it) }
            link.text = job.link
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onClickListener.onRemoveJob(job)
                                true
                            }
                            R.id.edit -> {
                                onClickListener.onEditJob(job)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

class JobMyWallDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}