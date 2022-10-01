package android.example.health.models

data class Doctor(
    val doctorName: String = "",
    val doctorImage: String = "",
    val doctorGender: String = "",
    val doctorCity: String = "",
    val doctorMobile: String = "",
    val doctorRegistrationID: String = "",
    val doctorRegistrationCouncil: String = "",
    val doctorQualification: String = "",
    val doctorRegistrationYear: Int = 0,
    val doctorSpeciality: String = "",
    val doctorExperience: Double = 0.0,
    val doctorConsultationFee: Int = 0,
    val phoneNumber: Long = 0,
    val slots: ArrayList<String> = ArrayList(),
    val appointment: MutableMap<String, String> = mutableMapOf()
)
