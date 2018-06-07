# Dev Instructions

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