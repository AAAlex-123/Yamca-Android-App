package alexman.yamca.app

import alexman.yamca.R
import alexman.yamca.eventdeliverysystem.client.UserAdapter
import alexman.yamca.eventdeliverysystem.client.UserEvent
import android.content.Context
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

    init {
        UserSingleton.addUserListener(object : UserAdapter() {
            override fun onTopicListened(e: UserEvent) {
                activity.runOnUiThread { list.add(e.topicName) }
            }

            override fun onTopicListenStopped(e: UserEvent) {
                activity.runOnUiThread { list.remove(e.topicName) }
            }

            override fun onTopicDeleted(e: UserEvent) {
                activity.runOnUiThread { list.remove(e.topicName) }
            }
        })
    }

    val list = SortedList(
        String::class.java,
        object : SortedListAdapterCallback<String>(this) {
            override fun compare(o1: String, o2: String): Int =
                o1.compareTo(o2)

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean = oldItem == newItem

            override fun areItemsTheSame(item1: String, item2: String): Boolean =
                item1 == item2
        })

    class TopicPreviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val topicNameTextView: TextView =
            view.findViewById(R.id.topicpreview_topicname_text)
        private val unreadTextView: TextView =
            view.findViewById(R.id.topicpreview_unread_count_text)
        private val unreadLayout: LinearLayout = view.findViewById(R.id.topicpreview_unread_layout)

        fun bindTo(topicName: String) {
            topicNameTextView.text = topicName

            val unreadCount = UserSingleton.getUnreadCount(topicName)
            unreadTextView.text = unreadCount.toString()
            // holder.unreadLayout.visibility = if (unreadCount == 0) View.INVISIBLE else View.VISIBLE
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
