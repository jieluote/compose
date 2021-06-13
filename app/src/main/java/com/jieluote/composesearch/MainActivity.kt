package com.jieluote.composesearch

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.jieluote.composesearch.ui.theme.ComposeSearchTheme
import android.os.Build
import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import android.webkit.WebSettings.LayoutAlgorithm
import android.webkit.WebSettings
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeSearchTheme {
                Surface(color = MaterialTheme.colors.background) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Search()
                    }
                }
            }
        }
    }
}

private var webView: WebView? = null
private val TAG = MainActivity::class.java.name

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun Search() {
    var inputText = remember { mutableStateOf("在compose上搜索") }
    val context = LocalContext.current.applicationContext

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(60.dp))
        Image(
            painter = painterResource(R.drawable.compose_search_logo),
            contentDescription = null,
            modifier = Modifier
                .width(160.dp),
            contentScale = ContentScale.Crop
        )
        OutlinedTextField(
            value = inputText.value,
            onValueChange = {
                inputText.value = it
                Log.d(TAG, "onValueChange:" + inputText.value)
            },
            Modifier
                .width(300.dp)
                .height(60.dp)
                .background(Color.White),
            placeholder = { Text("请输入网址或者关键词") },
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.text_field_search_bg), null,
                    modifier = Modifier
                        .clickable(onClick = {
                            var value = inputText.value
                            if (!value.contains("http") && !value.contains("www.")) {
                                value = "https://www.baidu.com/s?wd=" + value
                            }
                            Log.d(TAG, "load value:" + value + ",webView:" + webView)
                            webView?.loadUrl(value)
                        })
                        .height(40.dp)
                )
            },
            trailingIcon = {
                Icon(
                    painterResource(R.drawable.text_field_voice_bg), null,
                    modifier = Modifier
                        .clickable(onClick = {
                            Toast
                                .makeText(context, "对不起,还没有这个功能", Toast.LENGTH_SHORT)
                                .show()
                        })
                        .height(40.dp)
                )
            }
        )
        AndroidView(
            { context ->
                webView = WebView(context)
                val webSetting: WebSettings = webView!!.getSettings()
                webSetting.javaScriptEnabled = true
                webSetting.javaScriptCanOpenWindowsAutomatically = true
                webSetting.allowFileAccess = true
                webSetting.layoutAlgorithm = LayoutAlgorithm.NARROW_COLUMNS
                webSetting.setSupportZoom(true)
                webSetting.builtInZoomControls = true
                webSetting.useWideViewPort = true
                webSetting.setSupportMultipleWindows(true)
                val client: WebViewClient = object : WebViewClient() {
                    /**
                     * 防止加载网页时调起系统浏览器
                     */
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        Log.d(TAG, "UrlLoading:" + url)
                        view.loadUrl(url)
                        return true
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        Log.d(TAG, "onPageStarted:" + url)
                    }

                    override fun onPageFinished(webView: WebView, s: String) {
                        super.onPageFinished(webView, s)
                        Log.d(TAG, "onPageFinished:" + s + ",process:" + webView.progress)
                    }

                }
                webView!!.webViewClient = client
                return@AndroidView webView!!
            },
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White)
        )
    }
}
