Acuo Allocation Algo Scripts
============================ 

This maven project regroup all the R scripts used in the acuo-collateral service.

Use the following commands to build, test, package and deploy the library:


**mvn test**
> run the package tests (both the Java and R code tests).

**mvn package**
> create a JAR file of the package (named *foobar-1.0-SNAPSHOT.jar* in the example above) in the `target` folder of the package’s root directory.

**mvn install**
> install the artifact (i.e. package) into the local repository.

**mvn clean**
> clean the project’s working directory after a build (can also be combined with one of the previous commands, for example: `mvn clean install`).

**mvn release:prepare**
> prepare the release by creating a tag out of the version, increase the version of the project, commit and push the changes.

**mvn release:perform**
> perform the release by checking out the release tag, build and deploy the library to nexus.

Please refer to [writing renjin extensions](http://docs.renjin.org/en/latest/writing-renjin-extensions.html) documentation for more information.
