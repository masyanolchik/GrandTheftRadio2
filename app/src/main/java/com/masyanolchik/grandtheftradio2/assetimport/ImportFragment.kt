package com.masyanolchik.grandtheftradio2.assetimport

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.masyanolchik.grandtheftradio2.R
import com.masyanolchik.grandtheftradio2.assetimport.model.AssetImportModel
import com.masyanolchik.grandtheftradio2.assetimport.presenter.AssetImportPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.inject
import org.koin.android.scope.createScope
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * A [Fragment] which is used for importing serialized list of stations.
 * Use the [ImportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImportFragment : Fragment(), AssetImportContract.View {

    private val assetImportPresenter: AssetImportContract.Presenter by inject { parametersOf(this@ImportFragment) }

    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var importTemplateButton: MaterialButton
    private lateinit var copyTemplateButton: MaterialButton

    private var requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }
    private var requestFileLocationLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            result.data?.let {
                if(result.resultCode == AppCompatActivity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        requireContext().contentResolver.openInputStream(uri)?.bufferedReader()
                            ?.use { it.readText() }?.let {
                                assetImportPresenter.processImportedJsonString(it)
                            }
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_import, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressIndicator = view.findViewById(R.id.loading_indicator)

        copyTemplateButton = view.findViewById<MaterialButton>(R.id.copy_template_button).apply {
            setOnClickListener {
                requestPermission(
                    rationaleText = context.getString(R.string.write_file_permission_ratio),
                    rationaleActionText = context.getString(R.string.grant_permission_action),
                    requestedPermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) {
                    saveTemplateToExternalStorage()
                }
            }
        }

        importTemplateButton = view.findViewById<MaterialButton>(R.id.import_template_button).apply {
            setOnClickListener {
                requestPermission(
                    rationaleText = context.getString(R.string.read_file_permission_ratio),
                    rationaleActionText = context.getString(R.string.grant_permission_action),
                    requestedPermission = android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) {
                    chooseFile()
                }
            }
        }
    }

    override fun showImportProgress() {
        requireActivity().runOnUiThread {
            copyTemplateButton.isVisible = false
            importTemplateButton.isVisible = false
            progressIndicator.isVisible = true
        }
    }

    override fun hideImportProgress(status: String) {
        requireActivity().runOnUiThread {
            progressIndicator.isVisible = false
            copyTemplateButton.isVisible = true
            importTemplateButton.isVisible = true
            Snackbar.make(importTemplateButton,status,Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        assetImportPresenter.onDestroy()
    }

    private fun chooseFile() {
        val intent = Intent()
        intent.type = ANY_MIME_TYPE
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        //launch picker screen
        requestFileLocationLauncher.launch(intent)
    }

    private fun saveTemplateToExternalStorage(
        filename: String = EXPORTED_TEMPLATE_NAME,
        mimeType: String = ASSET_MIME_TYPE,
        directory: String = Environment.DIRECTORY_DOWNLOADS,
    ) {
        val context = requireContext()
        if (!File(Environment.getExternalStoragePublicDirectory(directory), filename).exists()) {
            var templateOutputStream: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val mediaContentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, filename)
                    put(MediaStore.Downloads.MIME_TYPE, mimeType)
                    put(MediaStore.Downloads.RELATIVE_PATH, directory)
                }


                context.contentResolver.run {
                    val uri = context.contentResolver.insert(mediaContentUri, values) ?: return
                    templateOutputStream = openOutputStream(uri) ?: return
                }
                val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
                startActivity(intent)
            } else {
                val templatePath = Environment.getExternalStoragePublicDirectory(directory)
                val template = File(templatePath, filename)
                templateOutputStream = FileOutputStream(template)
            }


            templateOutputStream?.let {
                it.use { outputStream ->
                    val templateBytes = context.assets.open(ASSET_FILE_NAME).readBytes()
                    outputStream.write(templateBytes)
                }
                Snackbar.make(
                    requireView(),
                    getString(
                        R.string.template_is_in_your_downloads_folder,
                    ),
                    Snackbar.LENGTH_SHORT
                ).show()
            }

        } else {
            Snackbar.make(
                requireView(),
                getString(R.string.template_is_in_your_downloads_folder),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun requestPermission(
        rationaleText: String,
        rationaleActionText: String,
        requestedPermission: String,
        blockWithPermission: () -> Unit = {},
        ) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> blockWithPermission.invoke()
            ContextCompat.checkSelfPermission(requireContext(), requestedPermission) == PackageManager.PERMISSION_GRANTED -> {
                blockWithPermission.invoke()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), requestedPermission) -> {
                Snackbar
                    .make(
                        requireView(),
                        rationaleText,
                        Snackbar.LENGTH_INDEFINITE)
                    .setAction(rationaleActionText) {
                        requestPermissionLauncher.launch(requestedPermission)
                    }
                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(requestedPermission)
            }
        }
    }

    companion object {
        private const val ASSET_FILE_NAME = "stations.json"
        private const val EXPORTED_TEMPLATE_NAME = "stations_template.json"
        private const val ASSET_MIME_TYPE = "application/json"
        private const val ANY_MIME_TYPE = "*/*"

        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment ImportFragment.
         */
        @JvmStatic
        fun newInstance() = ImportFragment()
    }
}