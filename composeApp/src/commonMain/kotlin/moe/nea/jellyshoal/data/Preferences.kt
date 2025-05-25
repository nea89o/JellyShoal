package moe.nea.jellyshoal.data

class Preferences(val _store: DataStore) {
	val testValue = _store.createStringValue("server")
	val accounts =
		_store.createMapValueWithPrefix("account.")
			.mapMany(
				mapper = { props ->
					val accounts = props.keys.filter { it.startsWith("account.token.") }
						.map { it.substring("account.token.".length) }
					accounts.map {
						val token = props["account.token.$it"]!!
						Account(it, token)
					}
				},
				unmapper = { accounts ->
					val props = mutableMapOf<String, String>()
					for (account in accounts) {
						props["account.token.${account.server}"] = account.token
					}
					props
				},
			)
}

data class Account(
	val server: String,
	val token: String,
)

