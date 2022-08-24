package alexman.yamca.app

import alexman.yamca.R
import alexman.yamca.eventdeliverysystem.client.UserAdapter
import alexman.yamca.eventdeliverysystem.client.UserEvent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.RuntimeException

class CreateTopicActivity : AppCompatActivity() {

    private val listener = object : UserAdapter() {
        override fun onTopicCreated(e: UserEvent) {
            val text: String =
                when (e.success) {
                    true -> "Created Topic \"${e.topicName}\""
                    false -> "Could not create Topic \"${e.topicName}\""
                }

            this@CreateTopicActivity.runOnUiThread {
                Toast.makeText(this@CreateTopicActivity, text, Toast.LENGTH_LONG).show()
            }
        }
    }

    private lateinit var topicNameField: TextView
    private lateinit var createButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_topic)

        setSupportActionBar(findViewById(R.id.createtopic_header))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        topicNameField = findViewById(R.id.createtopic_topicname_input)
        createButton = findViewById(R.id.createtopic_create_button)

        createButton.setOnClickListener {
            UserSingleton.createTopic(topicNameField.text.toString())
        }

        UserSingleton.addUserListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!UserSingleton.removeUserListener(listener))
            throw RuntimeException()
    }
}
