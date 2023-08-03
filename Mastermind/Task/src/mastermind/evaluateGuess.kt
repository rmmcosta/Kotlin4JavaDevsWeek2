package mastermind

data class Evaluation(val rightPosition: Int, val wrongPosition: Int)

fun evaluateGuess(secret: String, guess: String): Evaluation {
    return evaluateGuessWithMaps(secret, guess)
}

private fun evaluateGuessWithMaps(secret: String, guess: String): Evaluation {
    val secretMap: MutableMap<Int, Char> = mutableMapOf()
    secret.mapIndexed { index, c -> secretMap += (index to c) }
    val guessMap: MutableMap<Int, Char> = mutableMapOf()
    guess.mapIndexed { index, c -> guessMap += (index to c) }

    //1st check right positions
    val rightPositions = checkRightPositions(guessMap, secretMap)

    val guessMapAfterRemovingRightPositions =
        guessMap.filter { entry -> !rightPositions.contains((entry.key to entry.value)) }.toMutableMap()
    val secretAfterRemovingRightPositions =
        secretMap.filter { entry -> !rightPositions.contains((entry.key to entry.value)) }.toMutableMap()

    //2nd the one left if there are the same letters they are all in the wrong positions
    val wrongPositions = checkWrongPositions(guessMapAfterRemovingRightPositions, secretAfterRemovingRightPositions)

    return Evaluation(rightPositions.size, wrongPositions.size)
}

private fun checkRightPositions(
    guessMap: MutableMap<Int, Char>,
    secretMap: MutableMap<Int, Char>
): List<Pair<Int, Char>> {
    val markedToRemove: MutableList<Pair<Int, Char>> = mutableListOf()
    for ((idx, char) in guessMap) {
        if (secretMap[idx] == char) {
            markedToRemove += (idx to char)
        }
    }
    return markedToRemove
}

private fun checkWrongPositions(
    guessMap: MutableMap<Int, Char>,
    secretMap: MutableMap<Int, Char>
): List<Pair<Int, Char>> {
    //we can have repeated letters on the guess and not the same number on the secret
    //keep record of taken positions
    val wrongPositions: MutableList<Pair<Int, Char>> = mutableListOf()
    val takenPositions: MutableList<Int> = mutableListOf()
    for (char in guessMap.values) {
        val indexFound =
            getFirstIndexNotTakenForGivenValue(valuesMap = secretMap, takenPositions = takenPositions, lookupValue = char)
        if (indexFound != null) {
            takenPositions += indexFound
            wrongPositions += (indexFound to char)
        }
    }
    return wrongPositions
}

private fun getFirstIndexNotTakenForGivenValue(valuesMap: Map<Int, Char>, takenPositions: List<Int>, lookupValue: Char): Int? {
    if (!valuesMap.containsValue(lookupValue)) {
        return null
    }
    for ((idx, char) in valuesMap) {
        if (!takenPositions.contains(idx) && char == lookupValue) {
            return idx
        }
    }
    return null
}