package alexman.yamca.app

import alexman.yamca.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class TopicListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_list)

        val u = UserSingleton
        Log.e("TopicListActivity", u.currentProfileName)
        Log.e("TopicListActivity", u.allTopics.toString())
    }
}
