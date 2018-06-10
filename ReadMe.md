# Dev Instructions

## Files
* Copy the persistence-example.xml (src/main/resources/META-INF) and rename it as persistence.xml. In this file, 
change the database name (replace the DATABASE in line 9) and add your credentials in username and password. 
Finally, you need to add MySQL driver in the project's classpath and configure the project as a JPA project, 
adding the database source and the driver.
* Get the database from here: https://drive.google.com/drive/folders/1DVPmna1RsAuAvNxrMMYKYZqHDbsKoSvD?usp=sharing
* Get the jar files for JInsect and OpenJGraph from here: https://drive.google.com/open?id=1fEwP078qB0SlHIjGuFmEHBuk4sIZQr_m
* Database data in csv format from here: https://drive.google.com/open?id=1TS6V_YqzrAExYamOdD4m6DI-TXUEuD5h
* If you want to re-create the features and instances, copy below folders in src/main/resources :
    * BOW features: https://drive.google.com/open?id=1cTZvXqqfB0n2mhcv3P8F19300nCxW0Wh
    * ngrams: https://drive.google.com/open?id=1l42mUYEPssEPwF9IQUVmp7Iw-eyR7Ex5
    * Spelling: https://drive.google.com/open?id=1bd3CIIft8CoE_MQ2wLgEpDcLeysrsJPm
    * Preprocessing: https://drive.google.com/open?id=1csaBomaePGufhLfAJZCDrS7S1eYu4_BP
    * word2vec: https://drive.google.com/open?id=1DKPRjQFIPWJIQLOTEUAg-qwTPK2dXujs
* Also in the root folder of the project create a logs folder to save the program's logs.
* In case you do not want to produce new instances, you can find the instances I have produced here: https://drive.google.com/open?id=1jOfkrer7K75H7zjJL6PoMH93pBflV6Yd

## Configurations

* twitter4j.properties: file needed in order to download tweets from the one of the two datasets used
* log4j.properties: file to configure logger
* emailConfig-example.properties: rename this file to emailConfig.properties and define your own properties to get notified when execution of the program is finished
* config.properties: 
	* parallel: run folds in parallel
	* numFolds: configuation used in cross validation to define the folds number
	* runs: used only for cross validation classificationType. Defines how many times cross validation will be executed
	* dataset: select -1 to include all texts and run the program as single label supervised learning, otherwise choose only one of the two datasets (put 0 or 1) to select only one dataset and run the program as multi label supervised learning
	* instances: you can either choose "new" to generate new instances or "existing" to use already extracted instances, which will be accessed from arff file
	* pathToInstances: since we have created instances for the merged dataset and for each dataset separately, define from which folder the program will retrieve the instances, e.g. "./instances/singlelabel/". You need to define only this part of the path, since the remaining is the same in all instances folders. The path is associated with the previous field.
	* datasource: you can choose either to access data (texts, features and texts_features) from the database or from csv files
	* vectorFeatures: same here, you can write "new" to re-generate vector features or use "existing" to access them from the database or the csv. In both cases you should first select new in instances (above) field
	* graphFeatures: use true/false in order to generate or not graphFeatures (true is meaningless if you have not chosen new instances)
	* graphType: define if it is ngram or word graph (select true for graphFeatures first)
	* featuresKind: it is related to vector features. One can select "all" or a specific kind (e.g. bow, ngrams etc)
	* instancesToFile: in case you have selected to generate new instances, select true or false to define whether the instances will be exported to file or not
	* Below vector features configurations are used only in case you have selected the "new" option in vectorFeatures field:
		* preprocess: select true or false to define if you want to preprocess your texts
		* stopwords: in case you have selected to preprocess the texts, define if you want to also remove stopwords
		* bow: generate or not bow features
		* word2vec: generate or not word2vec features
		* aggregationType: define the aggregation type for word2vec features (this means that you have selected true in the above field)
		* charngram: generate or not charngram features
		* ngram: generate or not ngram features
		* spelling: generate or not spelling features
		* syntax: generate or not syntax features
	* classificationType: select either "classification" or "crossValidation"
	* Classifiers configuration: define which classifiers will run by selecting true/false in the fields NaiveBayes, LogisticRegression and KNN