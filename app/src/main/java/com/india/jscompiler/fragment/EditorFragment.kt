package com.india.jscompiler.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.india.jscompiler.R
import com.india.jscompiler.data.AppDatabase
import com.india.jscompiler.data.WorkspaceEntity
import com.india.jscompiler.databinding.FragmentEditorBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditorFragment : Fragment() {

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!

    private var currentWorkspaceId: Long = -1L
    private var currentWorkspace: WorkspaceEntity? = null
    private val database by lazy { AppDatabase.getDatabase(requireContext()) }
    private val workspaceDao by lazy { database.workspaceDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            currentWorkspaceId = it.getLong("workspaceId", -1L)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView()
        loadWorkspace()

        binding.btnClearConsole.setOnClickListener {
            binding.tvConsole.text = ""
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webViewEditor.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true

            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    currentWorkspace?.let {
                        view?.evaluateJavascript("setCode(`${it.code}`)", null)
                    }
                }
            }

            addJavascriptInterface(WebAppInterface(), "Android")
            loadUrl("file:///android_asset/editor.html")
        }
    }

    private fun loadWorkspace() {
        if (currentWorkspaceId != -1L) {
            lifecycleScope.launch {
                currentWorkspace = workspaceDao.getWorkspaceById(currentWorkspaceId)
                currentWorkspace?.let {
                    binding.webViewEditor.evaluateJavascript("setCode(`${it.code}`)", null)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_editor, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_run -> {
                runCode()
                true
            }
            R.id.action_save -> {
                saveCode()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun runCode() {
        binding.tvConsole.text = "--- Running ---\n"
        binding.webViewEditor.evaluateJavascript("runCode()", null)
    }

    private fun saveCode() {
        binding.webViewEditor.evaluateJavascript("getCode()") { code ->
            val cleanCode = code?.removeSurrounding("\"")?.replace("\\n", "\n")?.replace("\\\"", "\"") ?: ""
            currentWorkspace?.let {
                val updated = it.copy(code = cleanCode, lastUpdated = System.currentTimeMillis())
                lifecycleScope.launch(Dispatchers.IO) {
                    workspaceDao.updateWorkspace(updated)
                }
            }
        }
    }

    inner class WebAppInterface {
        @JavascriptInterface
        fun onLog(message: String) {
            lifecycleScope.launch(Dispatchers.Main) {
                binding.tvConsole.append("$message\n")
            }
        }

        @JavascriptInterface
        fun onError(message: String) {
            lifecycleScope.launch(Dispatchers.Main) {
                binding.tvConsole.append("ERROR: $message\n")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
