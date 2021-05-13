package it.benche.upofood.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable



open class Card {
    @SerializedName("card_img")
    var cardImg: Int? = null
    @SerializedName("card_type")
    var cardType: String? = null

    @SerializedName("bank_name")
    var bankName: String? = null

    @SerializedName("card_number")
    var cardNumber: String? = null

    @SerializedName("valid_date")
    var validDate: String? = null

    @SerializedName("holder_name")
    var holderName: String? = null
}

open class Address : Serializable {

    @SerializedName("address")
    var address: String? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("pin_code")
    var pincode: String? = null

    @SerializedName("provincia")
    var provincia: String? = null

    @SerializedName("is_default")
    var isDefault: Boolean? = false

}

open class Reward {
    @SerializedName("reward_img")
    var rewardImg: Int? = null
    @SerializedName("reward_value")
    var rewardValue: String? = null

    @SerializedName("reward")
    var reward: Int? = null

}
data class Attribute(
        val count: Int = 0,
        val description: String = "",
        val id: Int = 0,
        val menu_order: Int = 0,
        val name: String = "",
        val slug: String = ""
):Serializable