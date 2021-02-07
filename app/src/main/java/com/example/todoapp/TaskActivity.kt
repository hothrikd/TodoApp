package com.example.todoapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

const val DB_NAME = "todo.db"
class TaskActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var myCalender : Calendar
    lateinit var dateSetListner:DatePickerDialog.OnDateSetListener
    lateinit var timeSetListner:TimePickerDialog.OnTimeSetListener
    private val labels  = arrayListOf("Personal","Buisness","Insurance","Banking","Shopping")

    var finalDate = 0L
    var finalTime = 0L

    val db by lazy {
        AppDatabase.getDatabase(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        dateEdt.setOnClickListener(this)
        timeEdt.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
        setUpSpinner()
    }

    private fun setUpSpinner() {
        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,labels)
        labels.sort()
        spinnerCategory.adapter = adapter
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.dateEdt->{
                setListner()
            }
            R.id.timeEdt->{
                setTimeListner()
            }
            R.id.saveBtn->{
                saveTodo()
            }
        }
    }

    private fun saveTodo() {
        val category = spinnerCategory.selectedItem.toString()
        val title = titleInpLay.editText?.text.toString()
        val description = taskInpLay.editText?.text.toString()

        GlobalScope.launch(Dispatchers.IO) {
            val id = withContext(Dispatchers.IO){
                return@withContext db.tododao().insertTask(
                    TodoModel(
                        title,
                        description,
                        category,
                        finalDate,
                        finalTime
                    )
                )
            }
            finish()
        }
    }

    private fun setTimeListner() {
        myCalender = Calendar.getInstance()
        timeSetListner = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            myCalender.set(Calendar.HOUR,hourOfDay)
            myCalender.set(Calendar.MINUTE,minute)
            updateTime()
        }
        val timePickerDialog = TimePickerDialog(this,timeSetListner,myCalender.get(Calendar.HOUR),myCalender.get(Calendar.MINUTE),false)
        timePickerDialog.show()
    }

    private fun updateTime() {
        val myformat = "hh:mm a"
        val sdf = SimpleDateFormat(myformat)
        finalTime = myCalender.time.time
        timeEdt.setText(sdf.format(myCalender.time))
    }

    private fun setListner() {
        myCalender = Calendar.getInstance()
        dateSetListner = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDate()
        }
        val datePickerDialog = DatePickerDialog(this,dateSetListner,myCalender.get(Calendar.YEAR),myCalender.get(Calendar.MONTH),myCalender.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        val myformat = "EEE, d MMM yyyy"
        val sdf = SimpleDateFormat(myformat)
        finalDate = myCalender.time.time
        dateEdt.setText(sdf.format(myCalender.time))
        timeInptLay.visibility = View.VISIBLE
    }
}