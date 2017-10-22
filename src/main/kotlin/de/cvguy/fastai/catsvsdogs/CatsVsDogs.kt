package de.cvguy.fastai.catsvsdogs

import de.cvguy.fastai.catsvsdogs.game.CatsVsDogsGame
import de.cvguy.fastai.catsvsdogs.twitterclient.TwitterGameClient
import de.cvguy.fastai.catsvsdogs.web.KtorWebServerClient
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import org.datavec.image.loader.NativeImageLoader
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator
import org.deeplearning4j.eval.Evaluation
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.activations.impl.ActivationReLU
import org.nd4j.linalg.activations.impl.ActivationSoftmax
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.learning.config.Nesterovs
import org.nd4j.linalg.lossfunctions.LossFunctions
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    // Warning: The game is not threadsafe! So there might be complications.
    val game = CatsVsDogsGame()

//    val t1 = thread(true) {
//        val twitterClient = TwitterGameClient(game)
//        twitterClient.track()
//    }

    val t2 = thread(true) {
        val webServer = KtorWebServerClient(game)
        webServer.serve()
    }

    //t1.join()
    t2.join()
}

    // some dirty non optimized code that i used for training the model images
    fun train() {
        val conf = NeuralNetConfiguration.Builder()
            .seed(ThreadLocalRandom.current().nextLong())
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .iterations(1)
            .learningRate(0.006)
            .updater(Nesterovs(0.9))
            .regularization(true).l2(1e-4)
            .list()

            .layer(0, DenseLayer.Builder()
                    .nIn(1000) // Number of input datapoints.
                    .nOut(1000) // Number of output datapoints.
                    .activation(ActivationReLU()) // Activation function.
                    .weightInit(WeightInit.XAVIER) // Weight initialization.
                    .build()
            )
            .layer(1, OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .nIn(1000)
                    .nOut(2)
                    .activation(ActivationSoftmax())
                    .weightInit(WeightInit.XAVIER)
                    .build()
            )
            .pretrain(false).backprop(true)
            .build()

        val model = MultiLayerNetwork(conf)
        model.init()
        model.setListeners(ScoreIterationListener(50))

        val catLabels = ObjectInputStream(FileInputStream("/Users/lukas/dogscats/catlabels.ser")).readObject() as List<INDArray>
        val dogLabels = ObjectInputStream(FileInputStream("/Users/lukas/dogscats/doglabels.ser")).readObject() as List<INDArray>
        val cat = Nd4j.create(doubleArrayOf(1.0,0.0))
        val dog = Nd4j.create(doubleArrayOf(0.0,1.0))

        val trainingData = catLabels.map { DataSet(it, cat) } + dogLabels.map { DataSet(it, dog) }

        Collections.shuffle(trainingData)

        model.fit(ListDataSetIterator(trainingData))

        //ModelSerializer.writeModel(model, "/Users/lukas/dogscats/model.dl4j", true)

        Collections.shuffle(trainingData)

        trainingData.take(10).map {
            println(it.labels)
            println(model.output(it.features))
        }

//    val b = ObjectInputStream(FileInputStream("/Users/lukas/dogscats/doglabels.ser")).readObject() as List<INDArray>
    }

//    fun prepare() {
//            val catlabels = FileUtils.listFiles(
//            File("/Users/lukas/dogscats/train/cats"),
//            TrueFileFilter.INSTANCE,
//            TrueFileFilter.INSTANCE
//    ).map {
//        println("Predicting: ${it.absolutePath}")
//        val input = NativeImageLoader(224, 224, 3).asMatrix(it).also { VGG16ImagePreProcessor().transform(it) }
//        model.outputSingle(input).also {
//            println("Predicted: ${it.toResult().sortedByDescending { it.second }.take(5)}")
//        }
//    }
//
//    val doglabels = FileUtils.listFiles(
//            File("/Users/lukas/dogscats/train/dogs"),
//            TrueFileFilter.INSTANCE,
//            TrueFileFilter.INSTANCE
//    ).map {
//        println("Predicting: ${it.absolutePath}")
//        val input = NativeImageLoader(224, 224, 3).asMatrix(it).also { VGG16ImagePreProcessor().transform(it) }
//        val output = model.outputSingle(input)
//        model.outputSingle(input).also {
//            println("Predicted: ${it.toResult().sortedByDescending { it.second }.take(5)}")
//        }
//    }

//    ObjectOutputStream(FileOutputStream("/Users/lukas/dogscats/catlabels.ser")).writeObject(catlabels)
//    ObjectOutputStream(FileOutputStream("/Users/lukas/dogscats/doglabels.ser")).writeObject(doglabels)
//    }