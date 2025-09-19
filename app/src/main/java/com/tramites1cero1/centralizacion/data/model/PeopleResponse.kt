package com.tramites1cero1.centralizacion.data.model

data class PeopleResponse(
    val names: List<Name>?,
    val emailAddresses: List<Email>?,
    val phoneNumbers: List<Phone>?,
    val birthdays: List<Birthday>?,
    val addresses: List<Address>?
)

data class Name(val displayName: String?, val givenName: String?, val familyName: String?)
data class Email(val value: String?)
data class Phone(val value: String?)
data class Birthday(val date: Date?)
data class Address(val formattedValue: String?)
data class Date(val year: Int?, val month: Int?, val day: Int?)