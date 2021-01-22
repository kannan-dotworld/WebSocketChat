package dev.dotworld.websocketchat

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import okhttp3.*


class WschatFragment : Fragment() {
    private var name: String? = null
    private var webSocket: WebSocket? = null
    private var serverUrl: String = "ws://192.168.1.17:3000"

    //    private var serverUrl: String = "wss://echo.websocket.org"
    var alertDialog: AlertDialog.Builder? = null
    var textView: TextView? = null
    var contextView: Context? = null

    var TAG: String = "WschatFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_wschat, container, false)
        view.findViewById<Button>(R.id.send).setOnClickListener { showAlart() }
        alertDialog = AlertDialog.Builder(view.context)
        textView = view.findViewById(R.id.textView)
        contextView=view.context
        initWS()
        return view
    }

    private fun showAlart() {
        Log.d(TAG, "showAlart: ")
        alertDialog?.setTitle("SMS")
        var name = EditText(context)
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

     fun showtext(text: String) {
        Log.d(TAG, "showtext: $text")

    }

    fun initWS() {
        Log.d(TAG, "initWS: ")
        val okHttpClient = OkHttpClient()
        var request = Request.Builder().url(serverUrl).build()
        webSocket = okHttpClient.newWebSocket(request, SocketListener())
        webSocket?.send("1st")

    }
    class SocketListener : WebSocketListener() {
        var TAG: String = "SocketListener"
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.d(TAG, "onOpen: " + response.toString())
        }

        override fun onMessage(webSocket: WebSocket, t: String) {
            super.onMessage(webSocket, t)
            Log.d(TAG, "onMessage: $t")
            val w = WschatFragment()
            w.showtext(t)
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

