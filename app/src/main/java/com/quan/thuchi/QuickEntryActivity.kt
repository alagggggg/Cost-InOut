package com.quan.thuchi
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale
class QuickEntryActivity:AppCompatActivity(){private lateinit var web:WebView;private var ready=false;private var factor=1000L;private lateinit var save:Button;private lateinit var amount:EditText;private lateinit var note:EditText;private lateinit var error:TextView;private lateinit var preview:TextView;private lateinit var cat:CategoryItem;private lateinit var kind:String;private lateinit var factors:List<Pair<Button,Long>>
 @SuppressLint("SetJavaScriptEnabled") override fun onCreate(b:Bundle?){super.onCreate(b);requestWindowFeature(Window.FEATURE_NO_TITLE);setContentView(R.layout.activity_quick_entry);window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);kind=intent.getStringExtra(QuickEntryWidgetProvider.EXTRA_KIND)?:"expense";cat=WidgetCategoryCatalog.find(intent.getStringExtra(QuickEntryWidgetProvider.EXTRA_CATEGORY_ID).orEmpty())?.takeIf{it.kind==kind}?:WidgetCategoryCatalog.byKind(kind).first();findViewById<TextView>(R.id.quick_icon).text=cat.icon;findViewById<TextView>(R.id.quick_category).text=cat.name;findViewById<TextView>(R.id.quick_kind).apply{text=if(kind=="income")"TIỀN VÀO" else "TIỀN RA";setTextColor(Color.parseColor(if(kind=="income")"#168746" else "#D53B4C"))};amount=findViewById(R.id.quick_amount);note=findViewById(R.id.quick_note);error=findViewById(R.id.quick_error);preview=findViewById(R.id.quick_preview);save=findViewById(R.id.quick_save);factors=listOf(findViewById<Button>(R.id.factor_one) to 1L,findViewById<Button>(R.id.factor_thousand) to 1000L,findViewById<Button>(R.id.factor_million) to 1000000L,findViewById<Button>(R.id.factor_billion) to 1000000000L);factors.forEach{(b,v)->b.setOnClickListener{selectFactor(v)}};selectFactor(1000L);amount.addTextChangedListener(object:TextWatcher{override fun beforeTextChanged(s:CharSequence?,a:Int,c:Int,d:Int){};override fun onTextChanged(s:CharSequence?,a:Int,b:Int,c:Int){updatePreview()};override fun afterTextChanged(e:Editable?){}});save.isEnabled=false;findViewById<Button>(R.id.quick_cancel).setOnClickListener{finish()};save.setOnClickListener{commit()};setupWeb();amount.requestFocus();amount.postDelayed({(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(amount,InputMethodManager.SHOW_IMPLICIT)},250)}
 private fun selectFactor(v:Long){factor=v;factors.forEach{it.first.isSelected=it.second==v;it.first.setTextColor(Color.parseColor(if(it.second==v)"#FFFFFF" else "#17171C"))};updatePreview()}
 private fun updatePreview(){val raw=amount.text.toString().replace(",","").trim().toDoubleOrNull()?:0.0;preview.text="= "+NumberFormat.getNumberInstance(Locale.US).format(raw*factor)}
 private fun setupWeb(){web=findViewById(R.id.quick_webview);val loader=WebViewAssetLoader.Builder().addPathHandler("/assets/",WebViewAssetLoader.AssetsPathHandler(this)).build();web.settings.javaScriptEnabled=true;web.settings.domStorageEnabled=true;web.settings.allowFileAccess=false;web.webViewClient=object:WebViewClient(){override fun shouldInterceptRequest(v:WebView,r:WebResourceRequest):WebResourceResponse?=loader.shouldInterceptRequest(r.url);override fun shouldInterceptRequest(v:WebView,u:String):WebResourceResponse?=loader.shouldInterceptRequest(Uri.parse(u));override fun onPageFinished(v:WebView,u:String){v.evaluateJavascript("window.nativeQuickDefaultScale ? window.nativeQuickDefaultScale() : 1000"){result->selectFactor(result.trim('"').toLongOrNull()?.takeIf{it in listOf(1L,1000L,1000000L,1000000000L)}?:1000L);ready=true;save.isEnabled=true}}};web.loadUrl("https://appassets.androidplatform.net/assets/index.html")}
 private fun commit(){val raw=amount.text.toString().replace(",","").trim().toDoubleOrNull();if(raw==null||raw<=0){error.text="Vui lòng nhập số tiền hợp lệ";amount.requestFocus();return};if(!ready){error.text="Đang chuẩn bị dữ liệu, vui lòng chờ";return};save.isEnabled=false;val js="window.nativeQuickSave("+JSONObject.quote(kind)+","+JSONObject.quote(cat.id)+","+raw+","+factor+","+JSONObject.quote(note.text.toString().trim())+")";web.evaluateJavascript(js){result->if(result=="true"){Toast.makeText(this,"Đã ghi ${cat.name}",Toast.LENGTH_SHORT).show();finish()}else{save.isEnabled=true;error.text="Không thể lưu giao dịch"}}}
 override fun onDestroy(){web.stopLoading();web.destroy();super.onDestroy()}}
