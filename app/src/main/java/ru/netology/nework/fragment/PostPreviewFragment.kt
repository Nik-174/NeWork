package ru.netology.nework.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth.Companion.avatar
import ru.netology.nework.databinding.FragmentOpenPostBinding
import ru.netology.nework.enumeration.AttachmentType
import ru.netology.nework.fragment.ImageFragment.Companion.textArg
import ru.netology.nework.fragment.FeedFragment.Companion.intArg
import ru.netology.nework.fragment.MapFragment.Companion.pointArg
import ru.netology.nework.util.DataConverter
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.PostViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class PostPreviewFragment : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()
    private val postViewModel: PostViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOpenPostBinding.inflate(
            inflater,
            container,
            false
        )

        if (arguments?.intArg != null) {
            val id = arguments?.intArg
            id?.let { postViewModel.getPost(it) }
        } else {
            postViewModel.getPost(0)
        }
        binding.allPost.visibility = View.GONE


        postViewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.refreshing

            if (state.loading) {
                Snackbar.make(binding.root, R.string.problem_loading, Snackbar.LENGTH_SHORT).show()
            }
        }

        postViewModel.postResponse.observe(viewLifecycleOwner) { post ->
            if (post.id == arguments?.intArg) {
                binding.allPost.visibility = View.VISIBLE
                binding.postContent.apply {
                    mentionedMe.visibility = View.GONE
                    video.visibility = View.GONE
                    videoField.visibility = View.GONE

                    Glide.with(this@PostPreviewFragment)
                        .load(post.authorAvatar)
                        .error(R.drawable.ic_no_avatar_48)
                        .timeout(10_000)
                        .circleCrop()
                        .into(avatar)


                    if (post.attachment?.url != "") {
                        when (post.attachment?.type) {
                            AttachmentType.IMAGE -> {
                                videoField.visibility = View.VISIBLE
                            }
                            AttachmentType.VIDEO -> {
                                video.visibility = View.VISIBLE
                                videoField.visibility = View.VISIBLE
                            }
                            AttachmentType.AUDIO -> {}
                            null -> {
                                video.visibility = View.GONE
                                videoField.visibility = View.GONE
                            }
                        }
                        Glide.with(this@PostPreviewFragment).load(post.attachment?.url)
                            .timeout(10_000).into(videoField)
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
                    menu.visibility =
                        if (post.authorId == authViewModel.data.value?.id?.toInt()) View.VISIBLE else View.INVISIBLE
                    mentions.text = "${post.mentionIds.size}"

                    menu.setOnClickListener {
                        PopupMenu(it.context, it).apply {
                            inflate(R.menu.options_post)
                            setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.remove -> {
                                        postViewModel.removeById(post.id)
                                        findNavController().navigateUp()
                                        true
                                    }
                                    R.id.edit -> {
                                        findNavController().navigate(
                                            R.id.newPostFragment,
                                            Bundle().apply { intArg = post.id })
                                        true
                                    }

                                    else -> false
                                }
                            }
                        }.show()
                    }

                    like.setOnClickListener {
                        if (authViewModel.authenticated) {
                            if (!post.likedByMe) postViewModel.likeById(post.id) else postViewModel.disLikeById(
                                post.id
                            )
                        } else {
                            Snackbar.make(binding.root, R.string.To_continue, Snackbar.LENGTH_SHORT)
                                .show()
                            findNavController().navigate(R.id.authenticationFragment)
                        }
                    }

                    share.setOnClickListener {
                        if (authViewModel.authenticated) {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, post.content)
                                type = "text/plain"
                            }

                            val shareIntent =
                                Intent.createChooser(intent, getString(R.string.chooser_share_post))
                            startActivity(shareIntent)
                        } else {
                            Snackbar.make(binding.root, R.string.To_continue, Snackbar.LENGTH_SHORT)
                                .show()
                            findNavController().navigate(R.id.authenticationFragment)
                        }
                    }

                    videoField.setOnClickListener {
                        it.findNavController().navigate(
                            R.id.displayingImagesFragment2,
                            Bundle().apply { textArg = post.attachment?.url ?: " " })
                    }
                    mentions.setOnClickListener {
                        if (authViewModel.authenticated) {
                            if (post.mentionIds.isEmpty()) {
                                Snackbar.make(
                                    binding.root,
                                    R.string.mention_anyone,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else {
                                postViewModel.loadUsersMentions(post.mentionIds)
                                findNavController().navigate(R.id.listOfMentions)
                            }
                        } else {
                            Snackbar.make(binding.root, R.string.To_continue, Snackbar.LENGTH_SHORT)
                                .show()
                            findNavController().navigate(R.id.authenticationFragment)
                        }
                    }

                    author.setOnClickListener {
                        val idAuthor = post.authorId.toString()
                        findNavController().navigate(
                            R.id.userJobFragment,
                            Bundle().apply { textArg = idAuthor })
                    }

                    geo.setOnClickListener { view ->
                        view.findNavController().navigate(
                            R.id.mapFragment,
                            Bundle().apply {
                                Point(
                                    post.coords?.lat!!.toDouble(), post.coords.long.toDouble()
                                ).also { pointArg = it }
                            })
                    }

                }
            }
        }
        return binding.root
    }
}