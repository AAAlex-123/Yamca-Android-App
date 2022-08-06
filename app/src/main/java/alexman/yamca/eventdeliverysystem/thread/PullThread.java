package alexman.yamca.eventdeliverysystem.thread;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;

import alexman.yamca.eventdeliverysystem.datastructures.AbstractTopic;
import alexman.yamca.eventdeliverysystem.util.LG;
import alexman.yamca.eventdeliverysystem.datastructures.Packet;
import alexman.yamca.eventdeliverysystem.datastructures.PostInfo;

/**
 * A Thread that reads some Posts from a stream and then posts them to a Topic as they arrive.
 *
 * @author Alex Mandelias
 */
public final class PullThread extends Thread {

	private final ObjectInputStream ois;
	private final AbstractTopic topic;
	private final Callback callback;

	/**
	 * Constructs the Thread that reads some Posts from a stream and posts them to a Topic.
	 *
	 * @param stream the input stream from which to read the Posts
	 * @param topic the Topic in which the new Posts will be added
	 */
	public PullThread(ObjectInputStream stream, AbstractTopic topic) {
		this(stream, topic, null);
	}

	/**
	 * Constructs the Thread that reads some Posts from a stream and posts them to a Topic and
	 * additionally calls a callback right before finishing execution.
	 *
	 * @param stream the input stream from which to read the Posts
	 * @param topic the Topic in which the new Posts will be added
	 * @param callback the callback to call right before finishing execution
	 *
	 * @see Callback
	 */
	public PullThread(ObjectInputStream stream, AbstractTopic topic, Callback callback) {
		super("PullThread-" + topic.getName());
		ois = stream;
		this.topic = topic;
		this.callback = callback;
	}

	@Override
	public void run() {
		LG.sout("%s#run()", getName());

		try {
			final int postCount = ois.readInt();
			LG.sout("postCount=%d", postCount);

			for (int i = 0; i < postCount; i++) {

				final PostInfo postInfo = (PostInfo) ois.readObject();

				LG.in();
				LG.sout("postInfo=%s", postInfo);
				topic.post(postInfo);

				Packet packet;
				do {
					packet = (Packet) ois.readObject();

					LG.sout("packet=%s", packet);

					topic.post(packet);
				} while (!packet.isFinal());

				LG.out();
			}

			if (callback != null) {
				callback.onCompletion(true, topic.getName(), null);
			}
		} catch (final EOFException | SocketException e) {
			if (callback != null) {
				callback.onCompletion(true, topic.getName(), e);
			}
		} catch (final ClassNotFoundException | IOException e) {
			LG.exception(e);

			if (callback != null) {
				callback.onCompletion(false, topic.getName(), e);
			}
		}

		LG.sout("#%s#run()", getName());
	}
}
