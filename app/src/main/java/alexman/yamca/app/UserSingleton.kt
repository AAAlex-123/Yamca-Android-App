package alexman.yamca.app

import alexman.yamca.eventdeliverysystem.client.IUser
import alexman.yamca.eventdeliverysystem.client.IUserHolder
import alexman.yamca.eventdeliverysystem.client.User
import alexman.yamca.eventdeliverysystem.dao.IProfileDAO
import java.net.InetAddress

object UserSingleton : IUser by Holder.get() {

    object Holder : IUserHolder {

        private val instance: User = User.empty()

        override fun get(): IUser {
            return instance
        }

        override fun configure(ip: InetAddress, port: Int, profileDao: IProfileDAO) {
            instance.configure(ip, port, profileDao)
        }

        override fun switchToNewProfile(profileName: String?) {
            instance.switchToNewProfile(profileName)
        }

        override fun switchToExistingProfile(profileName: String?) {
            instance.switchToExistingProfile(profileName)
        }
    }
}
