package alexman.yamca.app

import alexman.yamca.R
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Pattern

class ConnectActivity : AppCompatActivity() {

    private companion object {
        // https://stackoverflow.com/questions/5284147/validating-ipv4-addresses-with-regexp
        val ipPatter: Pattern = Pattern.compile(
            "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)(\\.(?!\$)|\$)){4}\$"
        )

        // https://3widgets.com/
        val portPattern: Pattern = Pattern.compile(
            "^[1-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5]$"
        )
    }

    private lateinit var ipField: TextView
    private lateinit var portField: TextView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        ipField = findViewById(R.id.connect_ip_input)
        portField = findViewById(R.id.connect_port_input)
        button = findViewById(R.id.connect_submit_button)

        button.setOnClickListener {
            if (checkIpAndPort()) {
                val intent = Intent(this, PickUserActivity::class.java)
                intent.putExtra(PickUserActivity.ip, ipField.text.toString())
                intent.putExtra(PickUserActivity.port, portField.text.toString())

                startActivity(intent)
            }
        }
    }

    private fun checkIpAndPort(): Boolean {

        val correctIp = ipPatter.matcher(ipField.text).matches()
        val correctPort = portPattern.matcher(portField.text).matches()

        if (!correctIp) {
            ipField.error = "Invalid IP"
        }

        if (!correctPort) {
            portField.error = "Invalid Port"
        }

        return correctIp && correctPort
    }
}
