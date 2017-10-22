package de.cvguy.fastai.catsvsdogs.vgg16

import org.datavec.image.loader.NativeImageLoader
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.util.ModelSerializer
import org.deeplearning4j.zoo.PretrainedType
import org.deeplearning4j.zoo.model.VGG16
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor
import java.io.InputStream

class VGG16 {
    val catsdogsModel = ModelSerializer.restoreMultiLayerNetwork(javaClass.getResource("/catdogmodel.dl4j").openStream())
    val model = VGG16().initPretrained(PretrainedType.IMAGENET) as ComputationGraph

    fun getCatDogScore(image: InputStream): Pair<AnimalType, Double> {
        val input = NativeImageLoader(224, 224, 3).asMatrix(image).also { VGG16ImagePreProcessor().transform(it) }
        val output = catsdogsModel.output(model.outputSingle(input))

        val cat = output.getDouble(0)
        val dog = output.getDouble(1)

        return if(cat > dog) return Pair(AnimalType.CAT, cat) else Pair(AnimalType.DOG, dog)
    }

    fun topVGG16LabelsOfImage(image: InputStream): List<Pair<VGG16Label, Double>> {
        val input = NativeImageLoader(224, 224, 3).asMatrix(image).also { VGG16ImagePreProcessor().transform(it) }
        val output = model.outputSingle(input)

        return output.toResult().sortedByDescending { it.second }.take(3)
    }
}

fun INDArray.getRowAsList(i: Int) = (1..size(1)).map { this.getDouble(i, it-1) }
fun INDArray.getFirstRowAsList() = getRowAsList(0)
fun INDArray.toResult() = getFirstRowAsList().mapIndexed { i, v -> Pair(allVGG16Labels[i], v) }