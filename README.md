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
    
    
Creazione pacchetto
-------------------
Per generare il pacchetto della libreria è necessario utilizzare maven.
Il pacchetto viene generator tramite il comando `mvn install`.
