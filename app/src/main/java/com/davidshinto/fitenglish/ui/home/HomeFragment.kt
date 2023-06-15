package com.davidshinto.fitenglish.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.davidshinto.fitenglish.MainActivity
import com.davidshinto.fitenglish.R
import com.davidshinto.fitenglish.databinding.FragmentHomeBinding
import com.davidshinto.fitenglish.utils.startAnimation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val CHANNEL_ID = "channel_id"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupPlayButton()

        val database = FirebaseDatabase.getInstance("https://fitenglishdb-default-rtdb.europe-west1.firebasedatabase.app/")
        val dbRefWOTD = database.getReference("/WordOfTheDay")
        dbRefWOTD.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                setWordOfTheDay(dataSnapshot.value.toString())

                createNotificationChannel(requireContext())

                showNotification(requireContext(), "Word of the day", "Your new word of the day is ready!")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context,"An error happened while getting database data: $databaseError",
                    Toast.LENGTH_SHORT).show()
            }
        })

        return root
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel"
            val description = "My Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                this.description = description
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        builder.color = Color.BLUE

        val notificationManager =
            context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, builder.build())
    }

    private fun setupPlayButton(){
        val explosionAnim = getExplosionAnim()
        binding.fab.setOnClickListener{
            binding.fab.isVisible = false
            binding.elipse.isVisible = true
            binding.elipse.startAnimation(explosionAnim){
                this.context?.let { _ ->
                    it.findNavController().navigate(R.id.action_navigation_home_to_navigation_game_conf)
                }
            }
        }
    }

    fun setWordOfTheDay(word: String) {
        binding.tvWordOfTheDayText.text = word
    }

    private fun getExplosionAnim(): Animation {
        val animation =
            AnimationUtils.loadAnimation(this.context, R.anim.elipse_explosion_anim).apply {
                duration = 700
                interpolator = AccelerateDecelerateInterpolator()
            }
        return animation
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}