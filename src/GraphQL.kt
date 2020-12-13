package com.emanuelmairoll

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.KGraphQL
import com.apurebase.kgraphql.schema.dsl.operations.subscribe
import java.util.*

private val chatService = ChatService()

val schema = KGraphQL.schema {
    configure {
        useDefaultPrettyPrinter = true
    }

    intScalar<Id> {
        deserialize = { id: Int -> Id(id) }
        serialize = { id: Id -> id.value }
    }

    type<User>()
    type<ContactInfo>()
    type<Chat>()
    type<Message>()

    inputType<UserInput>()

    query("user") {
        resolver { id: Id, ctx: Context ->
            if (ctx.get<Id>() == id)
                chatService.dataForUser(id)!!
            else null
        }
    }

    mutation("updateDataForUser") {
        resolver { id: Id, input: UserInput, ctx: Context ->
            if (ctx.get<Id>() == id)
                chatService.updateDataForUser(id, input)!!
            else null
        }
    }

    query("contactInfo") {
        resolver { id: Id ->
            chatService.contactInfoForUser(id)
        }
    }

    query("findContactInfo") {
        resolver { query: String, limit: Int? ->
            chatService.findContactInfo(query, limit ?: 10)
        }
    }

    mutation("createChat") {
        resolver { chatName: String, members: List<Id>, ctx: Context ->
            if (members.contains(ctx.get<Id>()))
                chatService.createChat(chatName, members.toSet())
            else null
        }
    }

    mutation("postInChat") {
        resolver { userId: Id, chatId: Id, messageText: String, ctx: Context ->
            if (ctx.get<Id>() == userId && chatService.dataForUser(userId)?.getChat(chatId) != null)
                chatService.postInChat(userId, chatId, messageText)
            else null
        }
    }

    mutation("leaveChat") {
        resolver { userId: Id, chatId: Id, ctx: Context ->
            if (ctx.get<Id>() == userId && chatService.dataForUser(userId)?.getChat(chatId) != null)
                chatService.leaveChat(userId, chatId)
            else null
        }
    }

    /*
    subscription("notifications") {
        resolver { userId: Id, ctx: Context ->
            if (ctx.get<Id>() == userId)
                chatService.notificationDispatcher(userId)!!
            else null
        }
    }
    */


}



