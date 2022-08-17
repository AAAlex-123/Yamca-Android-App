package alexman.yamca.app

import alexman.yamca.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SingleTopicActivity : AppCompatActivity() {

    companion object {
        const val extras_topicName: String = "topicName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_topic)

        val dummy: TextView = findViewById(R.id.topicName)
        dummy.text = intent.getStringExtra(extras_topicName)
    }
}
