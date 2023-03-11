package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nework.R
import ru.netology.nework.databinding.CardMentionBinding
import ru.netology.nework.dto.UserRequest


class UsersListAdapter(private val callback: AdapterUsersIdCallback) :
    ListAdapter<UserRequest, UsersListViewHolder>(UsersListIdDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersListViewHolder {
        val binding = CardMentionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersListViewHolder(binding, callback)
    }

    override fun onBindViewHolder(holder: UsersListViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class UsersListViewHolder(
    private val binding: CardMentionBinding,
    private val callback: AdapterUsersIdCallback
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: UserRequest) {
        binding.apply {
            Glide.with(binding.root)
                .load(user.avatar)
                .error(R.drawable.ic_no_avatar_48)
                .placeholder(R.drawable.ic_no_user_48)
                .timeout(10_000)
                .circleCrop()
                .into(avatar)
            author.text = user.name
            avatar.setOnClickListener {
                callback.goToPageUser(user.id)
            }
            author.setOnClickListener {
                callback.goToPageUser(user.id)
            }
        }
    }
}

class UsersListIdDiffCallback : DiffUtil.ItemCallback<UserRequest>() {
    override fun areItemsTheSame(oldItem: UserRequest, newItem: UserRequest): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserRequest, newItem: UserRequest): Boolean {
        return oldItem == newItem
    }
}