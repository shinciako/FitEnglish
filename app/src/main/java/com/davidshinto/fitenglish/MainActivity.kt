package com.davidshinto.fitenglish

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowMetrics
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.davidshinto.fitenglish.databinding.ActivityMainBinding
import com.davidshinto.fitenglish.utils.WidthProvider
import com.davidshinto.fitenglish.utils.Word
import com.davidshinto.fitenglish.utils.WordList
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity(), WidthProvider {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNav()
        FirebaseApp.initializeApp(this)
        loadDataFromDB()
    }

    private fun setupNav() {
        navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_finder,
                R.id.navigation_cards,
                R.id.navigation_history
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        navView.setupWithNavController(navController)
        setupSelectedFragmentIcon(navController)
    }

    private fun setupSelectedFragmentIcon(navController: NavController) {
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id != navView.selectedItemId) {
                controller.backQueue.asReversed().drop(1).forEach { entry ->
                    navView.menu.forEach { item ->
                        if (entry.destination.id == item.itemId) {
                            item.isChecked = true
                            return@addOnDestinationChangedListener
                        }
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
    }

    override fun getWidth(): Int {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                WindowInsetsCompat.Type.systemBars()
            )
            displayMetrics.widthPixels = windowMetrics.bounds.width() - insets.left - insets.right
        } else { //earlier version of Android
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        return displayMetrics.widthPixels
    }

    private fun loadDataFromDB()
    {
        val database = FirebaseDatabase.getInstance("https://fitenglishdb-default-rtdb.europe-west1.firebasedatabase.app/")
        val dbRef = database.getReference("wordList/")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataList = mutableListOf<Word>()
                for (snapshot in dataSnapshot.children) {
                    val data = snapshot.getValue(Word::class.java)
                    data?.let { dataList.add(it) }
                }
                dataList.sortBy { it.engName }
                WordList.wordList = dataList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext,"An error happened while getting database data: $databaseError",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }
}