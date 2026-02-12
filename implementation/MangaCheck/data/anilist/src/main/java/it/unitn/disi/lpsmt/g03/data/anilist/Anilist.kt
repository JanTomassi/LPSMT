package it.unitn.disi.lpsmt.g03.data.anilist

import com.apollographql.apollo3.ApolloClient
import it.unitn.disi.lpsmt.g03.data.graphql.SearchByNameQuery

class Anilist private constructor() {

    companion object {
        @Volatile
        private var instance: Anilist? = null

        fun getInstance(): Anilist {
            return instance ?: run {
                synchronized(this) {
                    instance = instance ?: Anilist()
                }
                instance!!
            }
        }
    }

    private val apolloClient by lazy { ApolloClient.Builder().serverUrl("https://graphql.anilist.co").build() }

    suspend fun searchByName(search: String) = apolloClient.query(SearchByNameQuery(search)).execute()
}