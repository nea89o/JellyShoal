package moe.nea.jellyshoal.data

actual object DataStore : IDataStore() {

	override fun createMapValueWithPrefix(prefix: String): DataValue<Map<String, String>> {
		TODO("Not yet implemented")
	}
}
