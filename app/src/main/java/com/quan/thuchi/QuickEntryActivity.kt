package com.quan.thuchi
import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import org.json.JSONObject
class QuickEntryActivity:AppCompatActivity(){private lateinit var web:WebView;private var ready=false;private lateinit var save:Button;private lateinit var amount:EditText;private lateinit var note:EditText;private lateinit var error:TextView;private lateinit var cat:CategoryItem;private lateinit var kind:String
 @SuppressLint("SetJavaScriptEnabled") override fun onCreate(b:Bundle?){super.onCreate(b);requestWindowFeature(Window.FEATURE_NO_TITLE);setContentView(R.layout.activity_quick_entry);window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);kind=intent.getStringExtra(QuickEntryWidgetProvider.EXTRA_KIND)?:"expense";cat=WidgetCategoryCatalog.find(intent.getStringExtra(QuickEntryWidgetProvider.EXTRA_CATEGORY_ID).orEmpty())?:WidgetCategoryCatalog.byKind(kind).first();findViewById<TextView>(R.id.quick_icon).text=cat.icon;findViewById<TextView>(R.id.quick_category).text=cat.name;findViewById<TextView>(R.id.quick_kind).apply{text=if(kind=="income")"TIỀN VÀO" else "TIỀN RA";setTextColor(Color.parseColor(if(kind=="income")"#168746" else "#D53B4C"))};amount=findViewById(R.id.quick_amount);note=findViewById(R.id.quick_note);error=findViewById(R.id.quick_error);save=findViewById(R.id.quick_save);save.isEnabled=false;findViewById<Button>(R.id.quick_cancel).setOnClickListener{finish()};save.setOnClickListener{commit()};setupWeb();amount.requestFocus();amount.postDelayed({(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(amount,InputMethodManager.SHOW_IMPLICIT)},250)}
 private fun setupWeb(){web=findViewById(R.id.quick_webview);val loader=WebViewAssetLoader.Builder().addPathHandler("/assets/",WebViewAssetLoader.AssetsPathHandler(this)).build();web.settings.javaScriptEnabled=true;web.settings.domStorageEnabled=true;web.settings.allowFileAccess=false;web.webViewClient=object:WebViewClient(){override fun shouldInterceptRequest(v:WebView,r:WebResourceRequest):WebResourceResponse?=loader.shouldInterceptRequest(r.url);override fun shouldInterceptRequest(v:WebView,u:String):WebResourceResponse?=loader.shouldInterceptRequest(Uri.parse(u));override fun onPageFinished(v:WebView,u:String){ready=true;save.isEnabled=true}};web.loadUrl("https://appassets.androidplatform.net/assets/index.html")}
 private fun commit(){val raw=amount.text.toString().replace(",","").trim().toDoubleOrNull();if(raw==null||raw<=0){error.text="Vui lòng nhập số tiền hợp lệ";amount.requestFocus();return};if(!ready){error.text="Đang chuẩn bị dữ liệu, vui lòng chờ";return};save.isEnabled=false;val js="window.nativeQuickSave("+JSONObject.quote(kind)+","+JSONObject.quote(cat.id)+","+raw+","+JSONObject.quote(note.text.toString().trim())+")";web.evaluateJavascript(js){result->if(result=="true"){Toast.makeText(this,"Đã ghi ${cat.name}",Toast.LENGTH_SHORT).show();finish()}else{save.isEnabled=true;error.text="Không thể lưu giao dịch"}}}
 override fun onDestroy(){web.stopLoading();web.destroy();super.onDestroy()}}
