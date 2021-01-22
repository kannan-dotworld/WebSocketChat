package dev.dotworld.websocketchat

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import okhttp3.*


open class MainActivity : AppCompatActivity() {
    private var TAG: String = MainActivity::class.java.simpleName
    private var name: String? = null
    protected var webSocket: WebSocket? = null
    protected var serverUrl: String = "ws://192.168.1.17:3000"
    protected var send: Button? = null

    //    private var serverUrl: String = "wss://echo.websocket.org"
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
  fun  intUi(){
      textView = findViewById(R.id.textView1)
      send = findViewById(R.id.send1)
    }
    override fun onResume() {
        Log.d(TAG, "onResume: ")
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
        } else {
            Log.d(TAG, "onResume:  alart")
            showAlart()


        }
        super.onResume()
    }

    fun showtext(text: String) {
        textView?.text = text
        textView?.invalidate()
        Log.d(TAG, "showtext: $text")
        Handler(Looper.getMainLooper()).post(Runnable {
            Log.d(TAG, "showtext: handler")
            textView?.text = text
            textView?.invalidate()
        })
    }

    private fun showAlart() {
        Log.d(TAG, "showAlart: ")
        alertDialog?.setTitle("NAME")
        alertDialog?.setMessage("Enter your name")
        var name = EditText(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        name.layoutParams = lp
        alertDialog?.setView(name)
        alertDialog?.setPositiveButton(
            "continue"
        ) { dialog, which ->
            var nameText = name.text.toString()
//            startTOchat(nameText)
            initWS()

        }
        alertDialog?.setCancelable(false)
        alertDialog?.show();
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
        webSocket = okHttpClient.newWebSocket(request, SocketListener())
        webSocket?.send("1st")

    }

    class SocketListener() : WebSocketListener() {
        var TAG: String = "SocketListener"
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.d(TAG, "onOpen: " + response.toString())
        }

        override fun onMessage(webSocket: WebSocket, t: String) {
            super.onMessage(webSocket, t)
            Log.d(TAG, "onMessage: $t")
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.d(
                    "UI thread",
                    "I am the UI thread"
                )

                MainActivity().showtext(t)
            })


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
            Log.d(TAG, "onFailure: text: $t, reponce ${response}")
        }
    }

}