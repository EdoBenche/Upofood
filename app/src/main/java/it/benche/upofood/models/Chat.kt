package it.benche.upofood.models

class Chat {

    private lateinit var sender: String
    private lateinit var receiver: String
    private lateinit var message: String
    private lateinit var time: String

    constructor(sender: String, receiver: String, message: String, time: String) {
        this.sender = sender
        this.receiver = receiver
        this.message = message
        this.time = time
    }

    constructor() {

    }

    fun getSender(): String {
        return this.sender
    }

    fun getMessage(): String {
        return this.message
    }

    fun getReceiver(): String {
        return this.receiver
    }

    fun getTime(): String {
        return this.time
    }

    fun setSender(sender: String) {
        this.sender = sender
    }

    fun setReceiver(receiver: String) {
        this.receiver = receiver
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun setTime(time: String) {
        this.time = time
    }
}