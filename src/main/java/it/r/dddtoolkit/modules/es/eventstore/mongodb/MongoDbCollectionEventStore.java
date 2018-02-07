//package it.r.dddtoolkit.modules.es.eventstore.mongodb;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.Iterators;
//import com.mongodb.DBCollection;
//import com.mongodb.WriteConcern;
//import it.r.dddtoolkit.modules.es.ddd.AggregateTransaction;
//import it.r.dddtoolkit.core.Context;
//import it.r.dddtoolkit.modules.es.eventstore.EventStore;
//import it.r.dddtoolkit.modules.es.eventstore.EventStream;
//import it.r.dddtoolkit.modules.es.eventstore.Version;
//import it.r.dddtoolkit.modules.es.support.EventPublisher;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import org.bson.types.ObjectId;
//import org.mongojack.DBCursor;
//import org.mongojack.DBSort;
//import org.mongojack.JacksonDBCollection;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Iterator;
//import java.util.List;
//import java.util.Optional;
//
////TODO: remove mongojack
//@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
//public class MongoDbCollectionEventStore<C extends Context> implements EventStore<C> {
//
//    private static final String VERSION_TIMESTAMP = "version.timestamp";
//    private static final Logger LOG = LoggerFactory.getLogger(MongoDbCollectionEventStore.class);
//
//    private final JacksonDBCollection<TransactionCommitDocument, ObjectId> collection;
//
//    public MongoDbCollectionEventStore(DBCollection collection, ObjectMapper mapper) {
//        this(JacksonDBCollection.wrap(collection, TransactionCommitDocument.class, ObjectId.class, mapper));
//    }
//
//    public MongoDbCollectionEventStore(DBCollection collection, EventPublisher eventPublisher) {
//        this(collection, new ObjectMapper());
//    }
//
//    @Override
//    public EventStream<C> eventStream(String streamId) {
//
//        final Iterator<TransactionCommitDocument> cursor = collection.find()
//            .is("eventStreamId", streamId)
//            .sort(DBSort.asc(VERSION_TIMESTAMP))
//            .iterator();
//
//        final Iterator<AggregateTransaction<C>> transactions = Iterators.transform(
//            cursor,
//            tx -> new AggregateTransaction<>(streamId, tx.getEvents(), tx.getContext())
//        );
//        final List<AggregateTransaction<C>> events = ImmutableList.copyOf(transactions);
//
//        return new EventStream<>(events);
//    }
//
//    @Override
//    public Version append(AggregateTransaction<C> tx, Version expectedVersion) {
//        final String id = tx.getStreamId();
//
//        final Version actualVersion = versionOf(id);
//        if (!actualVersion.equals(expectedVersion)) {
//            throw new IllegalStateException(String.format("Concurrent modification of stream [%s] expected version %s but was %s", id, expectedVersion, actualVersion));
//        }
//        final Version nextVersion = actualVersion.next();
//
//        final TransactionCommitDocument<C> document = new TransactionCommitDocument<>(
//            null, id, nextVersion, tx.getEvents(), tx.getContext());
//
//        LOG.debug("Appending history to stream {}", id);
//
//        /*
//         *   TODO
//         *    reimplement the model having 1 document per eventstream
//         *    so that here we can use the $findAndUpdate
//         */
//        collection.insert(WriteConcern.MAJORITY, document);
//
//        return nextVersion;
//    }
//
//
//    @Override
//    public EventStream happenedFrom(Version version) {
//        final Iterator<TransactionCommitDocument> commits = collection.find()
//            .sort(DBSort.asc(VERSION_TIMESTAMP))
//            .iterator();
//
//        return new EventStream(ImmutableList.copyOf(commits));
//    }
//
//    private Version versionOf(String stream) {
//        return lastEventOf(stream)
//            .map(TransactionCommitDocument::getVersion)
//            .orElse(Version.UNINITIALIZED);
//    }
//
//    private Optional<TransactionCommitDocument> lastEventOf(String streamId) {
//        final DBCursor<TransactionCommitDocument> descriptors = collection.find()
//            .is("eventStreamId", streamId)
//            .sort(DBSort.desc(VERSION_TIMESTAMP));
//
//        return descriptors.hasNext() ? Optional.of(descriptors.next()) : Optional.empty();
//    }
//}
