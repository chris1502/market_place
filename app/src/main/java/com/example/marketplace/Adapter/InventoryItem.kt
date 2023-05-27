package com.example.marketplace.Adapter

import android.os.Parcel
import android.os.Parcelable

data class InventoryItem(
    val name: String?,
    val description: String?,
    val price: Double?,
    val location: String?,
    val image: String?,
    val senderId: String?,
    val productId: String?,
    val userId: String? // Add the userId property
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString() // Read userId from the parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeValue(price)
        parcel.writeString(location)
        parcel.writeString(image)
        parcel.writeString(senderId)
        parcel.writeString(productId)
        parcel.writeString(userId) // Write userId to the parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InventoryItem> {
        override fun createFromParcel(parcel: Parcel): InventoryItem {
            return InventoryItem(parcel)
        }

        override fun newArray(size: Int): Array<InventoryItem?> {
            return arrayOfNulls(size)
        }
    }
}
