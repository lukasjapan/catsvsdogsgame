package de.cvguy.fastai.catsvsdogs.twitterclient

import de.cvguy.fastai.catsvsdogs.game.CatsVsDogsGame
import java.net.URL
import twitter4j.*
import twitter4j.conf.ConfigurationBuilder
import java.lang.Exception
import twitter4j.FilterQuery
import twitter4j.util.CharacterUtil


class TwitterGameClient(
        val game: CatsVsDogsGame
) {
    val conf = ConfigurationBuilder()
            .setOAuthConsumerKey("")
            .setOAuthConsumerSecret("")
            .setOAuthAccessToken("")
            .setOAuthAccessTokenSecret("")
            .build()

    val twitter = TwitterFactory(conf).instance
    val stream =  TwitterStreamFactory(conf).instance

    val listener = object : StatusListener {
        override fun onTrackLimitationNotice(numberOfLimitedStatuses: Int) {
            println(numberOfLimitedStatuses)
        }

        override fun onStallWarning(warning: StallWarning?) {
            println(warning)
        }

        override fun onException(ex: Exception?) {
            println(ex)
        }

        override fun onDeletionNotice(statusDeletionNotice: StatusDeletionNotice?) {
            println(statusDeletionNotice)
        }

        override fun onStatus(status: Status?) {
            println(status)
            status ?: return

            // Prevent duplicate entries
            if(game.scoreBoard.entries.map { it.name }.contains(status.user.screenName)) return

            val profileURL       = URL(status.user.originalProfileImageURL)
            // val attachedImageURL = status.mediaEntities.find { it.type == "photo" }?.let { URL(it.mediaURL) } ?: return

            // Submit to game
            val entry = game.addEntry(status.user.screenName, profileURL.openStream())
            val rank = game.scoreBoard.getRank(entry)

            // val profileLabels = game.vgg16.topVGG16LabelsOfImage(profileURL.openStream())

            val labelString = entry.others.joinToString("\n") {
                "${it.first.en}  ${it.first.ja} ${"%.2f".format(it.second * 100)}%"
            }

            tweet("""#m3kt 犬vs猫 @${status.user.screenName}のスコア:
${rank?:"-"}位 ${entry.score.first} ${"%.2f".format(entry.score.second * 100)}%

VGG16プロフィール診断：
${labelString}""")
        }

        override fun onScrubGeo(userId: Long, upToStatusId: Long) {
            println(userId)
            println(upToStatusId)
        }
    }

    fun track() {
        stream.addListenerFixed(listener)
        stream.filter(FilterQuery("#m3kt", "#catsdogs"))
    }

    fun tweet(text: String) {
        var trimmed = text
        // is there a function for this in twitter4j? is this safe with japanese input?
        while(CharacterUtil.isExceedingLengthLimitation(trimmed)) trimmed = trimmed.dropLast(1)

        val tweet = StatusUpdate(trimmed)

//        println("Dummy tweet:")
//        println(tweet)

        val status = twitter.updateStatus(tweet)
        println(status)
    }
}

// https://stackoverflow.com/questions/45781964/how-to-make-kotlin-stop-casting-argument-to-wrong-classinterface
fun TwitterStream.addListenerFixed(listener: StatusListener) {
    Twitter4jFixer.addListener(this, listener)
}