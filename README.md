Acuo Allocation Algo Scripts
============================ 

This maven project regroup all the R scripts used in the acuo-collateral service.

Use the following commands to build, test, package and deploy the library:


**mvn test**
> run the package tests (both the Java and R code tests)

**mvn package**
> create a JAR file of the package (named *foobar-1.0-SNAPSHOT.jar* in the example above) in the `target` folder of the package’s root directory

**mvn install**
> install the artifact (i.e. package) into the local repository

**mvn deploy**
> upload the artifact to a remote repository (requires additional configuration)

**mvn clean**
> clean the project’s working directory after a build (can also be combined with one of the previous commands, for example: `mvn clean install`) 

Please refer to [writing renjin extensions](http://docs.renjin.org/en/latest/writing-renjin-extensions.html) documentation for more information
