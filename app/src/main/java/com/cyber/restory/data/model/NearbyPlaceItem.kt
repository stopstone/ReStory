package com.cyber.restory.data.model

sealed class NearbyPlaceItem {
    data class Title(val title: String) : NearbyPlaceItem()
    data class PlaceList(val places: List<LocationBasedTourItem>) : NearbyPlaceItem()
}