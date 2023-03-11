package ru.netology.nework.fragment

import android.annotation.SuppressLint
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
import ru.netology.nework.databinding.FragmentOpenEventBinding
import ru.netology.nework.enumeration.AttachmentType
import ru.netology.nework.enumeration.EventType
import ru.netology.nework.fragment.ImageFragment.Companion.textArg
import ru.netology.nework.fragment.EventFragment.Companion.intArg
import ru.netology.nework.fragment.MapFragment.Companion.pointArg
import ru.netology.nework.util.DataConverter
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.EventViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class EventPreviewFragment : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()
    private val viewModel: EventViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOpenEventBinding.inflate(
            inflater,
            container,
            false
        )
        if (arguments?.intArg != null) {
            val id = arguments?.intArg
            id?.let { viewModel.getEvent(it) }
        } else {
            viewModel.getEvent(0)
        }
        binding.allEvent.visibility = View.GONE

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.refreshing

            if (state.loading) {
                Snackbar.make(binding.root, R.string.problem_loading, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.eventResponse.observe(viewLifecycleOwner) { event ->
            if (event.id == arguments?.intArg) {
                binding.allEvent.visibility = View.VISIBLE
                binding.eventContent.apply {
                    video.visibility = View.GONE
                    videoField.visibility = View.GONE

                    Glide.with(this@EventPreviewFragment)
                        .load(event.authorAvatar)
                        .error(R.drawable.ic_no_avatar_48)
                        .placeholder(R.drawable.ic_no_user_48)
                        .timeout(10_000)
                        .circleCrop()
                        .into(avatar)


                    if (event.attachment != null) {
                        when (event.attachment.type) {
                            AttachmentType.IMAGE -> {
                                videoField.visibility = View.VISIBLE
                            }
                            AttachmentType.VIDEO -> {
                                video.visibility = View.VISIBLE
                                videoField.visibility = View.VISIBLE
                            }
                            AttachmentType.AUDIO -> {}
                        }
                        Glide.with(this@EventPreviewFragment).load(event.attachment.url)
                            .timeout(10_000).into(videoField)
                    }

                    when (event.type) {
                        EventType.OFFLINE -> type.setImageResource(R.drawable.ic_offline_32)
                        EventType.ONLINE -> type.setImageResource(R.drawable.ic_online_32)
                    }
                    author.text = event.author
                    published.text = DataConverter.convertDataTime(event.published)
                    dateTime.text = DataConverter.convertDataTime(event.datetime)
                    content.text = event.content + if (event.link != null) {
                        "\n" + event.link
                    } else {
                        ""
                    }
                    like.isChecked = event.likedByMe
                    like.text = "${event.likeOwnerIds.size}"
                    participate.isChecked = event.participatedByMe
                    participate.text = "${event.participantsIds.size}"
                    geo.visibility = if (event.coords != null) View.VISIBLE else View.INVISIBLE
                    menu.visibility = if (event.ownerByMe) View.VISIBLE else View.INVISIBLE
                    speakers.text = "${event.speakerIds.size}"

                    menu.setOnClickListener {
                        PopupMenu(it.context, it).apply {
                            inflate(R.menu.options_post)
                            setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.remove -> {
                                        viewModel.removeById(event.id)
                                        true
                                    }
                                    R.id.edit -> {
                                        findNavController().navigate(R.id.newEventFragment,
                                            Bundle().apply { intArg = event.id })
                                        true
                                    }

                                    else -> false
                                }
                            }
                        }.show()
                    }

                    like.setOnClickListener {
                        if (authViewModel.authenticated) {
                            if (!event.likedByMe) viewModel.likeById(event.id) else viewModel.disLikeById(
                                event.id
                            )
                        } else {
                            Snackbar.make(binding.root, R.string.To_continue, Snackbar.LENGTH_SHORT)
                                .show()
                            findNavController().navigate(R.id.authenticationFragment)
                        }
                    }
                    participate.setOnClickListener {
                        if (authViewModel.authenticated) {
                            if (!event.participatedByMe) viewModel.participateInEvent(event.id) else viewModel.doNotParticipateInEvent(
                                event.id
                            )
                        } else {
                            Snackbar.make(binding.root, R.string.To_continue, Snackbar.LENGTH_SHORT)
                                .show()
                            findNavController().navigate(R.id.authenticationFragment)
                        }
                    }
                    participate.setOnLongClickListener {
                        if (authViewModel.authenticated) {
                            if (event.participantsIds.isEmpty()) {
                                Snackbar.make(
                                    binding.root,
                                    R.string.mention_anyone,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else {
                                viewModel.goToUserParticipateInEvent(event.participantsIds)
                                findNavController().navigate(R.id.listOfSpeakers)
                            }
                        } else {
                            Snackbar.make(binding.root, R.string.To_continue, Snackbar.LENGTH_SHORT)
                                .show()
                            findNavController().navigate(R.id.authenticationFragment)
                        }
                        true
                    }

                    videoField.setOnClickListener {
                        it.findNavController().navigate(
                            R.id.displayingImagesFragment2,
                            Bundle().apply { textArg = event.attachment?.url ?: " " })
                    }
                    speakers.setOnClickListener {
                        if (authViewModel.authenticated) {
                            if (event.speakerIds.isEmpty()) {
                                Snackbar.make(
                                    binding.root,
                                    R.string.mention_anyone,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else {
                                viewModel.loadUsersSpeakers(event.speakerIds)
                                findNavController().navigate(R.id.listOfSpeakers)
                            }
                        } else {
                            Snackbar.make(binding.root, R.string.To_continue, Snackbar.LENGTH_SHORT)
                                .show()
                            findNavController().navigate(R.id.authenticationFragment)
                        }
                    }

                    author.setOnClickListener {
                        val idAuthor = event.authorId.toString()
                        findNavController().navigate(
                            R.id.userJobFragment,
                            Bundle().apply { textArg = idAuthor })
                    }

                    avatar.setOnClickListener {
                        val idAuthor = event.authorId.toString()
                        findNavController().navigate(
                            R.id.userJobFragment,
                            Bundle().apply { textArg = idAuthor })
                    }

                    geo.setOnClickListener { view ->
                        view.findNavController().navigate(
                            R.id.mapFragment,
                            Bundle().apply {
                                Point(
                                    event.coords?.lat!!.toDouble(), event.coords.long.toDouble()
                                ).also { pointArg = it }
                            })
                    }
                }
            }
        }
        return binding.root
    }
}