package meeting.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.Chronometer
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_meeting.*
import java.util.*
import kotlin.concurrent.timer
import android.R
import android.content.Intent
import android.view.MenuInflater
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.Menu
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate


class MeetingActivity : AppCompatActivity() {

    private var isTimerRunning = false
    private var pauseOffset = 0L
    private var persons : Int = 0
    private var salary : Int = 0

    var time = ""
    var currentDate = ""

    var meetingCost = 0.00
    var costValue = 0.00

    val myDb: DatabaseHelper = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(meeting.app.R.layout.activity_meeting)

        val bundle :Bundle ?=intent.extras
        persons = bundle!!.getInt("EXTRA_PERSONS")
        salary = bundle.getInt("EXTRA_SALARY")

        val formatter = SimpleDateFormat("dd.M.yyyy")
        currentDate = formatter.format(Date())

        startStop()
        buttonClick.setOnClickListener{
            startStop()
        }
        buttonClick.setOnLongClickListener{
            longClick()
            true
        }
        chronometer.setOnChronometerTickListener {
            updateUi()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(meeting.app.R.menu.menu_meeting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            meeting.app.R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startStop(){
        if (isTimerRunning){
            isTimerRunning = false
            chronometer.stop()
            buttonClick.text = getString(meeting.app.R.string.button_start)
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
        }
        else{
            isTimerRunning = true
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            chronometer.start()
            buttonClick.text = getString(meeting.app.R.string.button_stop)
        }
    }

    private fun longClick(){
        time = chronometer.text.toString()
        showSummary(time)
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.stop()
        pauseOffset = 0
        buttonClick.text = getString(meeting.app.R.string.button_start)
    }

    private fun updateUi(){
        val time = SystemClock.elapsedRealtime() - chronometer.base
        meetingCost = (time.toDouble() / 3600000) * persons.toFloat() * salary.toFloat()
        val costString = "Palaveri on maksanut: %.2f"  .format(meetingCost) + "€"
        textViewTime.text = costString

        costValue = meetingCost / 4.15
        val costString2 = "Työntekijän tunnit että palaveri on maksettu: %.1f" .format(costValue)
        textViewValue.text = costString2
    }

    private fun showSummary(time: String){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(meeting.app.R.layout.summary_view_dialog,null)
        dialogBuilder.setView(dialogView)

        val textView = dialogView.findViewById<TextView>(meeting.app.R.id.dialogTextView)
        val summaryString = "Palaverin kesto: " + time +
                "\nPalaverin hinta: %.2f" .format(meetingCost) + "€" +
                "\nTyötunnit palaverin kustannukseen: %.1f" .format(costValue) + "h"
        textView.text = summaryString

        dialogBuilder.setTitle("Tallennetaanko palaverin tiedot")//"Tallennetaanko palaverin tiedot"
        dialogBuilder.setPositiveButton("Kyllä"){dialog,_ ->
            addData(currentDate, summaryString)
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Ei") {dialog,_ ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun addData(date: String, summary: String){
        val isInserted = myDb.insertData(date, summary)
        if (isInserted > -1){
            Toast.makeText(applicationContext, "Data Inserted", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(applicationContext, "Error Inserting Data", Toast.LENGTH_LONG).show()
        }
    }
}