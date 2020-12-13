package com.emanuelmairoll


val ci1 = ContactInfo(Id(1), "Apple", "Testname1")
val ci2 = ContactInfo(Id(2), "Banana", "Testname2")
val ci3 = ContactInfo(Id(Int.MAX_VALUE), "Clownfish", "Testname3")

val m1c12 = Message(Id(1), ci1, "Message 1 in Chat 1", now())
val m2c12 = Message(Id(2), ci2, "Message 2 in Chat 1", now())
val m3c12 = Message(Id(3), ci1, "Message 3 in Chat 1", now())
val m1c13 = Message(Id(4), ci3, "Message 1 in Chat 2", now())
val m2c13 = Message(Id(5), ci1, "Message 2 in Chat 2", now())
val m3c13 = Message(Id(6), ci3, "Message 3 in Chat 2", now())
val m1c123 = Message(Id(7), ci2, "Message 1 in Chat 3", now())
val m2c123 = Message(Id(8), ci3, "Message 2 in Chat 3", now())
val m3c123 = Message(Id(9), ci1, "Message 3 in Chat 3", now())
val m4c123 = Message(Id(10), ci2, "Message 4 in Chat 3", now())

val c12 = Chat(Id(1), "Testchat1", mutableSetOf(ci1, ci2), mutableSetOf(m1c12, m2c12, m3c12))
val c13 = Chat(Id(2), "Testchat2", mutableSetOf(ci1, ci3), mutableSetOf(m1c13, m2c13, m3c13))
val c123 = Chat(Id(3), "Testchat3", mutableSetOf(ci1, ci2, ci3), mutableSetOf(m1c123, m2c123, m3c123, m4c123))

val u1 = User(ci1, "0660 111111", "testuser1@mail.com", mutableSetOf(c12, c13, c123))
val u2 = User(ci2, "0660 222222", "testuser2@mail.com", mutableSetOf(c123))
val u3 = User(ci3, "0660 333333", "testuser3@mail.com", mutableSetOf(c13, c123))

val allTestUsers = mutableSetOf(u1, u2, u3)