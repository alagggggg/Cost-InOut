package com.quan.thuchi
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
class QuickEntryWidgetProvider:AppWidgetProvider(){
 override fun onUpdate(c:Context,m:AppWidgetManager,ids:IntArray){ids.forEach{updateWidget(c,m,it)}}
 override fun onDeleted(c:Context,ids:IntArray){val e=c.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit();ids.forEach{id->(1..4).forEach{i->e.remove(key(id,"income",i)).remove(key(id,"expense",i))}};e.apply()}
 companion object{
  const val PREFS="quick_widget_prefs";const val EXTRA_KIND="quick_kind";const val EXTRA_CATEGORY_ID="quick_category_id"
  private val defaultIncome=arrayOf("salary","bonus","business","refund_tax");private val defaultExpense=arrayOf("groceries","transport","family_shop","unexpected")
  private val incomeCells=intArrayOf(R.id.income_cell_1,R.id.income_cell_2,R.id.income_cell_3,R.id.income_cell_4);private val expenseCells=intArrayOf(R.id.expense_cell_1,R.id.expense_cell_2,R.id.expense_cell_3,R.id.expense_cell_4)
  private val incomeIcons=intArrayOf(R.id.income_icon_1,R.id.income_icon_2,R.id.income_icon_3,R.id.income_icon_4);private val expenseIcons=intArrayOf(R.id.expense_icon_1,R.id.expense_icon_2,R.id.expense_icon_3,R.id.expense_icon_4)
  private val incomeNames=intArrayOf(R.id.income_name_1,R.id.income_name_2,R.id.income_name_3,R.id.income_name_4);private val expenseNames=intArrayOf(R.id.expense_name_1,R.id.expense_name_2,R.id.expense_name_3,R.id.expense_name_4)
  fun key(id:Int,k:String,i:Int)="widget_${id}_${k}_$i"
  fun defaults(k:String)=if(k=="income")defaultIncome else defaultExpense
  fun updateWidget(c:Context,m:AppWidgetManager,id:Int){val p=c.getSharedPreferences(PREFS,Context.MODE_PRIVATE);val v=RemoteViews(c.packageName,R.layout.widget_quick_entry);for(i in 1..4){bind(c,v,id,i,"income",p.getString(key(id,"income",i),defaultIncome[i-1])?:defaultIncome[i-1],incomeCells[i-1],incomeIcons[i-1],incomeNames[i-1]);bind(c,v,id,i+4,"expense",p.getString(key(id,"expense",i),defaultExpense[i-1])?:defaultExpense[i-1],expenseCells[i-1],expenseIcons[i-1],expenseNames[i-1])};val open=Intent(c,MainActivity::class.java);v.setOnClickPendingIntent(R.id.widget_open_app,PendingIntent.getActivity(c,id*20,open,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE));m.updateAppWidget(id,v)}
  private fun bind(c:Context,v:RemoteViews,wid:Int,slot:Int,kind:String,catId:String,cell:Int,icon:Int,name:Int){val cat=WidgetCategoryCatalog.find(catId)?:WidgetCategoryCatalog.byKind(kind).first();v.setTextViewText(icon,cat.icon);v.setTextViewText(name,cat.name);val it=Intent(c,QuickEntryActivity::class.java).putExtra(EXTRA_KIND,kind).putExtra(EXTRA_CATEGORY_ID,cat.id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);v.setOnClickPendingIntent(cell,PendingIntent.getActivity(c,wid*20+slot,it,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))}
 }
}