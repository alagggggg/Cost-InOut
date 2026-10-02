package com.quan.thuchi
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
class WidgetConfigActivity:AppCompatActivity(){private var widgetId=AppWidgetManager.INVALID_APPWIDGET_ID
 override fun onCreate(b:Bundle?){super.onCreate(b);setResult(Activity.RESULT_CANCELED);setContentView(R.layout.activity_widget_config);widgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);if(widgetId==AppWidgetManager.INVALID_APPWIDGET_ID){finish();return};setup("income");setup("expense");findViewById<Button>(R.id.save_widget).setOnClickListener{save()}}
 private fun setup(kind:String){val list=WidgetCategoryCatalog.byKind(kind);val prefs=getSharedPreferences(QuickEntryWidgetProvider.PREFS,MODE_PRIVATE);for(i in 1..4){val sp=findViewById<Spinner>(resources.getIdentifier("${kind}_spinner_$i","id",packageName));sp.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,list);val current=prefs.getString(QuickEntryWidgetProvider.key(widgetId,kind,i),QuickEntryWidgetProvider.defaults(kind)[i-1]);sp.setSelection(list.indexOfFirst{it.id==current}.coerceAtLeast(0))}}
 private fun save(){val e=getSharedPreferences(QuickEntryWidgetProvider.PREFS,MODE_PRIVATE).edit();for(kind in listOf("income","expense"))for(i in 1..4){val sp=findViewById<Spinner>(resources.getIdentifier("${kind}_spinner_$i","id",packageName));e.putString(QuickEntryWidgetProvider.key(widgetId,kind,i),(sp.selectedItem as CategoryItem).id)};e.apply();QuickEntryWidgetProvider.updateWidget(this,AppWidgetManager.getInstance(this),widgetId);setResult(Activity.RESULT_OK,Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId));finish()}}
