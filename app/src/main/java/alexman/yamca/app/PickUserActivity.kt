package alexman.yamca.app

import alexman.yamca.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class PickUserActivity : AppCompatActivity() {

    companion object {
        const val ip = "IP"
        const val port = "PORT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_user)

        // TODO remove placeholder code
        val ip1: TextView = findViewById(R.id.ip)
        val port1: TextView = findViewById(R.id.port)

        ip1.text = intent.getStringExtra(ip)
        port1.text = intent.getStringExtra(port)
    }
}
