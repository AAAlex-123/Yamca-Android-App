package alexman.yamca.eventdeliverysystem.client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import alexman.yamca.eventdeliverysystem.client.UserEvent.Tag;
import alexman.yamca.eventdeliverysystem.dao.IProfileDAO;
import alexman.yamca.eventdeliverysystem.datastructures.AbstractTopic;
import alexman.yamca.eventdeliverysystem.datastructures.Post;
import alexman.yamca.eventdeliverysystem.util.LG;

/**
 * A User object acts both as an {@code IUserHolder}, an object which holds an {@code IUser}
 * instance, and a {@code IUser}, an object which is the facade itself. References to a User object
 * can be cast to either interface to achieve the desired encapsulation.
 *
 * @author Alex Mandelias
 * @author Dimitris Tsirmpas
 *
 * @see IUserHolder
 * @see IUser
 */
public final class User implements IUser, IUserHolder {

	private final CompositeListener listener = new CompositeListener();
	private final UserStub userStub = new UserStub();

	private IProfileDAO profileDao = null;
	private Profile currentProfile = null;

	private final Publisher publisher = new Publisher(userStub);
	private final Consumer consumer = new Consumer(userStub);

	/**
	 * Creates a new empty User object and returns it. The User must first be configured using the
	 * {@code configure} method and then have a Profile assigned to it using one of the appropriate
	 * {@code switchToProfile} methods.
	 *
	 * @return the new empty User
	 *
	 * @see IUserHolder#configure(String, int, IProfileDAO)
	 * @see IUserHolder#switchToNewProfile(String)
	 * @see IUserHolder#switchToExistingProfile(String)
	 */
	public static User empty() {
		return new User();
	}

	private User() {
		addUserListener(new BasicListener());
	}

	@Override
	public IUser get() {
		return this;
	}

	@Override
	public void configure(InetAddress ip, int port, IProfileDAO profileDao) {
		publisher.configure(ip, port);
		consumer.configure(ip, port);
		this.profileDao = profileDao;
	}

	@Override
	public void switchToNewProfile(String profileName) throws IOException {
		consumer.close();
		currentProfile = new Profile(profileName);
		profileDao.createNewProfile(profileName);
		consumer.setTopics(new HashSet<>(currentProfile.getTopics()));
	}

	@Override
	public void switchToExistingProfile(String profileName) throws IOException {
		consumer.close();
		currentProfile = new Profile(profileName);
		profileDao.loadProfile(profileName).forEach(currentProfile::addTopic);
		consumer.setTopics(new HashSet<>(currentProfile.getTopics()));
	}

	@Override
	public String getCurrentProfileName() {
		return currentProfile.getName();
	}

	@Override
	public Collection<AbstractTopic> getAllTopics() {
		return new HashSet<>(currentProfile.getTopics());
	}

	@Override
	public int getUnreadCount(String topicName) {
		return currentProfile.getUnread(topicName);
	}

	@Override
	public void post(Post post, String topicName) {
		LG.sout("User#post(%s, %s)", post, topicName);
		LG.in();

		if (userIsNotSubscribed(topicName)) {
			userStub.fireEvent(UserEvent.failed(Tag.MESSAGE_SENT, topicName,
					new NoSuchElementException("This User can't post to Topic " + topicName
					                           + " because they aren't subscribed to it")));
		} else {
			publisher.push(post, topicName);
		}

		LG.out();
	}

	@Override
	public void createTopic(String topicName) {
		LG.sout("User#createTopic(%s)", topicName);
		LG.in();

		publisher.createTopic(topicName);

		LG.out();
	}

	@Override
	public void deleteTopic(String topicName) {
		LG.sout("User#deleteTopic(%s)", topicName);
		LG.in();

		if (userIsNotSubscribed(topicName)) {
			userStub.fireEvent(UserEvent.failed(Tag.TOPIC_DELETED, topicName,
					new NoSuchElementException("This User can't delete Topic " + topicName
					                           + " because they aren't subscribed to it")));
		} else {
			publisher.deleteTopic(topicName);
		}

		LG.out();
	}

	@Override
	public void pull(String topicName) throws IOException, NoSuchElementException {
		LG.sout("User#pull from Topic '%s'", topicName);
		LG.in();
		final List<Post> newPosts = consumer.pull(topicName); // sorted from earliest to latest
		LG.sout("newPosts=%s", newPosts);
		currentProfile.updateTopic(topicName, newPosts);
		currentProfile.clearUnread(topicName);

		for (final Post post : newPosts) {
			LG.sout("Saving Post '%s'", post);
			profileDao.savePostForCurrentProfile(post, topicName);
		}

		LG.out();
	}

	@Override
	public void listenForNewTopic(String topicName) {
		LG.sout("User#listenForNewTopic(%s)", topicName);
		LG.in();

		consumer.listenForNewTopic(topicName);

		LG.out();
	}

	@Override
	public void stopListeningForTopic(String topicName) {
		LG.sout("User#stopListeningForTopic(%s)", topicName);
		LG.in();

		if (userIsNotSubscribed(topicName)) {
			userStub.fireEvent(UserEvent.failed(Tag.TOPIC_LISTEN_STOPPED, topicName,
					new NoSuchElementException("This User can't unsubscribe from Topic " + topicName
					                           + " because they aren't subscribed to it")));
		} else {
			consumer.stopListeningForTopic(topicName);
		}

		LG.out();
	}

	@Override
	public void addUserListener(UserListener l) {
		listener.addListener(l);
	}

	@Override
	public boolean removeUserListener(UserListener l) {
		return listener.removeListener(l);
	}

	private boolean userIsNotSubscribed(String topicName) {
		return currentProfile.getTopics().stream()
		                     .noneMatch(topic -> topic.getName().equals(topicName));
	}

	/**
	 * Exposes the event processing capabilities of the User. Objects of this class are injected
	 * wherever needed to allow for events to be fired to the User without exposing its whole
	 * interface.
	 *
	 * @author Alex Mandelias
	 */
	final class UserStub {

		/**
		 * Fires a user event by forwarding it to its associated User.
		 *
		 * @param e the event to fire
		 */
		void fireEvent(UserEvent e) {
			switch (e.tag) {
			case MESSAGE_SENT:
				listener.onMessageSent(e);
				break;
			case MESSAGE_RECEIVED:
				listener.onMessageReceived(e);
				break;
			case TOPIC_CREATED:
				listener.onTopicCreated(e);
				break;
			case TOPIC_DELETED:
				listener.onTopicDeleted(e);
				break;
			case SERVER_TOPIC_DELETED:
				listener.onServerTopicDeleted(e);
				break;
			case TOPIC_LISTENED:
				listener.onTopicListened(e);
				break;
			case TOPIC_LOADED:
				listener.onTopicLoaded(e);
				break;
			case TOPIC_LISTEN_STOPPED:
				listener.onTopicListenStopped(e);
				break;
			default:
				throw new IllegalArgumentException(
						"You forgot to put a case for the new UserEvent#Tag enum");
			}
		}
	}

	private static final class CompositeListener implements UserListener {

		private final Set<UserListener> listeners = new HashSet<>();

		void addListener(UserListener l) {
			listeners.add(l);
		}

		boolean removeListener(UserListener l) {
			return listeners.remove(l);
		}

		@Override
		public void onMessageSent(UserEvent e) {
			CompositeListener.log(e);

			listeners.forEach(l -> l.onMessageSent(e));
		}

		@Override
		public void onMessageReceived(UserEvent e) {
			CompositeListener.log(e);

			listeners.forEach(l -> l.onMessageReceived(e));
		}

		@Override
		public void onTopicCreated(UserEvent e) {
			CompositeListener.log(e);

			listeners.forEach(l -> l.onTopicCreated(e));
		}

		@Override
		public void onTopicDeleted(UserEvent e) {
			CompositeListener.log(e);

			listeners.forEach(l -> l.onTopicDeleted(e));
		}

		@Override
		public void onServerTopicDeleted(UserEvent e) {
			CompositeListener.log(e);

			listeners.forEach(l -> l.onServerTopicDeleted(e));
		}

		@Override
		public void onTopicListened(UserEvent e) {
			CompositeListener.log(e);

			listeners.forEach(l -> l.onTopicListened(e));
		}

		@Override
		public void onTopicLoaded(UserEvent e) {
			CompositeListener.log(e);

			listeners.forEach(l -> l.onTopicLoaded(e));
		}

		@Override
		public void onTopicListenStopped(UserEvent e) {
			CompositeListener.log(e);

			listeners.forEach(l -> l.onTopicListenStopped(e));
		}

		private static void log(UserEvent e) {
			LG.header("%s - %s - %s", e.tag, e.topicName, e.success);
		}
	}

	private final class BasicListener implements UserListener {

		@Override
		public void onMessageSent(UserEvent e) {
			if (e.success) {
				// do nothing
			} else {
				LG.exception(e.getCause());
			}
		}

		@Override
		public void onMessageReceived(UserEvent e) {
			if (e.success) {
				String topicName = e.topicName;
				currentProfile.markUnread(topicName);
			} else {
				LG.exception(e.getCause());
			}
		}

		@Override
		public void onTopicCreated(UserEvent e) {
			if (e.success) {
				listenForNewTopic(e.topicName);
			} else {
				LG.exception(e.getCause());
			}
		}

		@Override
		public void onTopicDeleted(UserEvent e) {
			if (e.success) {
				removeTopicLocally(e);
			} else {
				LG.exception(e.getCause());
			}
		}

		@Override
		public void onServerTopicDeleted(UserEvent e) {
			if (e.success) {
				// do nothing
			} else {
				LG.exception(e.getCause());
			}
		}

		@Override
		public void onTopicListened(UserEvent e) {
			if (e.success) {
				currentProfile.addTopic(e.topicName);
				try {
					profileDao.createTopicForCurrentProfile(e.topicName);
				} catch (IOException e1) {
					userStub.fireEvent(UserEvent.failed(e.tag, e.topicName, e1));
				}
			} else {
				LG.exception(e.getCause());
			}
		}

		@Override
		public void onTopicLoaded(UserEvent e) {
			if (e.success) {
				// do nothing
			} else {
				LG.exception(e.getCause());
			}
		}

		@Override
		public void onTopicListenStopped(UserEvent e) {
			if (e.success) {
				removeTopicLocally(e);
			} else {
				LG.exception(e.getCause());
			}
		}

		private void removeTopicLocally(UserEvent e) {
			currentProfile.removeTopic(e.topicName);
			try {
				profileDao.deleteTopicFromCurrentProfile(e.topicName);
			} catch (IOException e1) {
				userStub.fireEvent(UserEvent.failed(e.tag, e.topicName, e1));
			}
		}
	}
}
