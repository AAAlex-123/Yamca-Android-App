package alexman.yamca.eventdeliverysystem.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alexman.yamca.eventdeliverysystem.client.User.UserStub;
import alexman.yamca.eventdeliverysystem.client.UserEvent.Tag;
import alexman.yamca.eventdeliverysystem.datastructures.Message.MessageType;
import alexman.yamca.eventdeliverysystem.datastructures.Packet;
import alexman.yamca.eventdeliverysystem.datastructures.Post;
import alexman.yamca.eventdeliverysystem.datastructures.PostInfo;
import alexman.yamca.eventdeliverysystem.server.Broker;
import alexman.yamca.eventdeliverysystem.thread.PushThread.Protocol;
import alexman.yamca.eventdeliverysystem.util.LG;

/**
 * A client-side process which is responsible for creating Topics and pushing Posts to them by
 * connecting to a remote server.
 *
 * @author Alex Mandelias
 * @author Dimitris Tsirbas
 * @see Broker
 */
final class Publisher extends ClientNode {

	/**
	 * Constructs a Publisher that must then be configured using one of the
	 * {@code ClientNode#configure} methods.
	 *
	 * @param userStub the UserStub object that will be notified if a push fails
	 */
	Publisher(UserStub userStub) {
		super(userStub);
	}

	/**
	 * Pushes a Post by creating a new Thread that connects to the actual Broker and starts a
	 * PushThread.
	 *
	 * @param post the Post
	 * @param topicName the name of the Topic to which to push the Post
	 */
	void push(Post post, String topicName) {
		LG.sout("Publisher#push(%s, %s)", post, topicName);
		Thread thread = new PushThread(post, topicName);
		thread.start();
	}

	/**
	 * Request that the remote server create a new Topic with the specified name by creating a new
	 * Thread that connects to the actual Broker for the Topic.
	 *
	 * @param topicName the name of the new Topic
	 */
	void createTopic(String topicName) {
		LG.sout("Publisher#createTopic(%s)", topicName);
		Thread thread = new CreateTopicThread(topicName);
		thread.start();
	}

	/**
	 * Request that the remote server delete the existing Topic with the specified name by creating
	 * a new Thread that connects to the actual Broker for the Topic.
	 *
	 * @param topicName the name of the new Topic
	 */
	void deleteTopic(String topicName) {
		LG.sout("Publisher#deleteTopic(%s)", topicName);
		Thread thread = new DeleteTopicThread(topicName);
		thread.start();
	}

	private final class PushThread extends ClientThread {

		private final Post post;

		/**
		 * Constructs a new PostThread that connects to the actual Broker and starts a PushThread to
		 * post the Post.
		 *
		 * @param post the Post
		 * @param topicName the name of the Topic to which to push the Post
		 */
		private PushThread(Post post, String topicName) {
			super(Tag.MESSAGE_SENT, MessageType.DATA_PACKET_SEND, topicName);
			this.post = post;
		}

		@Override
		protected void doWorkAndMaybeCloseSocket(boolean success, Socket socket,
				ObjectOutputStream oos, ObjectInputStream ois) throws IOException {
			try {
				if (!success) {
					throw new ServerException(ClientNode.getTopicDNEString(topicName));
				}

				final PostInfo postInfo = post.getPostInfo();
				final List<PostInfo> postInfoList = new LinkedList<>();
				postInfoList.add(postInfo);

				final Map<Long, Packet[]> packetMap = new HashMap<>();
				packetMap.put(postInfo.getId(), Packet.fromPost(post));

				final Thread pushThread =
						new alexman.yamca.eventdeliverysystem.thread.PushThread(oos, topicName, postInfoList,
								packetMap, Protocol.NORMAL,
								(callbackSuccess, callbackTopicName, callbackCause) -> {
									if (!callbackSuccess) {
										Exception e = new ServerException(
												ClientNode.CONNECTION_TO_SERVER_LOST_STRING,
												callbackCause);
										userStub.fireEvent(
												UserEvent.failed(eventTag, callbackTopicName, e));
									}
								});

				pushThread.run();
			} finally {
				socket.close();
			}
		}
	}

	private final class CreateTopicThread extends ClientThread {

		private CreateTopicThread(String topicName) {
			super(Tag.TOPIC_CREATED, MessageType.CREATE_TOPIC, topicName);
		}

		@Override
		protected void doWorkAndMaybeCloseSocket(boolean success, Socket socket,
				ObjectOutputStream oos, ObjectInputStream ois) throws IOException {
			try {
				if (!success) {
					throw new ServerException(ClientNode.getTopicAEString(topicName));
				}
			} finally {
				socket.close();
			}
		}
	}

	private final class DeleteTopicThread extends ClientThread {

		private DeleteTopicThread(String topicName) {
			super(Tag.SERVER_TOPIC_DELETED, MessageType.DELETE_TOPIC, topicName);
		}

		@Override
		protected void doWorkAndMaybeCloseSocket(boolean success, Socket socket,
				ObjectOutputStream oos, ObjectInputStream ois) throws IOException {
			try {
				if (!success) {
					throw new ServerException(ClientNode.getTopicDNEString(topicName));
				}
			} finally {
				socket.close();
			}
		}
	}
}
