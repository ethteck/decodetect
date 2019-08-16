#Decodetect
Decodetect is a text encoding detection library designed to support encodings that many other libraries don't. It contains the infrastructure to train and test custom models, and everything is written in pure Java to maximize portability.

Models encode byte bigram frequency counts. At runtime, input data is converted to this same byte bigram frequency format and compared with the trained models via cosine similarity.

The training data that creates the distributed model is gathered through Wikipedia (see module `train`). However, it is possible to supply one's own training data and train a more specialized model as well.

##Supported Encodings
#####All languages:
* UTF-7
* UTF-8
* UTF-16 BE
* UTF-16 LE
* UTF-32 BE
* UTF-32 LE

##Project Structure
Decodetect can be built simply with maven. The modules are as follows:

* `core` Used at runtime, contains the main `Decodetect` class for encoding detection.

* `train` Used for downloading training data from Wikipedia and training new models.

##Dependencies
Runtime:

* [jutf7](http://jutf7.sourceforge.net/) for UTF-7 Charset support ([MIT] (https://opensource.org/licenses/MIT))

Training:

* [gson](https://github.com/google/gson) for parsing json to extract text from Wikipedia ([Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0))

Testing:

* [JUnit 5](https://junit.org/junit5/) ([Eclipse 2.0](https://www.eclipse.org/legal/epl-2.0/))

##License
Decodetect uses the MIT license.

If this library helps you and you feel compelled to give credit, please feel free to do so. :)
