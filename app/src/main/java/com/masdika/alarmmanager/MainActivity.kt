package com.masdika.alarmmanager

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.masdika.alarmmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var timePicker: MaterialTimePicker
    private lateinit var calendarPicker: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        createNotificationChannel()
        binding.setTimeButton.setOnClickListener(this)
        binding.setAlarmButton.setOnClickListener(this)
        binding.cancelAlarmButton.setOnClickListener(this)

    }

    private fun createNotificationChannel() {
        val name: CharSequence = "Emergency Alarm"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("Masdika", name, importance)
        channel.description = "Wakey Wakey"
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(channel)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.setTimeButton -> {
                showTimePicker()
            }

            R.id.setAlarmButton -> {
                setAlarm()
            }

            R.id.cancelAlarmButton -> {
                cancelAlarm()
            }
        }
    }

    private fun showTimePicker() {
        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()
        timePicker.show(supportFragmentManager, "Masdika")

        timePicker.addOnPositiveButtonClickListener {
            val formattedTime = String.format("%02d", timePicker.hour) + " : " + String.format(
                "%02d",
                timePicker.minute
            )
            binding.dateTimeTV.text = formattedTime

            calendarPicker = Calendar.getInstance()
            calendarPicker[Calendar.HOUR_OF_DAY] = timePicker.hour
            calendarPicker[Calendar.MINUTE] = timePicker.minute
            calendarPicker[Calendar.SECOND] = 0
            calendarPicker[Calendar.MILLISECOND] = 0
        }
    }

    private fun setAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendarPicker.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )

        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
        Toast.makeText(this, "Alarm cancel", Toast.LENGTH_SHORT).show()
    }
}