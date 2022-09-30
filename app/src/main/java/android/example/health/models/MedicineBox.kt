package android.example.health.models

data class MedicineBox (
    //val medicineImage:String = "",
    val medicineName:String  = "",
    val medicineManufacturer:String  = "",
    val medicineMRP:String  = "",
    val medicineManufactureDate:String = "",
    val medicineExpiryDate:String  = "",
    var ContactUserName:String = "",
    var ContactUserEmailID:String = "",
    var ContactUserMobileNumber: String? = "",
    var ContactUserCity : String = "",
    var ContactUserState : String = "",
    var ContactUserCountry : String = "",
    val createdAt : Long =0L,
    val createdBy : User=User()
)