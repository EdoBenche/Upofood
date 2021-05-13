package it.benche.upofood.notifications

import kotlin.properties.Delegates

class Data {

    private lateinit var user: String
    private var icon by Delegates.notNull<Int>()
    private lateinit var body:String
    private lateinit var title:String
    private lateinit var sented:String

    constructor(user:String, icon:Int, body:String, title:String, sented:String) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.sented = sented
    }

    constructor() {}

    fun getUser():String {
        return user
    }

    fun setUser(user:String) {
        this.user = user
    }

    fun geticon():Int {
        return icon
    }

    @JvmName("setIcon1")
    fun setIcon(icon: Int) {
        this.icon = icon
    }

    fun getBody():String {
        return body
    }

    fun setBody(body:String) {
        this.body = body
    }

    fun getTitle():String {
        return title
    }

    fun setTitle(title:String) {
        this.title = title
    }

    fun getSended():String {
        return sented
    }

    fun setSented(sented: String) {
        this.sented = sented
    }
}