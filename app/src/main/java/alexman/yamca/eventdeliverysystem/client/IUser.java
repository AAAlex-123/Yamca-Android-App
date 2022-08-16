package alexman.yamca.eventdeliverysystem.client;

import java.io.IOException;
import java.util.Collection;
import java.util.NoSuchElementException;

import alexman.yamca.eventdeliverysystem.datastructures.AbstractTopic;
import alexman.yamca.eventdeliverysystem.datastructures.Post;

/**
 * Facade for the different components that make up a User. Only objects that implement this
 * interface are needed to interact with the client side of the event delivery system. The other
 * public classes of this package allow for more intricate interactions between the system and the
 * surrounding application.
 * <p>
 * Objects that implement this interface may be used with a {@code IUserHolder} instance to better
 * manage them.
 *
 * @author Alex Mandelias
 *
 * @see IUserHolder
 */
public interface IUser {

	/**
	 * Returns the name of this User's current Profile.
	 *
	 * @return the current Profile's name
	 */
	String getCurrentProfileName();

	/**
	 * Returns a Collection of this User's Topics.
	 *
	 * @return a Collection of this User's Topics
	 */
	Collection<AbstractTopic> getAllTopics();

	/**
	 * Returns the number of unread Posts for a Topic.
	 *
	 * @param topicName the name of the Topic
	 *
	 * @return the unread count for that Topic
	 */
	int getUnreadCount(String topicName);

	/**
	 * Sends a post to a specific topic on the server. This operation fires a user event with the
	 * {@code MESSAGE_SENT} tag when it's completed. Every user that is subscribed to this Topic
	 * receives a user event with the {@code MESSAGE_RECEIVED} tag.
	 *
	 * @param post the Post to post
	 * @param topicName the name of the Topic to which to post
	 */
	void post(Post post, String topicName);

	/**
	 * Creates a topic on the server. This operation fires a user event with the {@code
	 * TOPIC_CREATED} tag when it's completed.
	 *
	 * @param topicName the name of the Topic to create
	 */
	void createTopic(String topicName);

	/**
	 * Deletes a topic on the server. This operation fires a user event with the {@code
	 * SERVER_TOPIC_DELETED} tag when it's completed. Every user that is subscribed to this Topic
	 * receives a user event with the {@code TOPIC_DELETED} tag.
	 *
	 * @param topicName the name of the Topic to delete
	 */
	void deleteTopic(String topicName);

	/**
	 * Pulls all new Posts from a Topic, adds them to the Profile and saves them to the file system.
	 * Posts that have already been pulled are not pulled again.
	 *
	 * @param topicName the name of the Topic from which to pull
	 *
	 * @throws IOException if an I/O error occurs while interacting with the IProfileDAO object
	 * @throws NoSuchElementException if no Topic with the given name exists
	 */
	void pull(String topicName) throws IOException, NoSuchElementException;

	/**
	 * Registers this user to listen for posts on a Topic. THis operation fires a user event with
	 * the {@code TOPIC_LISTENED} tag.
	 *
	 * @param topicName the name of the Topic to listen for
	 */
	void listenForNewTopic(String topicName);

	/**
	 * Stops this user from listening for a Topic. This operation fires a user event with the {@code
	 * TOPIC_LISTEN_STOPPED} tag.
	 *
	 * @param topicName the name of the Topic to stop listening for
	 */
	void stopListeningForTopic(String topicName);

	/**
	 * Registers a listener to receive user events from this User.
	 *
	 * @param l the listener
	 */
	void addUserListener(UserListener l);
}
