package alexman.yamca.app

import alexman.yamca.R
import alexman.yamca.eventdeliverysystem.client.UserAdapter
import alexman.yamca.eventdeliverysystem.client.UserEvent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.lang.RuntimeException

class SearchForTopicActivity : AppCompatActivity() {

    private val listener = object : UserAdapter() {
        override fun onTopicListened(e: UserEvent) {
            val text: String =
                when (e.success) {
                    true -> "Joined Topic \"${e.topicName}\""
                    false -> "Could not join Topic \"${e.topicName}\""
                }

            this@SearchForTopicActivity.runOnUiThread {
                Toast.makeText(this@SearchForTopicActivity, text, Toast.LENGTH_LONG).show()
            }
        }
    }

    private lateinit var topicNameField: TextView
    private lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_for_topic)

        setSupportActionBar(findViewById(R.id.searchfortopic_header))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        topicNameField = findViewById(R.id.searchfortopic_topicname_input)
        searchButton = findViewById(R.id.searchfortopic_create_button)

        searchButton.setOnClickListener {
            UserSingleton.listenForNewTopic(topicNameField.text.toString())
        }

        UserSingleton.addUserListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!UserSingleton.removeUserListener(listener))
            throw RuntimeException()
    }
}
