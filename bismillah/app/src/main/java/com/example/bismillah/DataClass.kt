package com.example.bismillah

import android.os.Parcel
import android.os.Parcelable

data class DataClass(
    // ID gambar utama
    var dataTitle: String,    // Judul data
    var dataPrice: String,    // Harga data
    var dataDesc: String,
    val dataImage: String,// Deskripsi singkat item
    val dataDetailImage: String  // ID gambar detail
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dataImage)
        parcel.writeString(dataTitle)
        parcel.writeString(dataPrice)
        parcel.writeString(dataDesc)
        parcel.writeString(dataDetailImage)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<DataClass> {
        override fun createFromParcel(parcel: Parcel): DataClass {
            return DataClass(parcel)
        }

        override fun newArray(size: Int): Array<DataClass?> {
            return arrayOfNulls(size)
        }
    }

    // Sub-class untuk model tambahan
    data class Pesanan(
        val nama: String,
        val harga: String
    )

    // OrderHistory
    data class OrderHistory(
        val idTransaksi: String = "",
        val totalHarga: Int = 0,
        val metodePembayaran: String = "",
        val tanggalTransaksi: String = "",
        val namaProduk: String = "",
        val quantity: Int = 1
    )




    // Minuman
    data class Minuman(
        val image: Int,
        val name: String,
        val price: String,
        val estimation: String
    )

    // Makanan: Tambahkan Parcelable

    data class Makanan(
        val nama: String,
        val harga: String,
        val gambarUrl: String,
        val description: String = "" // Berikan nilai default kosong
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: ""
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(nama)
            parcel.writeString(harga)
            parcel.writeString(gambarUrl)
            parcel.writeString(description)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Makanan> {
            override fun createFromParcel(parcel: Parcel): Makanan {
                return Makanan(parcel)
            }

            override fun newArray(size: Int): Array<Makanan?> {
                return arrayOfNulls(size)
            }
        }
    }

}
