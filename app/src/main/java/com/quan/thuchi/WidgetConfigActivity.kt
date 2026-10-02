package com.quan.thuchi

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class WidgetConfigActivity : AppCompatActivity() {
    private var widgetId=AppWidgetManager.INVALID_APPWIDGET_ID
    private val incomeDefaults=arrayOf("Lương","Thưởng","Bán hàng","Thu khác")
    private val expenseDefaults=arrayOf("Ăn uống","Đi lại","Mua sắm","Chi khác")
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState);setResult(Activity.RESULT_CANCELED);setContentView(R.layout.activity_widget_config)
        widgetId=intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID)?:AppWidgetManager.INVALID_APPWIDGET_ID
        if(widgetId==AppWidgetManager.INVALID_APPWIDGET_ID){finish();return}
        val prefs=getSharedPreferences(QuickEntryWidgetProvider.PREFS,MODE_PRIVATE)
        for(i in 1..4){
            findViewById<EditText>(incomeId(i)).setText(prefs.getString(QuickEntryWidgetProvider.key(widgetId,"income",i),incomeDefaults[i-1]))
            findViewById<EditText>(expenseId(i)).setText(prefs.getString(QuickEntryWidgetProvider.key(widgetId,"expense",i),expenseDefaults[i-1]))
        }
        findViewById<Button>(R.id.save_widget).setOnClickListener{save()}
    }
    private fun save(){
        val edit=getSharedPreferences(QuickEntryWidgetProvider.PREFS,MODE_PRIVATE).edit()
        for(i in 1..4){
            edit.putString(QuickEntryWidgetProvider.key(widgetId,"income",i),findViewById<EditText>(incomeId(i)).text.toString().trim().ifBlank{incomeDefaults[i-1]})
            edit.putString(QuickEntryWidgetProvider.key(widgetId,"expense",i),findViewById<EditText>(expenseId(i)).text.toString().trim().ifBlank{expenseDefaults[i-1]})
        }
        edit.apply();QuickEntryWidgetProvider.updateWidget(this,AppWidgetManager.getInstance(this),widgetId)
        setResult(Activity.RESULT_OK,Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId));finish()
    }
    private fun incomeId(i:Int)=resources.getIdentifier("income_${i}_name","id",packageName)
    private fun expenseId(i:Int)=resources.getIdentifier("expense_${i}_name","id",packageName)
}
