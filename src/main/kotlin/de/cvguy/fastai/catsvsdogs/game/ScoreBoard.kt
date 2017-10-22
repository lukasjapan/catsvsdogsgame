package de.cvguy.fastai.catsvsdogs.game

import de.cvguy.fastai.catsvsdogs.vgg16.AnimalType
import de.cvguy.fastai.catsvsdogs.vgg16.VGG16Label

/**
 * A class that represents the current ranking
 */
class ScoreBoard {
    data class ScoreEntry(
            val name: String,
            val score: Pair<AnimalType, Double>,
            val others: List<Pair<VGG16Label, Double>>
    )

    /**
     * The entries with a public read only representation
     */
    private val _entries: MutableList<ScoreEntry> = mutableListOf()
    val entries: List<ScoreEntry> = _entries

    /**
     * Empty the ranking list
     */
    fun reset() = _entries.clear()

    /**
     * Adds a new entry into the list and returns the place in the scoreboard
     */
    fun addEntry(entry: ScoreEntry): Int? {
        _entries.add(entry)
        _entries.sortByDescending { it.score.second }
        //while(_entries.count() > 10) { _entries.remove(_entries.last()) }
        return getRank(entry)
    }

    fun getRank(entry: ScoreEntry) = _entries.indexOf(entry).let { if(it == -1) null else it + 1 }
}