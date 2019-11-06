package meeting.app

import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.util.ArrayList
import java.util.HashMap


class MainActivity : AppCompatActivity() {

    val myDb: DatabaseHelper = DatabaseHelper(this)
    val id = "id"
    val date = "date"
    val summary = "summary"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton.setOnClickListener{
            startMeeting()
        }

        showDataButton.setOnClickListener {
            getData()
        }
    }

    private fun validate(): Boolean {
        if (numberOfPersonsView.text.isEmpty()) return false
        if (salaryView.text.isEmpty()) return false
        return true
    }

    fun startMeeting(){
        if (validate()){
            val persons: Int = numberOfPersonsView.text.toString().toInt()
            val salary: Int = salaryView.text.toString().toInt()
            val intent = Intent(this, MeetingActivity::class.java)
            val extras = Bundle()
            extras.putInt("EXTRA_PERSONS",persons)
            extras.putInt("EXTRA_SALARY",salary)
            intent.putExtras(extras)
            startActivity(intent)
        }else{
            Toast.makeText(applicationContext, "Täytä kaikki tiedot! ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showData(title: String, dataList: ArrayList<HashMap<String, String>>){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.list_view_dialog,null)
        dialogBuilder.setView(dialogView)

        val textView = dialogView.findViewById<TextView>(R.id.title)
        val listView = dialogView.findViewById<ListView>(R.id.listView)
        val button = dialogView.findViewById<Button>(R.id.button)

        textView.text = title


        val adapter = SimpleAdapter(
            this, dataList, R.layout.custom_list_item,
            arrayOf(id, date, summary),
            intArrayOf(R.id.text1, R.id.text2, R.id.text3)
        )
        listView.setAdapter(adapter)

        //AdapterView.OnItemClickListener
        listView.setOnItemClickListener{ _, view, position,_ ->
            val clickedId = (view.findViewById(R.id.text1) as TextView).text.toString()

            val adb = AlertDialog.Builder(this)
            adb.setMessage("Haluatko poistaa valitun palaverin?")
            adb.setCancelable(false)
            adb.setNegativeButton("Ei", null)
            adb.setPositiveButton("Kyllä"){_,_ ->
                deleteData(clickedId)
                dataList.removeAt(position)
                adapter.notifyDataSetChanged()
            }
            adb.show()
        }

        val dialog = dialogBuilder.create()
        dialog.show()

        button.setOnClickListener { dialog.dismiss() }
    }

    private fun getData(){
        var title = ""
        val dataList = ArrayList<HashMap<String, String>>()
        try {
            val res = myDb.getAllData()
            if (res.count == 0) run { title = "No data found" }
            else {
                while (res.moveToNext()){
                    val data = HashMap<String, String>()

                    data.put(id, res.getString(0))
                    data.put(date,"Palaverin päivämäärä: " + res.getString(1))
                    data.put(summary, res.getString(2))

                    dataList.add(data)
                    title = "Tallennettu data"
                }
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
        showData(title,dataList)
    }

    private fun deleteData(rowToDelete: String){
        val deleteRows = myDb.deleteData(rowToDelete)
    }
}