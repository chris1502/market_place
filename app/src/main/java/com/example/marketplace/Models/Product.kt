package com.example.marketplace.Models

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val imageResId: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val senderId: String,
    val userId: String,
    val productId: String,
    val deviceToken: String
) : Parcelable {
    constructor() : this(0, "", "", 0.0, "", "", "", "", "")

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(imageResId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeDouble(price)
        parcel.writeString(image)
        parcel.writeString(senderId)
        parcel.writeString(userId)
        parcel.writeString(productId)
        parcel.writeString(deviceToken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
