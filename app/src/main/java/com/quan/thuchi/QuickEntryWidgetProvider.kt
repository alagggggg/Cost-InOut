package com.quan.thuchi

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class QuickEntryWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { updateWidget(context, manager, it) }
    }
    override fun onDeleted(context: Context, ids: IntArray) {
        val edit = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        ids.forEach { id -> (1..4).forEach { i -> edit.remove(key(id,"income",i)).remove(key(id,"expense",i)) } }
        edit.apply()
    }
    companion object {
        const val PREFS="quick_widget_prefs"
        const val EXTRA_KIND="quick_kind"
        const val EXTRA_CATEGORY="quick_category"
        private val incomeIds=intArrayOf(R.id.income_1,R.id.income_2,R.id.income_3,R.id.income_4)
        private val expenseIds=intArrayOf(R.id.expense_1,R.id.expense_2,R.id.expense_3,R.id.expense_4)
        private val incomeDefaults=arrayOf("Lương","Thưởng","Bán hàng","Thu khác")
        private val expenseDefaults=arrayOf("Ăn uống","Đi lại","Mua sắm","Chi khác")
        fun key(widgetId:Int,kind:String,index:Int)="widget_${widgetId}_${kind}_$index"
        fun updateWidget(context:Context,manager:AppWidgetManager,widgetId:Int){
            val prefs=context.getSharedPreferences(PREFS,Context.MODE_PRIVATE)
            val views=RemoteViews(context.packageName,R.layout.widget_quick_entry)
            for(i in 1..4){
                val income=prefs.getString(key(widgetId,"income",i),incomeDefaults[i-1])!!.ifBlank{incomeDefaults[i-1]}
                val expense=prefs.getString(key(widgetId,"expense",i),expenseDefaults[i-1])!!.ifBlank{expenseDefaults[i-1]}
                views.setTextViewText(incomeIds[i-1],"+ $income")
                views.setTextViewText(expenseIds[i-1],"− $expense")
                views.setOnClickPendingIntent(incomeIds[i-1],quickIntent(context,widgetId,i,"income",income))
                views.setOnClickPendingIntent(expenseIds[i-1],quickIntent(context,widgetId,i+4,"expense",expense))
            }
            val open=Intent(context,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            views.setOnClickPendingIntent(R.id.widget_open_app,PendingIntent.getActivity(context,widgetId*20,open,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            manager.updateAppWidget(widgetId,views)
        }
        private fun quickIntent(context:Context,widgetId:Int,slot:Int,kind:String,category:String):PendingIntent{
            val intent=Intent(context,MainActivity::class.java).apply{addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP);putExtra(EXTRA_KIND,kind);putExtra(EXTRA_CATEGORY,category)}
            return PendingIntent.getActivity(context,widgetId*20+slot,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }
}
