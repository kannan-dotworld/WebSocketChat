package dev.dotworld.websocket

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import okhttp3.*


open class MainActivity : AppCompatActivity() {
    private var TAG: String = MainActivity::class.java.simpleName
    private var name: String? = null
    protected var webSocket: WebSocket? = null
//    protected var serverUrl: String = "ws://192.168.1.17:3000"
    protected var send: Button? = null

        private var serverUrl: String = "wss://echo.websocket.org"
   protected var alertDialog: AlertDialog.Builder? = null
   protected var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView1)
        send = findViewById(R.id.send1)
        send?.setOnClickListener { ShowSMS() }

        alertDialog = AlertDialog.Builder(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume: ")
       initWS()
        super.onResume()
    }



    private fun ShowSMS() {
        Log.d(TAG, "showAlart: ")
        alertDialog?.setTitle("SMS")
        var name = EditText(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        name.layoutParams = lp
        alertDialog?.setView(name)
        alertDialog?.setPositiveButton(
            "Send"
        ) { dialog, which ->
            var nameText = name.text.toString()
            if (nameText !== "") {
                webSocket?.send(nameText)
            }
        }
        alertDialog?.setCancelable(false)
        alertDialog?.show();
    }

    fun initWS() {
        Log.d(TAG, "initWS: ")
        val okHttpClient = OkHttpClient()
        var request = Request.Builder().url(serverUrl).build()
        var webSocketListener: WebSocketListener = object : WebSocketListener() {

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.d(TAG, "onMessage: $text")
                Toast.makeText(this@MainActivity, ""+text, Toast.LENGTH_SHORT).show()
                textView?.text=text
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.d(TAG, "onOpen: " + response.toString())
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Log.d(TAG, "onClosed: ")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.d(TAG, "onClosing: ")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.d(TAG, "onFailure: $t ;$response")
            }
        }

        webSocket = okHttpClient.newWebSocket(request, webSocketListener)

        webSocket?.send("1st")

    }



}