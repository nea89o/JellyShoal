package moe.nea.jellyshoal.data

class Preferences(val _store: DataStore) {
	val testValue = _store.createStringValue("server")
}
