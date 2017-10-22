package de.cvguy.fastai.catsvsdogs.twitterclient;

import twitter4j.StatusListener;
import twitter4j.TwitterStream;

// https://stackoverflow.com/questions/45781964/how-to-make-kotlin-stop-casting-argument-to-wrong-classinterface
class Twitter4jFixer {
    public static void addListener(TwitterStream stream, StatusListener listener) {
        stream.addListener(listener);
    }
}
