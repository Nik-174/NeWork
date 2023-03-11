package ru.netology.nework.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.ImagePicker.Companion.getError
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.R
import ru.netology.nework.adapter.AdapterUsersIdCallback
import ru.netology.nework.adapter.ListUsersIdAdapter
import ru.netology.nework.databinding.FragmentNewPostBinding
import ru.netology.nework.fragment.ImageFragment.Companion.textArg
import ru.netology.nework.fragment.FeedFragment.Companion.intArg
import ru.netology.nework.fragment.MapFragment.Companion.pointArg
import ru.netology.nework.viewmodel.NewPostViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewPostFragment : Fragment() {

    @SuppressLint("FragmentBackPressedCallback")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        val newPostViewModel: NewPostViewModel by activityViewModels()
        var file: MultipartBody.Part

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            Snackbar.make(binding.root, R.string.be_lost, Snackbar.LENGTH_SHORT)
                .setAction(R.string.exit) {
                    newPostViewModel.deleteEditPost()
                    findNavController().navigate(R.id.feedFragment)
                }.show()
        }

        if (arguments?.intArg != null) {
            val id = arguments?.intArg
            id?.let { newPostViewModel.getPost(it) }
        }


        val adapter = ListUsersIdAdapter(object : AdapterUsersIdCallback {
            override fun goToPageUser(id: Int) {
                val idAuthor = id.toString()
                findNavController().navigate(
                    R.id.userJobFragment,
                    Bundle().apply { textArg = idAuthor })
            }
        })

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        val resultFile = uri?.toFile()
                        file = MultipartBody.Part.createFormData(
                            "file", resultFile?.name, resultFile!!.asRequestBody()
                        )
                        newPostViewModel.addPictureToThePost(file)
                        binding.image.setImageURI(uri)
                    }
                }
            }


        binding.menuAdd.setOnClickListener {
            binding.menuAdd.isChecked = binding.image.isVisible
            ImagePicker.with(this@NewPostFragment)
                .crop()
                .compress(2048)
                .provider(ImageProvider.BOTH)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                        "image/jpg"
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.mentionAdd.setOnClickListener {
            binding.mentionAdd.isChecked =
                newPostViewModel.newPost.value?.mentionIds?.isNotEmpty() ?: false
            findNavController().navigate(R.id.action_newPostFragment_to_listOfUsers)
        }

        binding.geoAdd.setOnClickListener {
            if (newPostViewModel.newPost.value?.coords != null) {
                binding.geoAdd.isChecked = true
                val point = Point(
                    newPostViewModel.newPost.value?.coords!!.lat.toDouble(),
                    newPostViewModel.newPost.value?.coords!!.long.toDouble()
                )
                newPostViewModel.inJob = true
                findNavController().navigate(R.id.action_newPostFragment_to_mapsFragment,
                    Bundle().apply { pointArg = point })
            } else {
                newPostViewModel.inJob = true
                findNavController().navigate(R.id.action_newPostFragment_to_mapsFragment)
            }
        }

        binding.linkAdd.setOnClickListener {
            binding.linkAdd.isChecked = newPostViewModel.newPost.value?.link != null
            binding.editLink.visibility = View.VISIBLE
            binding.okAdd.visibility = View.VISIBLE
        }

        binding.okAdd.setOnClickListener {
            val link: String = binding.editLink.text.toString()
            newPostViewModel.addLink(link)
            binding.editLink.visibility = View.GONE
            binding.okAdd.visibility = View.GONE
        }

        binding.mentionIds.adapter = adapter
        newPostViewModel.mentionsLive.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        newPostViewModel.newPost.observe(viewLifecycleOwner) {
            it.content.let(binding.edit::setText)
            it.link.let(binding.editLink::setText)
            binding.mentionAdd.isChecked = it.mentionIds.isNotEmpty()
            binding.geoAdd.isChecked = newPostViewModel.newPost.value?.coords != null
            binding.linkAdd.isChecked = newPostViewModel.newPost.value?.link != null
            if (it.attachment != null) {
                binding.image.visibility = View.VISIBLE
                Glide.with(this)
                    .load(it.attachment.url)
                    .error(R.drawable.ic_no_image_24)
                    .placeholder(R.drawable.ic_no_user_48)
                    .timeout(10_000)
                    .into(binding.image)
                binding.menuAdd.isChecked = true
            } else {
                binding.menuAdd.isChecked = false
                binding.image.visibility = View.GONE
            }

        }

        binding.image.setOnClickListener {
            newPostViewModel.deletePicture()
        }

        binding.ok.setOnClickListener {
            val content = binding.edit.text.toString()
            if (content == "") {
                Snackbar.make(binding.root, R.string.content_field, Snackbar.LENGTH_SHORT).show()
            } else {
                newPostViewModel.addPost(content)
            }

        }

        newPostViewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        newPostViewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
}