# Example Mule applications using BPM Connector

## Installation
```bash
mvn clean install
```

## Generating new examples
```bash
mvn archetype:generate -DarchetypeGroupId=org.mule.tools -DarchetypeArtifactId=mule-apikit-archetype -DarchetypeVersion=1.3.0 -DarchetypeRepository=http://repository.mulesoft.org/releases -DgroupId=com.alfame.esb.bpm.examples -DartifactId=new-example -Dpackage=com.alfame.com.esb.bpm.examples -Dversion=1.0.0-SNAPSHOT -B
```
