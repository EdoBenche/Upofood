package it.benche.upofood.notifications

class Sender {

    public lateinit var data: Data
    public lateinit var to: String

    constructor(data: Data, to: String) {
        this.data = data
        this.to = to
    }

}