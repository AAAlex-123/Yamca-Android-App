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

class PickUserActivity : AppCompatActivity() {

    private lateinit var usernameField: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_user)

        val profileDao: Optional<IProfileDAO> = getProfileDao()
        if (!profileDao.isPresent) {
            Toast.makeText(this, "Unexpected error", Toast.LENGTH_LONG).show()
            return
        }

        usernameField = findViewById(R.id.pickuser_username_input)

        val loginButton: Button = findViewById(R.id.pickuser_login_button)
        loginButton.setOnClickListener {
            toastOnException {
                UserSingleton.Holder.switchToExistingProfile(
                    usernameField.text.toString()
                )

                // if login does not throw
                toNextActivity()
            }
        }

        val createButton: Button = findViewById(R.id.pickuser_create_button)
        createButton.setOnClickListener {
            toastOnException {
                UserSingleton.Holder.switchToNewProfile(
                    usernameField.text.toString()
                )

                // if create does not throw
                toNextActivity()
            }
        }
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

    private fun toastOnException(fn: () -> Unit) {
        try {
            fn()
        } catch (e: Throwable) {
            Toast.makeText(
                this,
                e.message,
                Toast.LENGTH_LONG
            )
                .show()
            e.printStackTrace()
        }
    }

    private fun toNextActivity() = startActivity(
        Intent(this, TopicListActivity::class.java))
}
