package it.unitn.disi.lpsmt.g03.mangacheck.utils.formatterQuery

class QueryResult {

    /**
     * Give the response string of the query it divides it in a matrix of string.
     * @param [response] API response
     * @return matrix of string [x][0] id the ID and [x][1] id the name
     */
    fun parsing(response: String): Array<Array<String>> {
        val regex = Regex(
            """\(((?:(\d+), |(".+?")|('.+?'))+)\)""" // Jan goes brrrrrrr
        )
        val matches: Sequence<MatchResult> = regex.findAll(response)
        if (matches.toList().isEmpty()) {
            return Array(0) { arrayOf("", "") }
        }
        val listOfValues: List<String> = splitOnIdAndName(matches)
        val formattedResponse: Array<Array<String>> = Array(listOfValues.size / 2) {
            arrayOf("", "")
        }
        var indexOfFormattedResponse = 0
        for (index in listOfValues.indices step 2) {
            formattedResponse[indexOfFormattedResponse][0] =
                listOfValues[index] //id
            formattedResponse[indexOfFormattedResponse][1] =
                listOfValues[index + 1].removePrefix(" ").removeSurrounding("\'")
                    .removeSurrounding("\"") //name
            indexOfFormattedResponse += 1
        }
        return formattedResponse
    }

    /**
     * Due to the manga
     * "Banished from the Hero's Party, I Decided to Live a Quiet Life in the Countryside"
     * reimplemented the split on first comma.
     * @param [sequence] API result
     * @return List of tuples (id, name)
     */
    private fun splitOnIdAndName(sequence: Sequence<MatchResult>): List<String> {
        val listToReturn: MutableList<String> = mutableListOf()
        for (item in sequence.iterator()) {
            val separation = item.groupValues[1].split(",", limit = 2)
            listToReturn.add(separation[0])
            listToReturn.add(separation[1])
        }
        return listToReturn
    }
}