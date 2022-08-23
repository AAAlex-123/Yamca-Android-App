package alexman.yamca.app

import alexman.yamca.R
import alexman.yamca.eventdeliverysystem.client.UserAdapter
import alexman.yamca.eventdeliverysystem.client.UserEvent
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.lang.RuntimeException

class TopicListActivity : AppCompatActivity() {

    private val listener = object : UserAdapter() {
        override fun onTopicListenStopped(e: UserEvent) {
            runOnUiThread {
                adapter.removeTopic(e.topicName)
            }
        }

        override fun onTopicDeleted(e: UserEvent) {
            runOnUiThread {
                adapter.removeTopic(e.topicName)
            }
        }
    }

    private lateinit var adapter: TopicListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_list)

        adapter = TopicListAdapter(this)

        val usernameField: TextView = findViewById(R.id.topiclist_header_username_text)
        usernameField.text = UserSingleton.currentProfileName

        val addTopicButton: ImageButton = findViewById(R.id.topiclist_header_addTopic_button)
        val searchButton: ImageButton = findViewById(R.id.topiclist_header_search_button)
        val preferencesButton: ImageButton = findViewById(R.id.topiclist_header_preferences_button)

        addTopicButton.setOnClickListener {
            startActivity(Intent(this, CreateTopicActivity::class.java))
        }
        searchButton.setOnClickListener {
            // startActivity(Intent(this, SearchForTopicActivity::class.java))
        }
        preferencesButton.setOnClickListener {
            // startActivity(Intent(this, PreferencesActivity::class.java))
        }

        val recyclerView: RecyclerView = findViewById(R.id.topiclist_content)
        recyclerView.adapter = adapter
        UserSingleton.addUserListener(listener)
    }

    override fun onStart() {
        super.onStart()

        adapter.setTopics(UserSingleton.allTopics.map { topic -> topic.name })
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!UserSingleton.removeUserListener(listener))
            throw RuntimeException()
    }
}
