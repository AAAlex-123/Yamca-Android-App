package alexman.yamca.eventdeliverysystem.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;

import alexman.yamca.eventdeliverysystem.dao.IProfileDAO;

/**
 * Holder for an {@code IUser} instance.
 *
 * @author Alex Mandelias
 * @see IUser
 */
public interface IUserHolder {

	/**
	 * Returns the IUser object that this Holder is responsible for.
	 *
	 * @return this Holder's User object
	 */
	IUser get();

	/**
	 * Configures this Holder's IUser object. This method must be the first call to an IUserHolder.
	 *
	 * @param ip the IP to which this Holder's IUser object will connect
	 * @param port the port to which this Holder's IUser object will connect
	 * @param profileDao this Holder's IUser's IProfileDAO object
	 *
	 * @throws UnknownHostException if no IP address for the host could be found, or if a scope_id
	 * 		was specified for a global IPv6 address while resolving the defaultServerIP
	 */
	default void configure(String ip, int port, IProfileDAO profileDao) throws UnknownHostException {
		configure(InetAddress.getByName(ip), port, profileDao);
	}

	/**
	 * Configures this Holder's IUser object. This method must be the first call to an IUserHolder.
	 *
	 * @param ip the IP to which this Holder's IUser object will connect
	 * @param port the port to which this Holder's IUser object will connect
	 * @param profileDao this Holder's IUser's IProfileDAO object
	 *
	 * @throws UnknownHostException if no IP address for the host could be found, or if a scope_id
	 * 		was specified for a global IPv6 address while resolving the defaultServerIP
	 */
	default void configure(byte[] ip, int port, IProfileDAO profileDao) throws UnknownHostException {
		configure(InetAddress.getByAddress(ip), port, profileDao);
	}

	/**
	 * Configures this Holder's IUser object. This method must be the first call to an IUserHolder.
	 *
	 * @param ip the IP to which this Holder's IUser object will connect
	 * @param port the port to which this Holder's IUser object will connect
	 * @param profileDao this Holder's IUser's IProfileDAO object
	 */
	void configure(InetAddress ip, int port, IProfileDAO profileDao);

	/**
	 * Switches this Holder's IUser object to manage a new Profile.
	 *
	 * @param profileName the name of the new Profile
	 *
	 * @throws ServerException if the connection to the server could not be established
	 * @throws IOException if an I/O error occurs while interacting with the IProfileDAO object
	 * @throws IllegalArgumentException if a Profile with the given name already exists
	 */
	void switchToNewProfile(String profileName) throws IOException;

	/**
	 * Switches this Holder's IUser object to manage an existing Profile.
	 *
	 * @param profileName the name of the existing Profile
	 *
	 * @throws ServerException if the connection to the server could not be established
	 * @throws IOException if an I/O error occurs while interacting with the IProfileDAO object
	 * @throws NoSuchElementException if no Profile with that name exists
	 */
	void switchToExistingProfile(String profileName) throws IOException;
}
