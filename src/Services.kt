package com.emanuelmairoll

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.Publisher
import com.apurebase.kgraphql.schema.Subscriber
import java.lang.Integer.min

class AuthService {
    fun userAccessForToken(accesToken: String?): Id? =
        currentAccessTokens[accesToken]?.let { Id(it) }

    private val currentAccessTokens = mapOf(
        "08ed3aef3479a0140255224e1f95a262" to 1,
        "523f52134a1e464e498f4bfd5b6d8bea" to 2,
        "96aa7f50de80c12f2fc3aa8470605f25" to 3
    )
}

class ChatService {

    private val userStorage: MutableMap<Id, User> = mutableMapOf()
    private val queueStorage: MutableMap<Id, NotificationDispatcher> = mutableMapOf()

    init {
        userStorage.putAll(allTestUsers.associateBy { it.contactInfo.id })
        queueStorage.putAll(allTestUsers.associate { it.contactInfo.id to NotificationDispatcher(it.contactInfo.id) })
    }

    fun contactInfoForUser(id: Id): ContactInfo? =
        userStorage[id]?.contactInfo

    fun dataForUser(id: Id): User? =
        userStorage[id]

    fun updateDataForUser(id: Id, input: UserInput): User? {
        userStorage[id]?.let {
            it += input
            return it
        }
        return null
    }

    operator fun User.plusAssign(input: UserInput) {
        input.username?.let { contactInfo.username = it }
        input.name?.let { contactInfo.name = it }
        input.telephoneNumber?.let { telephoneNumber = it }
        input.mail?.let { mail = it }
    }

    fun findContactInfo(query: String, limit: Int) =
        userStorage.map { it.value.contactInfo }
            .filter { it.name.contains(query, ignoreCase = true) || it.username.contains(query, ignoreCase = true) }
            .take(min(limit, 20))

    fun createChat(chatName: String, members: Set<Id>): Chat? {

        val memberUsers = members.map { memberId -> userStorage[memberId] }
        if (memberUsers.any { it == null }) return null

        val c = Chat(Id(), chatName, memberUsers.map { it!!.contactInfo }.toMutableSet(), mutableSetOf())
        memberUsers.forEach { it!!.chats.add(c) }
        return c
    }

    fun postInChat(asUser: Id, chatId: Id, messageText: String): Message? {
        val user = userStorage[asUser]
        val chat = user?.getChat(chatId)
        val message = user?.contactInfo?.let { Message(Id(), it, messageText) }
        if (message != null) {
            chat?.messages?.add(message)
            queueStorage.filter { it.key != asUser }.map { it.value }.forEach { it.notify(message) }
        }
        return message
    }

    fun leaveChat(asUser: Id, chatId: Id): Chat? {
        val user = userStorage[asUser]
        val chat = user?.getChat(chatId)
        user?.chats?.remove(chat)
        return chat
    }

    fun notificationDispatcher(forUser: Id): NotificationDispatcher? = queueStorage[forUser]
}

class NotificationDispatcher(userId: Id) : Publisher {
    private val subscribers = mutableMapOf<String, Subscriber>()

    override fun subscribe(subscription: String, subscriber: Subscriber) {
        subscribers[subscription] = subscriber
    }

    override fun unsubscribe(subscription: String) {
        subscribers.remove(subscription)
    }

    fun notify(message: Message) {
        subscribers.values.forEach { sub -> sub.onNext(message) }
    }
}
