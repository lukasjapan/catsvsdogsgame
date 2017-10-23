# Cats vs Dogs Deeplearning sample project

This is a sample project that uses deeplearning to distinguish between cats and dogs.

## Run

```bash
git clone https://github.com/lukasjapan/catsvsdogsgame.git
cd catsvsdogsgame
./gradlew run
```

Open https://localhost:8080/ and upload images.

Please note that this will download many hundreds of MB (packages + vgg16 model) on first start.

## Main technologies 

- [kotlin](kotlinlang.org)
- [deeplearning4j](https://deeplearning4j.org/)
- [ktor](http://ktor.io/)
- [twitter4j](http://twitter4j.org/)

## Notes

The pretrained model for recognition is included in the repository and should work out of the box.

This project was introduced at [Tokyo Dokodemo Kotlin #3](https://m3-engineer.connpass.com/event/68524/)