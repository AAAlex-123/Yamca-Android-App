package alexman.yamca.app

import alexman.yamca.eventdeliverysystem.client.IUser
import alexman.yamca.eventdeliverysystem.client.IUserHolder
import alexman.yamca.eventdeliverysystem.client.User
import alexman.yamca.eventdeliverysystem.dao.IProfileDAO
import alexman.yamca.eventdeliverysystem.util.LG
import android.util.Log
import java.io.OutputStream
import java.io.PrintStream
import java.net.InetAddress

object UserSingleton : IUser by Holder.get() {

    init {
        val tag = "LG"

        val outputStream = object : PrintStream(object : OutputStream() {
            override fun write(b: Int) {}
        }) {
            override fun printf(format: String, vararg args: Any): PrintStream {
                Log.d(tag, String.format(format, *args))
                return this
            }
        }

        val errorStream = object : PrintStream(object : OutputStream() {
            override fun write(b: Int) {}
        }) {
            override fun printf(format: String, vararg args: Any): PrintStream {
                Log.e(tag, String.format(format, *args))
                return this
            }
        }

        LG.setOut(outputStream)
        LG.setErr(errorStream)
    }

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
