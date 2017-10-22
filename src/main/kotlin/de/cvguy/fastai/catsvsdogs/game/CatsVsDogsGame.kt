package de.cvguy.fastai.catsvsdogs.game

import java.io.InputStream
import de.cvguy.fastai.catsvsdogs.vgg16.VGG16
import org.apache.commons.io.IOUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Simple Cats vs Dog game that manages a score board for submitted images.
 */
class CatsVsDogsGame {
    val vgg16 = VGG16()
    val scoreBoard = ScoreBoard()

    fun addEntry(name: String, image: InputStream): ScoreBoard.ScoreEntry {
        val baos = IOUtils.toByteArray(image)

        val catDogScore = vgg16.getCatDogScore(ByteArrayInputStream(baos))
        val vgg16Labels = vgg16.topVGG16LabelsOfImage(ByteArrayInputStream(baos))

        val entry = ScoreBoard.ScoreEntry(name, catDogScore, vgg16Labels)

        scoreBoard.addEntry(entry)

        return entry
    }
}