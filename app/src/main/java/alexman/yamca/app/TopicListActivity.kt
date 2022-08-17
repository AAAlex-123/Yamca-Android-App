package alexman.yamca.app

import alexman.yamca.R
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class TopicListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_list)

        val usernameField: TextView = findViewById(R.id.topiclist_header_username_text)
        usernameField.text = UserSingleton.currentProfileName

        val addTopicButton: ImageButton = findViewById(R.id.topiclist_header_addTopic_button)
        val searchButton: ImageButton = findViewById(R.id.topiclist_header_search_button)
        val preferencesButton: ImageButton = findViewById(R.id.topiclist_header_preferences_button)

        addTopicButton.setOnClickListener {
            // startActivity(Intent(this, AddTopicActivity::class.java))
        }
        searchButton.setOnClickListener {
            // startActivity(Intent(this, SearchForTopicActivity::class.java))
        }
        preferencesButton.setOnClickListener {
            // startActivity(Intent(this, PreferencesActivity::class.java))
        }

        val recyclerView: RecyclerView = findViewById(R.id.topiclist_content)
        recyclerView.adapter = TopicListAdapter(this)
    }
}
