ddd-toolkit
===========

Toolkit di esempio per progetti con *DDD+CQRS+ES*

Navigazione package
-------------------

- **it.r.dddtoolkit.cqrs** è l'entry point delle classi di supporto per CQRS
- **it.r.dddtoolkit.ddd** è l'entry point delle classi generali per DDD
- **it.r.dddtoolkit.es** è l'entry point delle classi per Event Sourcing
    - **.ddd** contiene alcune implementazioni basi delle classi generali di DDD per adattarle ad ES
    - **.eventstore** contiene le classi utilizzate per implementare l'Event Store
    - **.support** classi di utils per event sourcing
    

Domain Entity & Event Sourcing
------------------------------
Per la gestione delle _entity_ tramite _EventSourcing_ sono messe a disposizione le seguenti classi:
**EventSourcedDomainEntity**: È la rappresentazione dell'entity, ed espone tutti i comandi che si possono lanciare su di essa.
Quando si esegue un comando l'EventSourcedDomainEntity si occuperà di controllare che i parametri del comando siano corretti,
che il suo stato permetta la modifica, ed infine modificare il suo stato tramite l'utilizzo del metodo ``apply(DomainEvent)`` passandogli
l'oggetto che rappresenta il cambiamento che vogliamo fare (Esempio: [TickTock](https://github.com/rascio/ddd-toolkit/blob/master/src/test/java/it/r/dddtoolkit/es/ddd/TickTock.java))  


Al suo interno però un'EventSourcedDomainEntity ha bisogno di uno "stato", rappresentato dalla classe **DomainEntityState**.
DomaniEntityState è una classe astratta che forza l'implementazione del metodo ``init()``.   
In questo metodo tramite la creazione di un **BehaviorEntity** si definisce come questo oggetto "reagisce" agli eventi (si modifica).
Per un esempio è possibile vedere [TickTockState](https://github.com/rascio/ddd-toolkit/blob/master/src/test/java/it/r/dddtoolkit/es/ddd/TickTockState.java).

In questo esempio si può vedere come le reazioni agli eventi vengono definite implementando al volo l'interfaccia **Behavior**.
Si è scelta questa tecnica in favore delle _lambda expression_ di Java 8:

```java
@Override
protected EntityBehavior init() {
    return behavior()
        .when(TickEvent.class, (TickEvent event) -> setTick(true))
        .when(TockEvent.class, (TockEvent event) -> setTick(false));
}
```


Creazione pacchetto
-------------------
Per generare il pacchetto della libreria è necessario utilizzare maven.
Il pacchetto viene generator tramite il comando `mvn install`.

**ATTENZIONE** Il progetto utilizza Lombok durante la fase di compilazione, per poterlo importare correttamente
in un IDE si dovrà installare Lombok. Per questo fare riferimento alla guida ufficiale: http://projectlombok.org/features/index.html
