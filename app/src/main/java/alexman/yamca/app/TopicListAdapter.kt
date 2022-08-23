package alexman.yamca.app

import alexman.yamca.R
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback

class TopicListAdapter(private val activity: TopicListActivity) :
    RecyclerView.Adapter<TopicListAdapter.TopicPreviewViewHolder>() {

    private val list = SortedList(
        String::class.java,
        object : SortedListAdapterCallback<String>(this) {
            override fun compare(o1: String, o2: String): Int = o1.compareTo(o2)

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean = oldItem == newItem

            override fun areItemsTheSame(item1: String, item2: String): Boolean =
                item1 == item2
        })

    fun setTopics(topics: Collection<String>) = list.replaceAll(topics)

    fun removeTopic(topicName: String) {
        list.remove(topicName)
    }

    inner class TopicPreviewViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val topicNameTextView: TextView =
            view.findViewById(R.id.topicpreview_topicname_text)
        private val unreadTextView: TextView =
            view.findViewById(R.id.topicpreview_unread_count_text)
        private val unreadLayout: LinearLayout = view.findViewById(R.id.topicpreview_unread_layout)

        fun bindTo(topicName: String) {
            topicNameTextView.text = topicName

            val unreadCount = UserSingleton.getUnreadCount(topicName)
            unreadTextView.text = unreadCount.toString()
            unreadLayout.visibility = if (unreadCount == 0) View.INVISIBLE else View.VISIBLE

            view.setOnClickListener {
                activity.startActivity(
                    Intent(activity, SingleTopicActivity::class.java).also {
                        it.putExtra(SingleTopicActivity.extras_topicName, topicName)
                    }
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicPreviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.topic_preview, parent, false)
        return TopicPreviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicPreviewViewHolder, position: Int) {
        holder.bindTo(list[position])
    }

    override fun getItemCount(): Int = list.size()
}
