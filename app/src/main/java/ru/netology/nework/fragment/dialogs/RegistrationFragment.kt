package ru.netology.nework.fragment.dialogs


import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentRegistrationBinding
import ru.netology.nework.dto.UserRegistration
import ru.netology.nework.util.AndroidUtils
import ru.netology.nework.viewmodel.AuthViewModel

@AndroidEntryPoint
class RegistrationFragment : DialogFragment() {

    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        var file: MultipartBody.Part? = null
        val enter = binding.enter

        enter.setOnClickListener {
            val usernameEditText = binding.username.text.toString()
            val loginEditText = binding.login.text.toString()
            val passwordEditText = binding.password.text.toString()
            val repeatPasswordEditText = binding.repeatPassword.text.toString()
            val avatarImage = file
            if (passwordEditText != repeatPasswordEditText) {
                Snackbar.make(binding.root, R.string.password_not_match, Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                if (loginEditText == "" || passwordEditText == "") {
                    Snackbar.make(binding.root, R.string.All_fields, Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    val user = UserRegistration(
                        loginEditText,
                        passwordEditText,
                        usernameEditText,
                        avatarImage
                    )
                    viewModel.onSignUp(user)
                    AndroidUtils.hideKeyboard(requireView())
                }
            }
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        val resultFile = uri?.toFile()
                        file = MultipartBody.Part.createFormData(
                            "file", resultFile?.name, resultFile!!.asRequestBody()
                        )
                        binding.avatar.setImageURI(uri)
                    }
                }
            }


        binding.avatar.setOnClickListener {
            ImagePicker.with(this)
                .crop(1F, 1F)
                .compress(2048)
                .provider(ImageProvider.BOTH)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.deleteImage.setOnClickListener {
            binding.avatar.setImageResource(R.drawable.ic_no_avatar_48)
        }

        viewModel.data.observeForever {
            if (it.token != null) {
                findNavController().navigateUp()
            }
        }
        return binding.root
    }
}
