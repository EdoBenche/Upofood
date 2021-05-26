package it.benche.upofood.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


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

