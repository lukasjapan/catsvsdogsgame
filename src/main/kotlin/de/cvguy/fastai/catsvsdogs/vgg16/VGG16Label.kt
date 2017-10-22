package de.cvguy.fastai.catsvsdogs.vgg16

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

data class VGG16Label(
        val num: String,
        val en: String,
        val ja: String
)

val allVGG16Labels: Array<VGG16Label> = ObjectMapper().registerKotlinModule().readValue(
        // https://gist.githubusercontent.com/PonDad/4dcb4b242b9358e524b4ddecbee385e9/raw/dda9454f74aa4fafee991ca8b848c9ab6ae0e732/imagenet_class_index.json
        // Warning: The japanese translations seem to be auto-translated
        /* src = */ VGG16Label::class.java.getResource("/imagenet_class_index.json"),
        /* valueType = */ arrayOf<VGG16Label>().javaClass
)