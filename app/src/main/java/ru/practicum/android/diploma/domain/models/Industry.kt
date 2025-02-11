package ru.practicum.android.diploma.domain.models

class Industry(
    val id: String,
    val name: String
) {
    override fun equals(other: Any?): Boolean {
        return if (other is Industry) {
            this.id == other.id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
