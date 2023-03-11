package ru.netology.nework.adapter.posts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.R
import ru.netology.nework.databinding.CardPostBinding
import ru.netology.nework.dto.PostResponse
import ru.netology.nework.enumeration.AttachmentType.*
import ru.netology.nework.fragment.ImageFragment.Companion.textArg
import ru.netology.nework.fragment.FeedFragment.Companion.intArg
import ru.netology.nework.fragment.MapFragment.Companion.pointArg
import ru.netology.nework.util.DataConverter

interface OnInteractionListener {
    fun onLike(post: PostResponse) {}
    fun onEdit(post: PostResponse) {}
    fun onRemove(post: PostResponse) {}
    fun onShare(post: PostResponse) {}
    fun loadingTheListOfMentioned(post: PostResponse) {}
    fun goToPageUser(post: PostResponse) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<PostResponse, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bind(post)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("NewApi", "SetTextI18n")
    fun bind(post: PostResponse) {
        binding.apply {
            mentionedMe.visibility = View.GONE
            video.visibility = View.GONE
            videoField.visibility = View.GONE
            Glide.with(itemView)
                .load(post.authorAvatar)
                .error(R.drawable.ic_no_avatar_48)
                .placeholder(R.drawable.ic_no_user_48)
                .timeout(10_000)
                .circleCrop()
                .into(avatar)


            if (post.attachment?.url != "") {
                when (post.attachment?.type) {
                    IMAGE -> {
                        videoField.visibility = View.VISIBLE
                    }
                    VIDEO -> {
                        video.visibility = View.GONE
                        videoField.visibility = View.GONE

                    }
                    AUDIO -> {}
                    null -> {
                        video.visibility = View.GONE
                        videoField.visibility = View.GONE
                    }
                }
                Glide.with(itemView).load(post.attachment?.url).timeout(10_000)
                    .into(videoField)
            }
            author.text = post.author
            published.text = DataConverter.convertDataTime(post.published)
            content.text = post.content + if (post.link != null) {
                "\n" + post.link
            } else {
                ""
            }
            like.isChecked = post.likedByMe
            like.text = "${post.likeOwnerIds.size}"
            geo.visibility = if (post.coords != null) View.VISIBLE else View.INVISIBLE
            mentionedMe.visibility = if (post.mentionedMe) View.VISIBLE else View.INVISIBLE
            menu.visibility = if (post.ownerByMe) View.VISIBLE else View.INVISIBLE
            mentions.text = "${post.mentionIds.size}"

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            videoField.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.displayingImagesFragment2,
                        Bundle().apply { textArg = post.attachment?.url ?: " " })
            }
            mentions.setOnClickListener {
                onInteractionListener.loadingTheListOfMentioned(post)
            }

            author.setOnClickListener {
                onInteractionListener.goToPageUser(post)
            }
            content.setOnClickListener {
                it.findNavController().navigate(
                    R.id.action_feedFragment_to_openPostFragment2,
                    Bundle().apply { intArg = post.id })
            }

            geo.setOnClickListener { view ->
                view.findNavController().navigate(R.id.action_feedFragment_to_mapsFragment,
                    Bundle().apply {
                        Point(
                            post.coords?.lat!!.toDouble(), post.coords.long.toDouble()
                        ).also { pointArg = it }
                    })
            }

        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<PostResponse>() {
    override fun areItemsTheSame(oldItem: PostResponse, newItem: PostResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PostResponse, newItem: PostResponse): Boolean {
        return oldItem == newItem
    }
}