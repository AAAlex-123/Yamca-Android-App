package alexman.yamca.app

import alexman.yamca.R
import alexman.yamca.eventdeliverysystem.dao.IProfileDAO
import alexman.yamca.eventdeliverysystem.filesystem.ProfileFileSystem
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.nio.file.Files
import java.util.*
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

        init {
            UserSingleton.init()
        }
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

                val ip: String = ipField.text.toString()
                val port: Int = Integer.parseInt(portField.text.toString())
                val profileDao: Optional<IProfileDAO> = getProfileDao()
                if (!profileDao.isPresent) {
                    Toast.makeText(this, "Unexpected error", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                UserSingleton.Holder.configure(ip, port, profileDao.get())

                startActivity(Intent(this, PickUserActivity::class.java))
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

    private fun getProfileDao(): Optional<IProfileDAO> {

        val root = filesDir.resolve("users").toPath()

        return try {
            if (!Files.exists(root)) {
                Files.createDirectory(root)
            }

            Optional.of(ProfileFileSystem(root))
        } catch (e: FileSystemException) {
            e.printStackTrace()
            Optional.empty()
        }
    }
}
