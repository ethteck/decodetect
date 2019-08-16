# Decodetect
Decodetect is a text encoding detection library designed to support encodings that many other libraries don't. It contains the infrastructure to train and test custom models, and everything is written in pure Java to maximize portability.

Models encode byte bigram frequency counts. At runtime, input data is converted to this same byte bigram frequency format and compared with the trained models via cosine similarity.

The training data that creates the distributed model is gathered through Wikipedia (see module `train`). However, it is possible to supply one's own training data and train a more specialized model as well.

## Usage
Decodetect can be found at [Maven Central](https://mvnrepository.com/artifact/com.ethteck.decodetect/decodetect-core/).

Using Decodetect involves simply creating an instance of `Decodetect` and then passing a `byte[]` to `getResults()`:

```java
byte[] rawBytes = Files.readAllBytes(somePath);

Decodetect decodetect = new Decodetect();
DecodetectResult topResult = decodetect.getResults(rawBytes).get(0);
Charset detectedCharset = topResult.getEncoding();

String decoded = new String(rawBytes, detectedCharset);
```

The confidence metric is a measure of how similar the input bytes represent the model trained on the encoding. For most use cases, the top result returned can simply be used, but 


## Supported Encodings
Decodetect supports a myriad of encodings for many languages. The bundled model has specific encodings for each language, but all languages support the following encodings as well:

* UTF-7
* UTF-8
* UTF-16 BE
* UTF-16 LE
* UTF-32 BE
* UTF-32 LE

For more information on the encodings and languages supported by Decodetect, see `Encodings.java`.


## Project Structure
Decodetect can be built simply with maven. The modules are as follows:

* `core` Used at runtime, contains the main `Decodetect` class for encoding detection.

* `train` Used for downloading training data from Wikipedia and training new models.

## Dependencies
Runtime:

* [jutf7](http://jutf7.sourceforge.net/) for UTF-7 Charset support ([MIT](https://opensource.org/licenses/MIT))

Training:

* [gson](https://github.com/google/gson) for parsing json to extract text from Wikipedia ([Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0))

Testing:

* [JUnit 5](https://junit.org/junit5/) ([Eclipse 2.0](https://www.eclipse.org/legal/epl-2.0/))

## About
Decodetect was written by Ethan Roseman and uses the MIT license. See the `LICENSE` file for more information.
