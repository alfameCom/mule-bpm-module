# Implementation of Mule BPM Module

This extension was initially created by:
```
mvn archetype:generate -DarchetypeGroupId=org.mule.extensions -DarchetypeArtifactId=mule-extensions-archetype -DgroupId=com.alfame.esb.bpm -DartifactId=mule-bpm-module -Dversion=1.0.0-SNAPSHOT -Dpackage=com.alfame.esb.bpm.module -DextensionName=BPM
```

## Components

[![
classDiagram
	class Mule BPM App {
		<<Application>>
	}
	class Mule BPM Module {
		<<Component>>
	}
	class Mule Module {
		<<SDK>>
	}
	class Mule BPM Flowable Activity {
		<<Component>>
	}
	class Flowable Engine {
		<<Component>>
	}
	class Mule BPM Task Queue {
		<<Component>>
	}
	class Mule BPM API {
		<<Component>>
	}
	Mule BPM App..>Mule BPM Module
	Mule BPM App..>Mule BPM API
	Mule BPM Module..|>Mule Module
	Mule BPM Module..>Flowable Engine
	Mule BPM Module..>Mule BPM Flowable Activity
	Mule BPM Flowable Activity..>Mule BPM Task Queue
	Mule BPM Flowable Activity..>Flowable Engine
	Mule BPM Module..>Mule BPM Task Queue
	Mule BPM Module..>Mule BPM API
	Mule BPM Task Queue..>Mule BPM API
](https://mermaid.ink/img/eyJjb2RlIjoiY2xhc3NEaWFncmFtXG5cdGNsYXNzIE11bGUgQlBNIEFwcCB7XG5cdFx0PDxBcHBsaWNhdGlvbj4-XG5cdH1cblx0Y2xhc3MgTXVsZSBCUE0gTW9kdWxlIHtcblx0XHQ8PENvbXBvbmVudD4-XG5cdH1cblx0Y2xhc3MgTXVsZSBNb2R1bGUge1xuXHRcdDw8U0RLPj5cblx0fVxuXHRjbGFzcyBNdWxlIEJQTSBGbG93YWJsZSBBY3Rpdml0eSB7XG5cdFx0PDxDb21wb25lbnQ-PlxuXHR9XG5cdGNsYXNzIEZsb3dhYmxlIEVuZ2luZSB7XG5cdFx0PDxDb21wb25lbnQ-PlxuXHR9XG5cdGNsYXNzIE11bGUgQlBNIFRhc2sgUXVldWUge1xuXHRcdDw8Q29tcG9uZW50Pj5cblx0fVxuXHRjbGFzcyBNdWxlIEJQTSBBUEkge1xuXHRcdDw8Q29tcG9uZW50Pj5cblx0fVxuXHRNdWxlIEJQTSBBcHAuLj5NdWxlIEJQTSBNb2R1bGVcblx0TXVsZSBCUE0gQXBwLi4-TXVsZSBCUE0gQVBJXG5cdE11bGUgQlBNIE1vZHVsZS4ufD5NdWxlIE1vZHVsZVxuXHRNdWxlIEJQTSBNb2R1bGUuLj5GbG93YWJsZSBFbmdpbmVcblx0TXVsZSBCUE0gTW9kdWxlLi4-TXVsZSBCUE0gRmxvd2FibGUgQWN0aXZpdHlcblx0TXVsZSBCUE0gRmxvd2FibGUgQWN0aXZpdHkuLj5NdWxlIEJQTSBUYXNrIFF1ZXVlXG5cdE11bGUgQlBNIEZsb3dhYmxlIEFjdGl2aXR5Li4-Rmxvd2FibGUgRW5naW5lXG5cdE11bGUgQlBNIE1vZHVsZS4uPk11bGUgQlBNIFRhc2sgUXVldWVcblx0TXVsZSBCUE0gTW9kdWxlLi4-TXVsZSBCUE0gQVBJXG5cdE11bGUgQlBNIFRhc2sgUXVldWUuLj5NdWxlIEJQTSBBUEkiLCJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCJ9LCJ1cGRhdGVFZGl0b3IiOmZhbHNlfQ)](https://mermaid-js.github.io/mermaid-live-editor/#/edit/eyJjb2RlIjoiY2xhc3NEaWFncmFtXG5cdGNsYXNzIE11bGUgQlBNIEFwcCB7XG5cdFx0PDxBcHBsaWNhdGlvbj4-XG5cdH1cblx0Y2xhc3MgTXVsZSBCUE0gTW9kdWxlIHtcblx0XHQ8PENvbXBvbmVudD4-XG5cdH1cblx0Y2xhc3MgTXVsZSBNb2R1bGUge1xuXHRcdDw8U0RLPj5cblx0fVxuXHRjbGFzcyBNdWxlIEJQTSBGbG93YWJsZSBBY3Rpdml0eSB7XG5cdFx0PDxDb21wb25lbnQ-PlxuXHR9XG5cdGNsYXNzIEZsb3dhYmxlIEVuZ2luZSB7XG5cdFx0PDxDb21wb25lbnQ-PlxuXHR9XG5cdGNsYXNzIE11bGUgQlBNIFRhc2sgUXVldWUge1xuXHRcdDw8Q29tcG9uZW50Pj5cblx0fVxuXHRjbGFzcyBNdWxlIEJQTSBBUEkge1xuXHRcdDw8Q29tcG9uZW50Pj5cblx0fVxuXHRNdWxlIEJQTSBBcHAuLj5NdWxlIEJQTSBNb2R1bGVcblx0TXVsZSBCUE0gQXBwLi4-TXVsZSBCUE0gQVBJXG5cdE11bGUgQlBNIE1vZHVsZS4ufD5NdWxlIE1vZHVsZVxuXHRNdWxlIEJQTSBNb2R1bGUuLj5GbG93YWJsZSBFbmdpbmVcblx0TXVsZSBCUE0gTW9kdWxlLi4-TXVsZSBCUE0gRmxvd2FibGUgQWN0aXZpdHlcblx0TXVsZSBCUE0gRmxvd2FibGUgQWN0aXZpdHkuLj5NdWxlIEJQTSBUYXNrIFF1ZXVlXG5cdE11bGUgQlBNIEZsb3dhYmxlIEFjdGl2aXR5Li4-Rmxvd2FibGUgRW5naW5lXG5cdE11bGUgQlBNIE1vZHVsZS4uPk11bGUgQlBNIFRhc2sgUXVldWVcblx0TXVsZSBCUE0gTW9kdWxlLi4-TXVsZSBCUE0gQVBJXG5cdE11bGUgQlBNIFRhc2sgUXVldWUuLj5NdWxlIEJQTSBBUEkiLCJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCJ9LCJ1cGRhdGVFZGl0b3IiOmZhbHNlfQ)

### Mule BPM App

Mule BPM Apps are Mule applications utilizing this Mule BPM Module. Examples of such applications can be found from [/examples](/examples).

### Mule Module

[Mule SDK](https://docs.mulesoft.com/mule-sdk/1.1/getting-started) defines a framework for Mule Modules.

### Mule BPM Module

This component, which realizes Mule Module and implements Mule BPM Module XML API [features](#features).

### Mule BPM API

Java API, which defines domain model and services of Mule BPM Module. All Mule XML operations implemented by Mule BPM Module, can be also invoked using this Java API.

### Mule BPM Flowable Activity

Implementation of Flowable Engine's Mule task. The same Java interface is also being used for Flowable's built-in Mule 3 implementation and is thus supported by Flowable Modeller as such.

There is also dummy version included, which doesn't actually execute any Mule tasks, but rather rejects them. This should be added to class path of Flowable UI to allow rendering of process definitions with Mule tasks.

### Mule BPM Task Queue

A Java library implementing JVM level queueing for Mule tasks.

### Flowable Engine

The core BPM engine implementation used by this Mule BPM Module. It is not currently interchangeable to other BPM engines.

## Features

### uniqueDeploymentName

When 'duplicateDeploymentFiltering' is set to true and 'uniqueDeploymentName' is provided: prevents deploying same process and form definitions if there are no changes.

Example:
```
<bpm:config name="myConfig" tenantId="com.alfame.esb" uniqueDeploymentName="unique-deployment" duplicateDeploymentFiltering="true">
```

### Process factory

`<bpm:process-factory />` can be used to instantiate new processes:

[![
sequenceDiagram
    autonumber
    participant Mule BPM App
    participant Mule BPM Module
    participant BPMN 2.0
    activate Mule BPM App
    rect rgb(200, 200, 240)
    Mule BPM App->>Mule BPM Module: <bpm:process-factory />
    activate Mule BPM Module
    Mule BPM Module->>BPMN 2.0: <startEvent />
    activate BPMN 2.0
    deactivate BPMN 2.0
    Mule BPM Module-->>Mule BPM App: com.alfame.esb.bpm.api.BPMProcessInstance
    deactivate Mule BPM Module
    end
    activate Mule BPM App
    Mule BPM App->>Mule BPM App: attributes.processInstanceId
    deactivate Mule BPM App
    deactivate Mule BPM App
](https://mermaid.ink/img/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgYXV0b251bWJlclxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIEFwcFxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIE1vZHVsZVxuICAgIHBhcnRpY2lwYW50IEJQTU4gMi4wXG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgcmVjdCByZ2IoMjAwLCAyMDAsIDI0MClcbiAgICBNdWxlIEJQTSBBcHAtPj5NdWxlIEJQTSBNb2R1bGU6IDxicG06cHJvY2Vzcy1mYWN0b3J5IC8-XG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgTXVsZSBCUE0gTW9kdWxlLT4-QlBNTiAyLjA6IDxzdGFydEV2ZW50IC8-XG4gICAgYWN0aXZhdGUgQlBNTiAyLjBcbiAgICBkZWFjdGl2YXRlIEJQTU4gMi4wXG4gICAgTXVsZSBCUE0gTW9kdWxlLS0-Pk11bGUgQlBNIEFwcDogY29tLmFsZmFtZS5lc2IuYnBtLmFwaS5CUE1Qcm9jZXNzSW5zdGFuY2VcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIGFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIE11bGUgQlBNIEFwcC0-Pk11bGUgQlBNIEFwcDogYXR0cmlidXRlcy5wcm9jZXNzSW5zdGFuY2VJZFxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBBcHAiLCJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCJ9LCJ1cGRhdGVFZGl0b3IiOmZhbHNlfQ)](https://mermaid-js.github.io/mermaid-live-editor/#/edit/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgYXV0b251bWJlclxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIEFwcFxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIE1vZHVsZVxuICAgIHBhcnRpY2lwYW50IEJQTU4gMi4wXG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgcmVjdCByZ2IoMjAwLCAyMDAsIDI0MClcbiAgICBNdWxlIEJQTSBBcHAtPj5NdWxlIEJQTSBNb2R1bGU6IDxicG06cHJvY2Vzcy1mYWN0b3J5IC8-XG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgTXVsZSBCUE0gTW9kdWxlLT4-QlBNTiAyLjA6IDxzdGFydEV2ZW50IC8-XG4gICAgYWN0aXZhdGUgQlBNTiAyLjBcbiAgICBkZWFjdGl2YXRlIEJQTU4gMi4wXG4gICAgTXVsZSBCUE0gTW9kdWxlLS0-Pk11bGUgQlBNIEFwcDogY29tLmFsZmFtZS5lc2IuYnBtLmFwaS5CUE1Qcm9jZXNzSW5zdGFuY2VcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIGFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIE11bGUgQlBNIEFwcC0-Pk11bGUgQlBNIEFwcDogYXR0cmlidXRlcy5wcm9jZXNzSW5zdGFuY2VJZFxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBBcHAiLCJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCJ9LCJ1cGRhdGVFZGl0b3IiOmZhbHNlfQ)

Process factory allows adding variables to process, by using nested `<bpm:process-variables />` tag.

By setting `uniqueBusinessKey` attribute, the instance is guaranteed to be the only instance carrying the key.


### Process querying & deletion

Use `<bpm:process-instance-query-builder>` to query for a process.
Use `<bpm:delete-process-instance>` to delete a process with processInstanceId and optional deleteReason.

Example:
```
<flow name="checkForSleepFlow">
    <bpm:task-listener config-ref="engineConfig" endpointUrl="bpm://checkForSleep" transactionType="LOCAL" transactionalAction="ALWAYS_BEGIN" />

    <bpm:get-variable config-ref="engineConfig" variableName="sleeperProcessInstanceId" target="sleeperProcessInstanceId" />
    <logger level="INFO" message="Sleeper process instance id: #[vars.sleeperProcessInstanceId]" />

    <bpm:process-instance-query-builder config-ref="engineConfig" target="sleeperProcessQuery">
        <bpm:process-instance-filters>
            <bpm:process-instance-tenant-filter tenantId="com.alfame.esb" />
            <bpm:process-instance-id-filter processInstanceId="#[vars.sleeperProcessInstanceId]" />
            <bpm:process-instance-name-like-filter nameLike="sleep%" />
            <bpm:process-instance-variable-like-filter variableName="sleeperName"  valueLike="patient%" />
            <bpm:process-instance-variable-like-filter variableName="targetAmount" />
            <bpm:process-instance-started-after-filter startedAfter="2000-01-01" />
            <bpm:process-instance-started-before-filter startedBefore="2050-01-01" />
            <bpm:process-instance-only-unfinished-filter />
            <bpm:process-instance-include-process-variables />
        </bpm:process-instance-filters>
    </bpm:process-instance-query-builder>
    <bpm:get-unique-process-instance config-ref="engineConfig" query="#[vars.sleeperProcessQuery]" target="sleeperProcess" />

    <choice>
        <when expression="#[vars.sleeperProcess != null and vars.sleeperProcess.processInstanceId == vars.sleeperProcessInstanceId]">
            <bpm:delete-process-instance config-ref="engineConfig" processInstanceId="#[vars.sleeperProcess.processInstanceId]" deleteReason="Deleted unneeded process" />
            <logger level="INFO" message="Unfinished sleeper found and deleted: #[vars.sleeperProcess.processInstanceId]" />
        </when>
        <otherwise>
            <raise-error type="ANY" description="Sleeper not found"/>
        </otherwise>
    </choice>
</flow>
```

### Mule task listener

Mule tasks are Mule flows using `<bpm:task-listener />` as a source. Process instances can be set to call such flows by adding `<serviceTask flowable:type="mule" />`, with matching Endpoint URL.

Execution of Mule task can be either synchronous or asynchronous: synchronous Mule tasks are executed on calling thread, while asynchronous Mule tasks are always executed on background.

[![
sequenceDiagram
    autonumber
    participant Mule BPM App
    participant Mule BPM Module
    participant BPMN 2.0
    activate Mule BPM App
    Mule BPM App->>Mule BPM Module: <bpm:process-factory />
    activate Mule BPM Module
    Mule BPM Module->>BPMN 2.0: <startEvent />
    activate BPMN 2.0
    rect rgb(200, 200, 240)
    Mule BPM Module->>BPMN 2.0: <serviceTask flowable:type="mule" flowable:async="false" />
    BPMN 2.0->>Mule BPM Module: org.flowable.mule.MuleSendActivityBehavior
    activate Mule BPM Module
    Mule BPM Module->>Mule BPM App: <bpm:task-listener />
    activate Mule BPM App
    Mule BPM App-->>Mule BPM Module: resultVariable
    deactivate Mule BPM App
    Mule BPM Module-->>BPMN 2.0: resultVariable
    BPMN 2.0-->>Mule BPM Module: done
    deactivate Mule BPM Module
    deactivate BPMN 2.0
    end
    Mule BPM Module-->>Mule BPM App: com.alfame.esb.bpm.api.BPMProcessInstance
    deactivate Mule BPM Module
    activate Mule BPM App
    Mule BPM App->>Mule BPM App: attributes.processInstanceId
    deactivate Mule BPM App
    rect rgb(200, 200, 240)
    Mule BPM Module->>BPMN 2.0: <serviceTask flowable:type="mule" flowable:async="true" />
    activate Mule BPM Module
    activate BPMN 2.0
    BPMN 2.0->>Mule BPM Module: org.flowable.mule.MuleSendActivityBehavior
    activate Mule BPM Module
    Mule BPM Module->>Mule BPM App: <bpm:task-listener />
    activate Mule BPM App
    Mule BPM App-->>Mule BPM Module: resultVariable
    deactivate Mule BPM App
    Mule BPM Module-->>BPMN 2.0: resultVariable
    BPMN 2.0-->>Mule BPM Module: done
    deactivate BPMN 2.0
    deactivate Mule BPM Module
    deactivate Mule BPM Module
    end
    deactivate Mule BPM App
](https://mermaid.ink/img/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgYXV0b251bWJlclxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIEFwcFxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIE1vZHVsZVxuICAgIHBhcnRpY2lwYW50IEJQTU4gMi4wXG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gTW9kdWxlOiA8YnBtOnByb2Nlc3MtZmFjdG9yeSAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiA8c3RhcnRFdmVudCAvPlxuICAgIGFjdGl2YXRlIEJQTU4gMi4wXG4gICAgcmVjdCByZ2IoMjAwLCAyMDAsIDI0MClcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5CUE1OIDIuMDogPHNlcnZpY2VUYXNrIGZsb3dhYmxlOnR5cGU9XCJtdWxlXCIgZmxvd2FibGU6YXN5bmM9XCJmYWxzZVwiIC8-XG4gICAgQlBNTiAyLjAtPj5NdWxlIEJQTSBNb2R1bGU6IG9yZy5mbG93YWJsZS5tdWxlLk11bGVTZW5kQWN0aXZpdHlCZWhhdmlvclxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-Pk11bGUgQlBNIEFwcDogPGJwbTp0YXNrLWxpc3RlbmVyIC8-XG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgTXVsZSBCUE0gQXBwLS0-Pk11bGUgQlBNIE1vZHVsZTogcmVzdWx0VmFyaWFibGVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5CUE1OIDIuMDogcmVzdWx0VmFyaWFibGVcbiAgICBCUE1OIDIuMC0tPj5NdWxlIEJQTSBNb2R1bGU6IGRvbmVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGRlYWN0aXZhdGUgQlBNTiAyLjBcbiAgICBlbmRcbiAgICBNdWxlIEJQTSBNb2R1bGUtLT4-TXVsZSBCUE0gQXBwOiBjb20uYWxmYW1lLmVzYi5icG0uYXBpLkJQTVByb2Nlc3NJbnN0YW5jZVxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gQXBwOiBhdHRyaWJ1dGVzLnByb2Nlc3NJbnN0YW5jZUlkXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBBcHBcbiAgICByZWN0IHJnYigyMDAsIDIwMCwgMjQwKVxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiA8c2VydmljZVRhc2sgZmxvd2FibGU6dHlwZT1cIm11bGVcIiBmbG93YWJsZTphc3luYz1cInRydWVcIiAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGFjdGl2YXRlIEJQTU4gMi4wXG4gICAgQlBNTiAyLjAtPj5NdWxlIEJQTSBNb2R1bGU6IG9yZy5mbG93YWJsZS5tdWxlLk11bGVTZW5kQWN0aXZpdHlCZWhhdmlvclxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-Pk11bGUgQlBNIEFwcDogPGJwbTp0YXNrLWxpc3RlbmVyIC8-XG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgTXVsZSBCUE0gQXBwLS0-Pk11bGUgQlBNIE1vZHVsZTogcmVzdWx0VmFyaWFibGVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5CUE1OIDIuMDogcmVzdWx0VmFyaWFibGVcbiAgICBCUE1OIDIuMC0tPj5NdWxlIEJQTSBNb2R1bGU6IGRvbmVcbiAgICBkZWFjdGl2YXRlIEJQTU4gMi4wXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gQXBwIiwibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQifSwidXBkYXRlRWRpdG9yIjpmYWxzZX0)](https://mermaid-js.github.io/mermaid-live-editor/#/edit/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgYXV0b251bWJlclxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIEFwcFxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIE1vZHVsZVxuICAgIHBhcnRpY2lwYW50IEJQTU4gMi4wXG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gTW9kdWxlOiA8YnBtOnByb2Nlc3MtZmFjdG9yeSAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiA8c3RhcnRFdmVudCAvPlxuICAgIGFjdGl2YXRlIEJQTU4gMi4wXG4gICAgcmVjdCByZ2IoMjAwLCAyMDAsIDI0MClcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5CUE1OIDIuMDogPHNlcnZpY2VUYXNrIGZsb3dhYmxlOnR5cGU9XCJtdWxlXCIgZmxvd2FibGU6YXN5bmM9XCJmYWxzZVwiIC8-XG4gICAgQlBNTiAyLjAtPj5NdWxlIEJQTSBNb2R1bGU6IG9yZy5mbG93YWJsZS5tdWxlLk11bGVTZW5kQWN0aXZpdHlCZWhhdmlvclxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-Pk11bGUgQlBNIEFwcDogPGJwbTp0YXNrLWxpc3RlbmVyIC8-XG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgTXVsZSBCUE0gQXBwLS0-Pk11bGUgQlBNIE1vZHVsZTogcmVzdWx0VmFyaWFibGVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5CUE1OIDIuMDogcmVzdWx0VmFyaWFibGVcbiAgICBCUE1OIDIuMC0tPj5NdWxlIEJQTSBNb2R1bGU6IGRvbmVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGRlYWN0aXZhdGUgQlBNTiAyLjBcbiAgICBlbmRcbiAgICBNdWxlIEJQTSBNb2R1bGUtLT4-TXVsZSBCUE0gQXBwOiBjb20uYWxmYW1lLmVzYi5icG0uYXBpLkJQTVByb2Nlc3NJbnN0YW5jZVxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gQXBwOiBhdHRyaWJ1dGVzLnByb2Nlc3NJbnN0YW5jZUlkXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBBcHBcbiAgICByZWN0IHJnYigyMDAsIDIwMCwgMjQwKVxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiA8c2VydmljZVRhc2sgZmxvd2FibGU6dHlwZT1cIm11bGVcIiBmbG93YWJsZTphc3luYz1cInRydWVcIiAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGFjdGl2YXRlIEJQTU4gMi4wXG4gICAgQlBNTiAyLjAtPj5NdWxlIEJQTSBNb2R1bGU6IG9yZy5mbG93YWJsZS5tdWxlLk11bGVTZW5kQWN0aXZpdHlCZWhhdmlvclxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-Pk11bGUgQlBNIEFwcDogPGJwbTp0YXNrLWxpc3RlbmVyIC8-XG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgTXVsZSBCUE0gQXBwLS0-Pk11bGUgQlBNIE1vZHVsZTogcmVzdWx0VmFyaWFibGVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5CUE1OIDIuMDogcmVzdWx0VmFyaWFibGVcbiAgICBCUE1OIDIuMC0tPj5NdWxlIEJQTSBNb2R1bGU6IGRvbmVcbiAgICBkZWFjdGl2YXRlIEJQTU4gMi4wXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gQXBwIiwibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQifSwidXBkYXRlRWRpdG9yIjpmYWxzZX0)

### Mule event listener

Flowable events can be used as flow sources with `<bpm:event-listener />`. Event filters can be used to filter the events that trigger flows.

Example:

```
<flow name="myFlow">
    <bpm:event-listener config-ref="myConfigRef">
        <bpm:event-filters>
            <bpm:activity-name-filter activityName="foobar"/>
        </bpm:event-filters>
    </bpm:event-listener>
</flow>
```

See [Event filters](#event-filters) for examples. 



[![
    sequenceDiagram
    autonumber
    participant Mule BPM App
    participant Mule BPM Module
    participant BPMN 2.0
    activate Mule BPM App
    Mule BPM App->>Mule BPM Module: <bpm:process-factory />  
    activate Mule BPM Module
    Mule BPM Module->>BPMN 2.0: <startEvent />
    activate BPMN 2.0
    rect rgb(200, 200, 240)
    BPMN 2.0->>Mule BPM Module: org.flowable.common.engine.api.delegate.event.FlowableEvent
    alt isUnfilteredEvent(FlowableEvent)
        Mule BPM Module->>Mule BPM App: <bpm:event-listener />
        Mule BPM App->>Mule BPM App: execute flow
    else
    end
    deactivate Mule BPM Module
    deactivate BPMN 2.0
    deactivate Mule BPM App
    end
](https://mermaid.ink/img/pako:eNp9U01rwzAM_SvGpw5aN5SdwghsrLt1DMZuuTi2khn8NcfuVkr_-5SPlrTrmkOQpaenJ1neU-Ek0Jy28JXACnhWvAnclJbgx1N0NpkKwnD2PEQllOc2kk3SQJ7eNuTR-xvRjZNo_gVg7JWsWDYWElFteYQrrFPPoiguiHPyUHmT--AEtO2iRiIXdmRZEPIf81TRhRP5j7qQuI2od70FVLssLtjO5QcQkYSmmq2ybE6G3312NwSP0GviXWhYrd03rzQw4YxxloFtlAXGvWISNDRYjkGngr2MyF7TKEhHotoPWysdIYDsQ7Mz4CjjervT6Y6z7GsttGojWAin1m_dRZ8MPyASjqbrZ0gB3Y5zBisHQ8LtC5nEz0d8LfG0I0hP59RAMFxJXOZ95y5p_AQDJc3RlFDzpGNJS3tAaPISmdZS4bbQPIYEc9ot-_vOiuN5wIzvYXAefgEokQWv)](https://mermaid-js.github.io/mermaid-live-editor/edit#pako:eNp9U01rwzAM_SvGpw5aN5SdwghsrLt1DMZuuTi2khn8NcfuVkr_-5SPlrTrmkOQpaenJ1neU-Ek0Jy28JXACnhWvAnclJbgx1N0NpkKwnD2PEQllOc2kk3SQJ7eNuTR-xvRjZNo_gVg7JWsWDYWElFteYQrrFPPoiguiHPyUHmT--AEtO2iRiIXdmRZEPIf81TRhRP5j7qQuI2od70FVLssLtjO5QcQkYSmmq2ybE6G3312NwSP0GviXWhYrd03rzQw4YxxloFtlAXGvWISNDRYjkGngr2MyF7TKEhHotoPWysdIYDsQ7Mz4CjjervT6Y6z7GsttGojWAin1m_dRZ8MPyASjqbrZ0gB3Y5zBisHQ8LtC5nEz0d8LfG0I0hP59RAMFxJXOZ95y5p_AQDJc3RlFDzpGNJS3tAaPISmdZS4bbQPIYEc9ot-_vOiuN5wIzvYXAefgEokQWv)

### Variable operations

Variable operations `<bpm:get-variable />`, `<bpm:set-variable />` and `<bpm:remove-variable />` are available inside Mule tasks:

[![
sequenceDiagram
    autonumber
    participant Mule BPM App
    participant Mule BPM Module
    participant BPMN 2.0
    activate Mule BPM App
    rect rgb(200, 200, 240)
    Mule BPM Module->>BPMN 2.0: <serviceTask flowable:type="mule" flowable:async="true" />
    activate Mule BPM Module
    activate BPMN 2.0
    BPMN 2.0->>Mule BPM Module: org.flowable.mule.MuleSendActivityBehavior
    activate Mule BPM Module
    Mule BPM Module->>Mule BPM App: <bpm:task-listener />
    activate Mule BPM App
    rect rgb(180, 180, 230)
    Mule BPM App->>Mule BPM Module: <bpm:get-variable />
    activate Mule BPM Module
    Mule BPM Module-->>Mule BPM App: content
    deactivate Mule BPM Module
    end
    rect rgb(180, 180, 230)
    Mule BPM App->>Mule BPM Module: <bpm:set-variable />
    activate Mule BPM Module
    Mule BPM Module-->>Mule BPM App: done
    deactivate Mule BPM Module
    end
    rect rgb(180, 180, 230)
    Mule BPM App->>Mule BPM Module: <bpm:remove-variable />
    activate Mule BPM Module
    Mule BPM Module-->>Mule BPM App: done
    deactivate Mule BPM Module
    end
    Mule BPM App-->>Mule BPM Module: resultVariable
    deactivate Mule BPM App
    Mule BPM Module-->>BPMN 2.0: set variable
    Mule BPM Module-->>BPMN 2.0: remove variable
    Mule BPM Module-->>BPMN 2.0: resultVariable
    BPMN 2.0-->>Mule BPM Module: done
    deactivate BPMN 2.0
    deactivate Mule BPM Module
    deactivate Mule BPM Module
    end
    deactivate Mule BPM App
](https://mermaid.ink/img/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgYXV0b251bWJlclxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIEFwcFxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIE1vZHVsZVxuICAgIHBhcnRpY2lwYW50IEJQTU4gMi4wXG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgcmVjdCByZ2IoMjAwLCAyMDAsIDI0MClcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5CUE1OIDIuMDogPHNlcnZpY2VUYXNrIGZsb3dhYmxlOnR5cGU9XCJtdWxlXCIgZmxvd2FibGU6YXN5bmM9XCJ0cnVlXCIgLz5cbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIEJQTU4gMi4wLT4-TXVsZSBCUE0gTW9kdWxlOiBvcmcuZmxvd2FibGUubXVsZS5NdWxlU2VuZEFjdGl2aXR5QmVoYXZpb3JcbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5NdWxlIEJQTSBBcHA6IDxicG06dGFzay1saXN0ZW5lciAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIHJlY3QgcmdiKDE4MCwgMTgwLCAyMzApXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gTW9kdWxlOiA8YnBtOmdldC12YXJpYWJsZSAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGNvbnRlbnRcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIHJlY3QgcmdiKDE4MCwgMTgwLCAyMzApXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gTW9kdWxlOiA8YnBtOnNldC12YXJpYWJsZSAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGRvbmVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIHJlY3QgcmdiKDE4MCwgMTgwLCAyMzApXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gTW9kdWxlOiA8YnBtOnJlbW92ZS12YXJpYWJsZSAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGRvbmVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIE11bGUgQlBNIEFwcC0tPj5NdWxlIEJQTSBNb2R1bGU6IHJlc3VsdFZhcmlhYmxlXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBBcHBcbiAgICBNdWxlIEJQTSBNb2R1bGUtLT4-QlBNTiAyLjA6IHNldCB2YXJpYWJsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5CUE1OIDIuMDogcmVtb3ZlIHZhcmlhYmxlXG4gICAgTXVsZSBCUE0gTW9kdWxlLS0-PkJQTU4gMi4wOiByZXN1bHRWYXJpYWJsZVxuICAgIEJQTU4gMi4wLS0-Pk11bGUgQlBNIE1vZHVsZTogZG9uZVxuICAgIGRlYWN0aXZhdGUgQlBNTiAyLjBcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgZW5kXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBBcHAiLCJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCJ9LCJ1cGRhdGVFZGl0b3IiOmZhbHNlfQ)](https://mermaid-js.github.io/mermaid-live-editor/#/edit/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgYXV0b251bWJlclxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIEFwcFxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIE1vZHVsZVxuICAgIHBhcnRpY2lwYW50IEJQTU4gMi4wXG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgcmVjdCByZ2IoMjAwLCAyMDAsIDI0MClcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5CUE1OIDIuMDogPHNlcnZpY2VUYXNrIGZsb3dhYmxlOnR5cGU9XCJtdWxlXCIgZmxvd2FibGU6YXN5bmM9XCJ0cnVlXCIgLz5cbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIEJQTU4gMi4wLT4-TXVsZSBCUE0gTW9kdWxlOiBvcmcuZmxvd2FibGUubXVsZS5NdWxlU2VuZEFjdGl2aXR5QmVoYXZpb3JcbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5NdWxlIEJQTSBBcHA6IDxicG06dGFzay1saXN0ZW5lciAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIHJlY3QgcmdiKDE4MCwgMTgwLCAyMzApXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gTW9kdWxlOiA8YnBtOmdldC12YXJpYWJsZSAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGNvbnRlbnRcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIHJlY3QgcmdiKDE4MCwgMTgwLCAyMzApXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gTW9kdWxlOiA8YnBtOnNldC12YXJpYWJsZSAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGRvbmVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIHJlY3QgcmdiKDE4MCwgMTgwLCAyMzApXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gTW9kdWxlOiA8YnBtOnJlbW92ZS12YXJpYWJsZSAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGRvbmVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIE11bGUgQlBNIEFwcC0tPj5NdWxlIEJQTSBNb2R1bGU6IHJlc3VsdFZhcmlhYmxlXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBBcHBcbiAgICBNdWxlIEJQTSBNb2R1bGUtLT4-QlBNTiAyLjA6IHNldCB2YXJpYWJsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5CUE1OIDIuMDogcmVtb3ZlIHZhcmlhYmxlXG4gICAgTXVsZSBCUE0gTW9kdWxlLS0-PkJQTU4gMi4wOiByZXN1bHRWYXJpYWJsZVxuICAgIEJQTU4gMi4wLS0-Pk11bGUgQlBNIE1vZHVsZTogZG9uZVxuICAgIGRlYWN0aXZhdGUgQlBNTiAyLjBcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgZW5kXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBBcHAiLCJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCJ9LCJ1cGRhdGVFZGl0b3IiOmZhbHNlfQ)

Mule task flows using variable operations must start a local transaction to allow operations to join to Mule task transaction: `<bpm:task-listener transactionType="LOCAL" transactionalAction="ALWAYS_BEGIN" />`.

### Attachments

Attachments are larger binary type content for processes.

Applications can currently `<bpm:create-attachment />` and `<bpm:get-latest-attachment />` of matching type and name:

[![
sequenceDiagram
    autonumber
    participant Mule BPM App
    participant Mule BPM Module
    participant BPMN 2.0
    activate Mule BPM App
    rect rgb(200, 200, 240)
    Mule BPM Module->>BPMN 2.0: <serviceTask flowable:type="mule" flowable:async="true" />
    activate Mule BPM Module
    activate BPMN 2.0
    BPMN 2.0->>Mule BPM Module: org.flowable.mule.MuleSendActivityBehavior
    activate Mule BPM Module
    Mule BPM Module->>Mule BPM App: <bpm:task-listener />
    activate Mule BPM App
    rect rgb(180, 180, 230)
    Mule BPM App->>Mule BPM Module: <bpm:get-latest-attachment />
    activate Mule BPM Module
    Mule BPM Module->>BPMN 2.0: get attachment
    activate BPMN 2.0
    BPMN 2.0-->>Mule BPM Module: content
    deactivate BPMN 2.0
    Mule BPM Module-->>Mule BPM App: content
    deactivate Mule BPM Module
    end
    Mule BPM App->>Mule BPM App: modify content
    rect rgb(180, 180, 230)
    Mule BPM App->>Mule BPM Module: <bpm:create-attachment />
    activate Mule BPM Module
    Mule BPM Module->>BPMN 2.0: create attachment
    activate BPMN 2.0
    BPMN 2.0-->>Mule BPM Module: done
    deactivate BPMN 2.0
    Mule BPM Module-->>Mule BPM App: done
    deactivate Mule BPM Module
    end
    Mule BPM App-->>Mule BPM Module: resultVariable
    deactivate Mule BPM App
    Mule BPM Module-->>BPMN 2.0: resultVariable
    BPMN 2.0-->>Mule BPM Module: done
    deactivate BPMN 2.0
    deactivate Mule BPM Module
    deactivate Mule BPM Module
    end
    deactivate Mule BPM App
](https://mermaid.ink/img/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgYXV0b251bWJlclxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIEFwcFxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIE1vZHVsZVxuICAgIHBhcnRpY2lwYW50IEJQTU4gMi4wXG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgcmVjdCByZ2IoMjAwLCAyMDAsIDI0MClcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5CUE1OIDIuMDogPHNlcnZpY2VUYXNrIGZsb3dhYmxlOnR5cGU9XCJtdWxlXCIgZmxvd2FibGU6YXN5bmM9XCJ0cnVlXCIgLz5cbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIEJQTU4gMi4wLT4-TXVsZSBCUE0gTW9kdWxlOiBvcmcuZmxvd2FibGUubXVsZS5NdWxlU2VuZEFjdGl2aXR5QmVoYXZpb3JcbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5NdWxlIEJQTSBBcHA6IDxicG06dGFzay1saXN0ZW5lciAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIHJlY3QgcmdiKDE4MCwgMTgwLCAyMzApXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gTW9kdWxlOiA8YnBtOmdldC1sYXRlc3QtYXR0YWNobWVudCAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiBnZXQgYXR0YWNobWVudFxuICAgIGFjdGl2YXRlIEJQTU4gMi4wXG4gICAgQlBNTiAyLjAtLT4-TXVsZSBCUE0gTW9kdWxlOiBjb250ZW50XG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGNvbnRlbnRcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIE11bGUgQlBNIEFwcC0-Pk11bGUgQlBNIEFwcDogbW9kaWZ5IGNvbnRlbnRcbiAgICByZWN0IHJnYigxODAsIDE4MCwgMjMwKVxuICAgIE11bGUgQlBNIEFwcC0-Pk11bGUgQlBNIE1vZHVsZTogPGJwbTpjcmVhdGUtYXR0YWNobWVudCAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiBjcmVhdGUgYXR0YWNobWVudFxuICAgIGFjdGl2YXRlIEJQTU4gMi4wXG4gICAgQlBNTiAyLjAtLT4-TXVsZSBCUE0gTW9kdWxlOiBkb25lXG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGRvbmVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIE11bGUgQlBNIEFwcC0tPj5NdWxlIEJQTSBNb2R1bGU6IHJlc3VsdFZhcmlhYmxlXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBBcHBcbiAgICBNdWxlIEJQTSBNb2R1bGUtLT4-QlBNTiAyLjA6IHJlc3VsdFZhcmlhYmxlXG4gICAgQlBNTiAyLjAtLT4-TXVsZSBCUE0gTW9kdWxlOiBkb25lXG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBlbmRcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIEFwcCIsIm1lcm1haWQiOnsidGhlbWUiOiJkZWZhdWx0In0sInVwZGF0ZUVkaXRvciI6ZmFsc2V9)](https://mermaid-js.github.io/mermaid-live-editor/#/edit/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgYXV0b251bWJlclxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIEFwcFxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIE1vZHVsZVxuICAgIHBhcnRpY2lwYW50IEJQTU4gMi4wXG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgcmVjdCByZ2IoMjAwLCAyMDAsIDI0MClcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5CUE1OIDIuMDogPHNlcnZpY2VUYXNrIGZsb3dhYmxlOnR5cGU9XCJtdWxlXCIgZmxvd2FibGU6YXN5bmM9XCJ0cnVlXCIgLz5cbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIEJQTU4gMi4wLT4-TXVsZSBCUE0gTW9kdWxlOiBvcmcuZmxvd2FibGUubXVsZS5NdWxlU2VuZEFjdGl2aXR5QmVoYXZpb3JcbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5NdWxlIEJQTSBBcHA6IDxicG06dGFzay1saXN0ZW5lciAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIHJlY3QgcmdiKDE4MCwgMTgwLCAyMzApXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gTW9kdWxlOiA8YnBtOmdldC1sYXRlc3QtYXR0YWNobWVudCAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiBnZXQgYXR0YWNobWVudFxuICAgIGFjdGl2YXRlIEJQTU4gMi4wXG4gICAgQlBNTiAyLjAtLT4-TXVsZSBCUE0gTW9kdWxlOiBjb250ZW50XG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGNvbnRlbnRcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIE11bGUgQlBNIEFwcC0-Pk11bGUgQlBNIEFwcDogbW9kaWZ5IGNvbnRlbnRcbiAgICByZWN0IHJnYigxODAsIDE4MCwgMjMwKVxuICAgIE11bGUgQlBNIEFwcC0-Pk11bGUgQlBNIE1vZHVsZTogPGJwbTpjcmVhdGUtYXR0YWNobWVudCAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiBjcmVhdGUgYXR0YWNobWVudFxuICAgIGFjdGl2YXRlIEJQTU4gMi4wXG4gICAgQlBNTiAyLjAtLT4-TXVsZSBCUE0gTW9kdWxlOiBkb25lXG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGRvbmVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIE11bGUgQlBNIEFwcC0tPj5NdWxlIEJQTSBNb2R1bGU6IHJlc3VsdFZhcmlhYmxlXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBBcHBcbiAgICBNdWxlIEJQTSBNb2R1bGUtLT4-QlBNTiAyLjA6IHJlc3VsdFZhcmlhYmxlXG4gICAgQlBNTiAyLjAtLT4-TXVsZSBCUE0gTW9kdWxlOiBkb25lXG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBlbmRcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIEFwcCIsIm1lcm1haWQiOnsidGhlbWUiOiJkZWZhdWx0In0sInVwZGF0ZUVkaXRvciI6ZmFsc2V9)

Attachment operations are not limited to Mule task context, but [Signalling](#signalling) should be utilized to synchronize execution.

### Signalling

BPMN 2.0 processes allow event-based actions, which can be triggered by `<bpm:trigger-signal />` to synchronize threads, e.g. after creating an attachment:

[![
sequenceDiagram
    autonumber
    participant Mule BPM App
    participant Mule BPM Module
    participant BPMN 2.0
    rect rgb(200, 200, 240)
    Mule BPM App->>Mule BPM Module: <bpm:create-attachment />
    activate Mule BPM App
    activate Mule BPM Module
    Mule BPM Module->>BPMN 2.0: create attachment
    activate BPMN 2.0
    BPMN 2.0-->>Mule BPM Module: done
    deactivate BPMN 2.0
    end
    rect rgb(180, 180, 230)
    Mule BPM App->>Mule BPM Module: <bpm:trigger-signal />
    activate Mule BPM Module
    Mule BPM Module->>BPMN 2.0: trigger signal
    activate BPMN 2.0
    BPMN 2.0-->>Mule BPM Module: done
    deactivate BPMN 2.0
    end
    deactivate Mule BPM App
    BPMN 2.0->>BPMN 2.0: Catch intermediate event
    rect rgb(180, 180, 230)
    Mule BPM Module->>BPMN 2.0: <serviceTask flowable:type="mule" flowable:async="true" />
    activate Mule BPM Module
    activate BPMN 2.0
    BPMN 2.0->>Mule BPM Module: org.flowable.mule.MuleSendActivityBehavior
    activate Mule BPM Module
    Mule BPM Module->>Mule BPM App: <bpm:task-listener />
    activate Mule BPM App
    rect rgb(200, 200, 240)
    Mule BPM App->>Mule BPM Module: <bpm:get-latest-attachment />
    activate Mule BPM Module
    Mule BPM Module->>BPMN 2.0: get attachment
    activate BPMN 2.0
    BPMN 2.0-->>Mule BPM Module: content
    deactivate BPMN 2.0
    Mule BPM Module-->>Mule BPM App: content
    deactivate Mule BPM Module
    end
    Mule BPM App-->>Mule BPM Module: resultVariable
    deactivate Mule BPM App
    Mule BPM Module-->>BPMN 2.0: resultVariable
    BPMN 2.0-->>Mule BPM Module: done
    deactivate BPMN 2.0
    deactivate Mule BPM Module
    deactivate Mule BPM Module
    end
](https://mermaid.ink/img/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgYXV0b251bWJlclxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIEFwcFxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIE1vZHVsZVxuICAgIHBhcnRpY2lwYW50IEJQTU4gMi4wXG4gICAgcmVjdCByZ2IoMjAwLCAyMDAsIDI0MClcbiAgICBNdWxlIEJQTSBBcHAtPj5NdWxlIEJQTSBNb2R1bGU6IDxicG06Y3JlYXRlLWF0dGFjaG1lbnQgLz5cbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBBcHBcbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5CUE1OIDIuMDogY3JlYXRlIGF0dGFjaG1lbnRcbiAgICBhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIEJQTU4gMi4wLS0-Pk11bGUgQlBNIE1vZHVsZTogZG9uZVxuICAgIGRlYWN0aXZhdGUgQlBNTiAyLjBcbiAgICBlbmRcbiAgICByZWN0IHJnYigxODAsIDE4MCwgMjMwKVxuICAgIE11bGUgQlBNIEFwcC0-Pk11bGUgQlBNIE1vZHVsZTogPGJwbTp0cmlnZ2VyLXNpZ25hbCAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiB0cmlnZ2VyIHNpZ25hbFxuICAgIGFjdGl2YXRlIEJQTU4gMi4wXG4gICAgQlBNTiAyLjAtLT4-TXVsZSBCUE0gTW9kdWxlOiBkb25lXG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIGVuZFxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgQlBNTiAyLjAtPj5CUE1OIDIuMDogQ2F0Y2ggaW50ZXJtZWRpYXRlIGV2ZW50XG4gICAgcmVjdCByZ2IoMTgwLCAxODAsIDIzMClcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5CUE1OIDIuMDogPHNlcnZpY2VUYXNrIGZsb3dhYmxlOnR5cGU9XCJtdWxlXCIgZmxvd2FibGU6YXN5bmM9XCJ0cnVlXCIgLz5cbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIEJQTU4gMi4wLT4-TXVsZSBCUE0gTW9kdWxlOiBvcmcuZmxvd2FibGUubXVsZS5NdWxlU2VuZEFjdGl2aXR5QmVoYXZpb3JcbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5NdWxlIEJQTSBBcHA6IDxicG06dGFzay1saXN0ZW5lciAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIHJlY3QgcmdiKDIwMCwgMjAwLCAyNDApXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gTW9kdWxlOiA8YnBtOmdldC1sYXRlc3QtYXR0YWNobWVudCAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiBnZXQgYXR0YWNobWVudFxuICAgIGFjdGl2YXRlIEJQTU4gMi4wXG4gICAgQlBNTiAyLjAtLT4-TXVsZSBCUE0gTW9kdWxlOiBjb250ZW50XG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGNvbnRlbnRcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIE11bGUgQlBNIEFwcC0tPj5NdWxlIEJQTSBNb2R1bGU6IHJlc3VsdFZhcmlhYmxlXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBBcHBcbiAgICBNdWxlIEJQTSBNb2R1bGUtLT4-QlBNTiAyLjA6IHJlc3VsdFZhcmlhYmxlXG4gICAgQlBNTiAyLjAtLT4-TXVsZSBCUE0gTW9kdWxlOiBkb25lXG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBlbmQiLCJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCJ9LCJ1cGRhdGVFZGl0b3IiOmZhbHNlfQ)](https://mermaid-js.github.io/mermaid-live-editor/#/edit/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgYXV0b251bWJlclxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIEFwcFxuICAgIHBhcnRpY2lwYW50IE11bGUgQlBNIE1vZHVsZVxuICAgIHBhcnRpY2lwYW50IEJQTU4gMi4wXG4gICAgcmVjdCByZ2IoMjAwLCAyMDAsIDI0MClcbiAgICBNdWxlIEJQTSBBcHAtPj5NdWxlIEJQTSBNb2R1bGU6IDxicG06Y3JlYXRlLWF0dGFjaG1lbnQgLz5cbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBBcHBcbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5CUE1OIDIuMDogY3JlYXRlIGF0dGFjaG1lbnRcbiAgICBhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIEJQTU4gMi4wLS0-Pk11bGUgQlBNIE1vZHVsZTogZG9uZVxuICAgIGRlYWN0aXZhdGUgQlBNTiAyLjBcbiAgICBlbmRcbiAgICByZWN0IHJnYigxODAsIDE4MCwgMjMwKVxuICAgIE11bGUgQlBNIEFwcC0-Pk11bGUgQlBNIE1vZHVsZTogPGJwbTp0cmlnZ2VyLXNpZ25hbCAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiB0cmlnZ2VyIHNpZ25hbFxuICAgIGFjdGl2YXRlIEJQTU4gMi4wXG4gICAgQlBNTiAyLjAtLT4-TXVsZSBCUE0gTW9kdWxlOiBkb25lXG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIGVuZFxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgQlBNTiAyLjAtPj5CUE1OIDIuMDogQ2F0Y2ggaW50ZXJtZWRpYXRlIGV2ZW50XG4gICAgcmVjdCByZ2IoMTgwLCAxODAsIDIzMClcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5CUE1OIDIuMDogPHNlcnZpY2VUYXNrIGZsb3dhYmxlOnR5cGU9XCJtdWxlXCIgZmxvd2FibGU6YXN5bmM9XCJ0cnVlXCIgLz5cbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIEJQTU4gMi4wLT4-TXVsZSBCUE0gTW9kdWxlOiBvcmcuZmxvd2FibGUubXVsZS5NdWxlU2VuZEFjdGl2aXR5QmVoYXZpb3JcbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5NdWxlIEJQTSBBcHA6IDxicG06dGFzay1saXN0ZW5lciAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIHJlY3QgcmdiKDIwMCwgMjAwLCAyNDApXG4gICAgTXVsZSBCUE0gQXBwLT4-TXVsZSBCUE0gTW9kdWxlOiA8YnBtOmdldC1sYXRlc3QtYXR0YWNobWVudCAvPlxuICAgIGFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiBnZXQgYXR0YWNobWVudFxuICAgIGFjdGl2YXRlIEJQTU4gMi4wXG4gICAgQlBNTiAyLjAtLT4-TXVsZSBCUE0gTW9kdWxlOiBjb250ZW50XG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGNvbnRlbnRcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIE1vZHVsZVxuICAgIGVuZFxuICAgIE11bGUgQlBNIEFwcC0tPj5NdWxlIEJQTSBNb2R1bGU6IHJlc3VsdFZhcmlhYmxlXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBBcHBcbiAgICBNdWxlIEJQTSBNb2R1bGUtLT4-QlBNTiAyLjA6IHJlc3VsdFZhcmlhYmxlXG4gICAgQlBNTiAyLjAtLT4-TXVsZSBCUE0gTW9kdWxlOiBkb25lXG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBlbmQiLCJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCJ9LCJ1cGRhdGVFZGl0b3IiOmZhbHNlfQ)

### Event subscription builder

`<bpm:event-subscription-builder />` can be used to subscribe events on Mule BPM Module. Currently process instance and variable lifecycle events are available.

Subscribed events can be awaited and/or retrieved without waiting:

[![
sequenceDiagram
    autonumber
    participant Munit
    participant Mule BPM App
    participant Mule BPM Module
    participant BPMN 2.0
    rect rgb(180, 180, 230)
    activate Munit
    Munit->>Mule BPM Module: <bpm:event-subscription-builder target="subscription" />
    Mule BPM Module-->>Munit: subscription
    deactivate Munit
    end
    rect rgb(200, 200, 240)
    activate Mule BPM App
    Mule BPM App->>Mule BPM Module: <bpm:process-factory />
    activate Mule BPM Module
    Mule BPM Module->>BPMN 2.0: <startEvent />
    activate BPMN 2.0
    Mule BPM Module->>BPMN 2.0: <serviceTask flowable:type="mule" flowable:async="false" />
    BPMN 2.0->>Mule BPM Module: org.flowable.mule.MuleSendActivityBehavior
    activate Mule BPM Module
    Mule BPM Module->>Mule BPM App: <bpm:task-listener />
    activate Mule BPM App
    Mule BPM App-->>Mule BPM Module: resultVariable
    deactivate Mule BPM App
    Mule BPM Module-->>BPMN 2.0: resultVariable
    BPMN 2.0-->>Mule BPM Module: done
    deactivate Mule BPM Module
    deactivate BPMN 2.0
    Mule BPM Module-->>Mule BPM App: com.alfame.esb.bpm.api.BPMProcessInstance
    deactivate Mule BPM Module
    deactivate Mule BPM App
    activate Munit
    rect rgb(180, 180, 230)
    BPMN 2.0--xMule BPM Module: process started event
    Munit->>Mule BPM Module: <bpm:wait-for-events subscription="#0035;[vars.subscription]" />
    Mule BPM Module-->>Munit: events
    end
    end
    rect rgb(180, 180, 230)
    Munit->>Mule BPM Module: <bpm:get-unique-event subscription="#0035;[vars.subscription]" />
    Mule BPM Module-->>Munit: event
    end
    Munit->>Munit: event.eventType
    deactivate Munit
](https://mermaid.ink/img/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgYXV0b251bWJlclxuICAgIHBhcnRpY2lwYW50IE11bml0XG4gICAgcGFydGljaXBhbnQgTXVsZSBCUE0gQXBwXG4gICAgcGFydGljaXBhbnQgTXVsZSBCUE0gTW9kdWxlXG4gICAgcGFydGljaXBhbnQgQlBNTiAyLjBcbiAgICByZWN0IHJnYigxODAsIDE4MCwgMjMwKVxuICAgIGFjdGl2YXRlIE11bml0XG4gICAgTXVuaXQtPj5NdWxlIEJQTSBNb2R1bGU6IDxicG06ZXZlbnQtc3Vic2NyaXB0aW9uLWJ1aWxkZXIgdGFyZ2V0PVwic3Vic2NyaXB0aW9uXCIgLz5cbiAgICBNdWxlIEJQTSBNb2R1bGUtLT4-TXVuaXQ6IHN1YnNjcmlwdGlvblxuICAgIGRlYWN0aXZhdGUgTXVuaXRcbiAgICBlbmRcbiAgICByZWN0IHJnYigyMDAsIDIwMCwgMjQwKVxuICAgIGFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIE11bGUgQlBNIEFwcC0-Pk11bGUgQlBNIE1vZHVsZTogPGJwbTpwcm9jZXNzLWZhY3RvcnkgLz5cbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5CUE1OIDIuMDogPHN0YXJ0RXZlbnQgLz5cbiAgICBhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiA8c2VydmljZVRhc2sgZmxvd2FibGU6dHlwZT1cIm11bGVcIiBmbG93YWJsZTphc3luYz1cImZhbHNlXCIgLz5cbiAgICBCUE1OIDIuMC0-Pk11bGUgQlBNIE1vZHVsZTogb3JnLmZsb3dhYmxlLm11bGUuTXVsZVNlbmRBY3Rpdml0eUJlaGF2aW9yXG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgTXVsZSBCUE0gTW9kdWxlLT4-TXVsZSBCUE0gQXBwOiA8YnBtOnRhc2stbGlzdGVuZXIgLz5cbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBBcHBcbiAgICBNdWxlIEJQTSBBcHAtLT4-TXVsZSBCUE0gTW9kdWxlOiByZXN1bHRWYXJpYWJsZVxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgTXVsZSBCUE0gTW9kdWxlLS0-PkJQTU4gMi4wOiByZXN1bHRWYXJpYWJsZVxuICAgIEJQTU4gMi4wLS0-Pk11bGUgQlBNIE1vZHVsZTogZG9uZVxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGNvbS5hbGZhbWUuZXNiLmJwbS5hcGkuQlBNUHJvY2Vzc0luc3RhbmNlXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIGFjdGl2YXRlIE11bml0XG4gICAgcmVjdCByZ2IoMTgwLCAxODAsIDIzMClcbiAgICBCUE1OIDIuMC0teE11bGUgQlBNIE1vZHVsZTogcHJvY2VzcyBzdGFydGVkIGV2ZW50XG4gICAgTXVuaXQtPj5NdWxlIEJQTSBNb2R1bGU6IDxicG06d2FpdC1mb3ItZXZlbnRzIHN1YnNjcmlwdGlvbj1cIiMwMDM1O1t2YXJzLnN1YnNjcmlwdGlvbl1cIiAvPlxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdW5pdDogZXZlbnRzXG4gICAgZW5kXG4gICAgZW5kXG4gICAgcmVjdCByZ2IoMTgwLCAxODAsIDIzMClcbiAgICBNdW5pdC0-Pk11bGUgQlBNIE1vZHVsZTogPGJwbTpnZXQtdW5pcXVlLWV2ZW50IHN1YnNjcmlwdGlvbj1cIiMwMDM1O1t2YXJzLnN1YnNjcmlwdGlvbl1cIiAvPlxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdW5pdDogZXZlbnRcbiAgICBlbmRcbiAgICBNdW5pdC0-Pk11bml0OiBldmVudC5ldmVudFR5cGVcbiAgICBkZWFjdGl2YXRlIE11bml0IiwibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQifSwidXBkYXRlRWRpdG9yIjpmYWxzZX0)](https://mermaid-js.github.io/mermaid-live-editor/#/edit/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgYXV0b251bWJlclxuICAgIHBhcnRpY2lwYW50IE11bml0XG4gICAgcGFydGljaXBhbnQgTXVsZSBCUE0gQXBwXG4gICAgcGFydGljaXBhbnQgTXVsZSBCUE0gTW9kdWxlXG4gICAgcGFydGljaXBhbnQgQlBNTiAyLjBcbiAgICByZWN0IHJnYigxODAsIDE4MCwgMjMwKVxuICAgIGFjdGl2YXRlIE11bml0XG4gICAgTXVuaXQtPj5NdWxlIEJQTSBNb2R1bGU6IDxicG06ZXZlbnQtc3Vic2NyaXB0aW9uLWJ1aWxkZXIgdGFyZ2V0PVwic3Vic2NyaXB0aW9uXCIgLz5cbiAgICBNdWxlIEJQTSBNb2R1bGUtLT4-TXVuaXQ6IHN1YnNjcmlwdGlvblxuICAgIGRlYWN0aXZhdGUgTXVuaXRcbiAgICBlbmRcbiAgICByZWN0IHJnYigyMDAsIDIwMCwgMjQwKVxuICAgIGFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIE11bGUgQlBNIEFwcC0-Pk11bGUgQlBNIE1vZHVsZTogPGJwbTpwcm9jZXNzLWZhY3RvcnkgLz5cbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBNdWxlIEJQTSBNb2R1bGUtPj5CUE1OIDIuMDogPHN0YXJ0RXZlbnQgLz5cbiAgICBhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIE11bGUgQlBNIE1vZHVsZS0-PkJQTU4gMi4wOiA8c2VydmljZVRhc2sgZmxvd2FibGU6dHlwZT1cIm11bGVcIiBmbG93YWJsZTphc3luYz1cImZhbHNlXCIgLz5cbiAgICBCUE1OIDIuMC0-Pk11bGUgQlBNIE1vZHVsZTogb3JnLmZsb3dhYmxlLm11bGUuTXVsZVNlbmRBY3Rpdml0eUJlaGF2aW9yXG4gICAgYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgTXVsZSBCUE0gTW9kdWxlLT4-TXVsZSBCUE0gQXBwOiA8YnBtOnRhc2stbGlzdGVuZXIgLz5cbiAgICBhY3RpdmF0ZSBNdWxlIEJQTSBBcHBcbiAgICBNdWxlIEJQTSBBcHAtLT4-TXVsZSBCUE0gTW9kdWxlOiByZXN1bHRWYXJpYWJsZVxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gQXBwXG4gICAgTXVsZSBCUE0gTW9kdWxlLS0-PkJQTU4gMi4wOiByZXN1bHRWYXJpYWJsZVxuICAgIEJQTU4gMi4wLS0-Pk11bGUgQlBNIE1vZHVsZTogZG9uZVxuICAgIGRlYWN0aXZhdGUgTXVsZSBCUE0gTW9kdWxlXG4gICAgZGVhY3RpdmF0ZSBCUE1OIDIuMFxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdWxlIEJQTSBBcHA6IGNvbS5hbGZhbWUuZXNiLmJwbS5hcGkuQlBNUHJvY2Vzc0luc3RhbmNlXG4gICAgZGVhY3RpdmF0ZSBNdWxlIEJQTSBNb2R1bGVcbiAgICBkZWFjdGl2YXRlIE11bGUgQlBNIEFwcFxuICAgIGFjdGl2YXRlIE11bml0XG4gICAgcmVjdCByZ2IoMTgwLCAxODAsIDIzMClcbiAgICBCUE1OIDIuMC0teE11bGUgQlBNIE1vZHVsZTogcHJvY2VzcyBzdGFydGVkIGV2ZW50XG4gICAgTXVuaXQtPj5NdWxlIEJQTSBNb2R1bGU6IDxicG06d2FpdC1mb3ItZXZlbnRzIHN1YnNjcmlwdGlvbj1cIiMwMDM1O1t2YXJzLnN1YnNjcmlwdGlvbl1cIiAvPlxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdW5pdDogZXZlbnRzXG4gICAgZW5kXG4gICAgZW5kXG4gICAgcmVjdCByZ2IoMTgwLCAxODAsIDIzMClcbiAgICBNdW5pdC0-Pk11bGUgQlBNIE1vZHVsZTogPGJwbTpnZXQtdW5pcXVlLWV2ZW50IHN1YnNjcmlwdGlvbj1cIiMwMDM1O1t2YXJzLnN1YnNjcmlwdGlvbl1cIiAvPlxuICAgIE11bGUgQlBNIE1vZHVsZS0tPj5NdW5pdDogZXZlbnRcbiAgICBlbmRcbiAgICBNdW5pdC0-Pk11bml0OiBldmVudC5ldmVudFR5cGVcbiAgICBkZWFjdGl2YXRlIE11bml0IiwibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQifSwidXBkYXRlRWRpdG9yIjpmYWxzZX0)

Awaiting events is really useful for Munit tests. However, event subscription is fully production grade implementation, which can be used inside actual Mule BPM Applications.


### Event filters

Events can be filtered by adding nested `<bpm:event-filters />` tag inside `<bpm:event-subscription-builder />`, `<bpm:get-unique-event />` and `<bpm:get-events />` or `<bpm:event-listener />` tags. By default, all matched events are returned. After adding filters, only matching events are qualified. 

Multiple filters of a same or different type may be added. Filters of the same type have a logical OR relationship and filters of a different type have a logical AND relationship. 

For example, this would filter  `(foo OR bar) AND baz AND TASK_CREATED`.

```
<bpm:event-filters>
    <bpm:activity-name-filter activityName="foo" />
    <bpm:activity-name-filter activityName="bar" />
    <bpm:process-definition-filter key="baz" />
    <bpm:event-type-filter eventType="TASK_CREATED" />
</bpm:event-filters>
```

Types of filters:
`<bpm:process-definition-filter key="foo">` filters for `<process id="foo">` in the process .bpmn20.xml file.
`<bpm:activity-name-filter activityName="bar">` filters for example `<userTask id="bar">`
`<bpm:event-type-filter eventType="TASK_CREATED" />` filters for event type such as TASK_CREATED, PROCESS_INSTANCE_CREATED, PROCESS_INSTANCE_ENDED
