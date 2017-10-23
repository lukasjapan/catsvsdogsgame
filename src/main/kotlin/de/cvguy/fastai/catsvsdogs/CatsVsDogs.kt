package de.cvguy.fastai.catsvsdogs

import de.cvguy.fastai.catsvsdogs.game.CatsVsDogsGame
import de.cvguy.fastai.catsvsdogs.twitterclient.TwitterGameClient
import de.cvguy.fastai.catsvsdogs.web.KtorGameClient
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    // Warning: The game is not threadsafe! So there might be complications.
     val game = CatsVsDogsGame()

//    val t1 = thread(true) {
//        val twitterClient = TwitterGameClient(game)
//        twitterClient.track()
//    }

//    val t2 = thread(true) {
        val webServer = KtorGameClient(game)
        webServer.serve()
//    }

//    t1.join()
//    t2.join()
}