package com.emanuelmairoll

import kotlin.random.Random

data class Id(val value: Int) {
    constructor() : this(Random.nextInt())
}


data class ContactInfo(
    var id: Id,
    var username: String,
    var name: String,
)

data class User(
    var contactInfo: ContactInfo,
    var telephoneNumber: String,
    var mail: String,
    var chats: MutableSet<Chat>,
) {
    fun getChat(chatId: Id): Chat? =
        chats.find { it.id == chatId }
}

data class Chat(
    var id: Id,
    var chatName: String,
    var members: MutableSet<ContactInfo>,
    var messages: MutableSet<Message>,
){
    fun messages(last: Int): List<Message> = messages.sortedBy { it.timestamp }.takeLast(last)
}

data class Message(
    var id: Id,
    var author: ContactInfo,
    var value: String,
    var timestamp: Long = now(),
    var attachmentLink: String? = null,
)

fun now() = System.currentTimeMillis()

data class UserInput(
    var username: String? = null,
    var name: String? = null,
    var telephoneNumber: String? = null,
    var mail: String? = null,
)
