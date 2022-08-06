package alexman.yamca.eventdeliverysystem.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import alexman.yamca.eventdeliverysystem.datastructures.AbstractTopic;
import alexman.yamca.eventdeliverysystem.datastructures.Post;

/**
 * A data structure holding information about a Profile and their subscribed Topics.
 *
 * @author Alex Mandelias
 * @author Dimitris Tsirmpas
 */
final class Profile {

	private final String name;
	private final Map<String, UserTopic> topics = new HashMap<>();
	private final Map<String, Integer> unreadTopics = new HashMap<>();

	/**
	 * Creates a new, empty, Profile with the specified name.
	 *
	 * @param name the unique name of the Profile
	 */
	Profile(String name) {
		this.name = name;
	}

	/**
	 * Returns this Profile's name.
	 *
	 * @return the name
	 */
	String getName() {
		return name;
	}

	/**
	 * Returns this Profile's User Topics.
	 *
	 * @return the User Topics
	 */
	Set<UserTopic> getTopics() {
		return new HashSet<>(topics.values());
	}

	/**
	 * Adds a new, unique, User Topic to this Profile.
	 *
	 * @param topicName the name of the new User Topic
	 *
	 * @throws IllegalArgumentException if a User Topic with the same name already exists
	 */
	void addTopic(String topicName) {
		addTopic(new UserTopic(topicName));
	}

	/**
	 * Adds a new Abstract Topic to this Profile.
	 *
	 * @param abstractTopic the Topic
	 *
	 * @throws NullPointerException if {@code abstractTopic == null}
	 * @throws IllegalArgumentException if a User Topic with the same name already exists
	 */
	void addTopic(AbstractTopic abstractTopic) {
		if (abstractTopic == null) {
			throw new NullPointerException("Topic can't be null");
		}

		addTopic(new UserTopic(abstractTopic));
	}

	/**
	 * Adds a new User Topic to this Profile.
	 *
	 * @param userTopic the Topic
	 *
	 * @throws NullPointerException if {@code userTopic == null}
	 * @throws IllegalArgumentException if a User Topic with the same name already exists
	 */
	private void addTopic(UserTopic userTopic) {
		if (userTopic == null) {
			throw new NullPointerException("Topic can't be null");
		}

		final String topicName = userTopic.getName();

		assertTopicDoesNotExist(topicName);

		topics.put(topicName, userTopic);
		unreadTopics.put(topicName, 0);
	}

	/**
	 * Updates a User Topic of this Profile with new Posts.
	 *
	 * @param topicName the name of the User Topic to update
	 * @param posts the new Posts to post to the Topic
	 *
	 * @throws NoSuchElementException if no User Topic with the given name exists
	 */
	void updateTopic(String topicName, List<Post> posts) {
		assertTopicExists(topicName);

		topics.get(topicName).postAll(posts);
	}

	/**
	 * Removes a User Topic from this Profile.
	 *
	 * @param topicName the name of the User Topic
	 *
	 * @throws NoSuchElementException if no User Topic with the given name exists
	 */
	void removeTopic(String topicName) {
		assertTopicExists(topicName);

		topics.remove(topicName);
		unreadTopics.remove(topicName);
	}

	/**
	 * Marks a User Topic as unread.
	 *
	 * @param topicName the name of the User Topic
	 *
	 * @throws NoSuchElementException if no User Topic with the given name exists
	 */
	void markUnread(String topicName) {
		assertTopicExists(topicName);

		unreadTopics.put(topicName, unreadTopics.get(topicName) + 1);
	}

	/**
	 * Returns the unread count for a Topic.
	 *
	 * @param topicName the name of the User Topic
	 *
	 * @return the number of unread Posts in that Topic
	 *
	 * @throws NoSuchElementException if no User Topic with the given name exists
	 */
	int getUnread(String topicName) {
		assertTopicExists(topicName);

		return unreadTopics.get(topicName);
	}

	/**
	 * Marks all posts in a User Topic as read.
	 *
	 * @param topicName the name of the User Topic
	 *
	 * @throws NoSuchElementException if no User Topic with the given name exists
	 */
	void clearUnread(String topicName) {
		assertTopicExists(topicName);

		unreadTopics.put(topicName, 0);
	}

	@Override
	public String toString() {
		final List<Object> topicNames = Arrays.asList(topics.keySet().toArray());
		return String.format("Profile [name=%s, topics=%s]", name, topicNames);
	}

	private void assertTopicExists(String topicName) throws NoSuchElementException {
		if (!topics.containsKey(topicName)) {
			throw new NoSuchElementException("No Topic with name " + topicName + " found");
		}
	}

	private void assertTopicDoesNotExist(String topicName) throws IllegalArgumentException {
		if (topics.containsKey(topicName)) {
			throw new NoSuchElementException("Topic with name " + topicName + " already exists");
		}
	}
}
