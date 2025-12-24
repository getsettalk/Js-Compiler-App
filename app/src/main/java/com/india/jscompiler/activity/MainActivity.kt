package com.india.jscompiler.activity

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.india.jscompiler.R
import com.india.jscompiler.adapter.WorkspaceAdapter
import com.india.jscompiler.data.AppDatabase
import com.india.jscompiler.data.WorkspaceEntity
import com.india.jscompiler.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val workspaceDao by lazy { database.workspaceDao() }
    private lateinit var adapter: WorkspaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupWorkspaceList()

        binding.btnNewWorkspace.setOnClickListener {
            createNewWorkspace()
        }
    }

    private fun setupWorkspaceList() {
        adapter = WorkspaceAdapter { workspace ->
            openWorkspace(workspace.id)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.rvWorkspaces.layoutManager = LinearLayoutManager(this)
        binding.rvWorkspaces.adapter = adapter
        
        lifecycleScope.launch {
            workspaceDao.getAllWorkspaces().collectLatest { workspaces ->
                adapter.submitList(workspaces)
            }
        }
    }

    private fun createNewWorkspace() {
        lifecycleScope.launch {
            val newWorkspace = WorkspaceEntity(
                title = "New Script ${System.currentTimeMillis() % 1000}",
                code = "console.log('Hello World');\n\n// Try ES6+\nconst greet = (name) => `Hello, \${name}!`;\nconsole.log(greet('Developer'));",
                jsVersion = "ES6"
            )
            val id = workspaceDao.insertWorkspace(newWorkspace)
            openWorkspace(id)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun openWorkspace(id: Long) {
        val navController = findNavController(R.id.nav_host_fragment)
        val bundle = Bundle().apply {
            putLong("workspaceId", id)
        }
        navController.navigate(R.id.editorFragment, bundle)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
