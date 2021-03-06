/*
 * Copyright 2014 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.reactivestreams.client;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.client.model.CreateCollectionOptions;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.reactivestreams.Publisher;

import static com.mongodb.assertions.Assertions.notNull;

class MongoDatabaseImpl implements MongoDatabase {

    private final com.mongodb.async.client.MongoDatabase wrapped;

    MongoDatabaseImpl(final com.mongodb.async.client.MongoDatabase wrapped) {
        this.wrapped = notNull("wrapped", wrapped);
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public CodecRegistry getCodecRegistry() {
        return wrapped.getCodecRegistry();
    }

    @Override
    public ReadPreference getReadPreference() {
        return wrapped.getReadPreference();
    }

    @Override
    public WriteConcern getWriteConcern() {
        return wrapped.getWriteConcern();
    }

    @Override
    public MongoDatabase withCodecRegistry(final CodecRegistry codecRegistry) {
        return new MongoDatabaseImpl(wrapped.withCodecRegistry(codecRegistry));
    }

    @Override
    public MongoDatabase withReadPreference(final ReadPreference readPreference) {
        return new MongoDatabaseImpl(wrapped.withReadPreference(readPreference));
    }

    @Override
    public MongoDatabase withWriteConcern(final WriteConcern writeConcern) {
        return new MongoDatabaseImpl(wrapped.withWriteConcern(writeConcern));
    }

    @Override
    public MongoCollection<Document> getCollection(final String collectionName) {
        return getCollection(collectionName, Document.class);
    }

    @Override
    public <T> MongoCollection<T> getCollection(final String collectionName, final Class<T> clazz) {
        return new MongoCollectionImpl<T>(wrapped.getCollection(collectionName, clazz));
    }

    @Override
    public Publisher<Document> executeCommand(final Object command) {
        return executeCommand(command, Document.class);
    }

    @Override
    public Publisher<Document> executeCommand(final Object command, final ReadPreference readPreference) {
        return executeCommand(command, readPreference, Document.class);
    }

    @Override
    public <T> Publisher<T> executeCommand(final Object command, final Class<T> clazz) {
        return new SingleResultPublisher<T>() {
            @Override
            void execute(final SingleResultCallback<T> callback) {
                wrapped.executeCommand(command, clazz, callback);
            }
        };
    }

    @Override
    public <T> Publisher<T> executeCommand(final Object command, final ReadPreference readPreference, final Class<T> clazz) {
        return new SingleResultPublisher<T>() {
            @Override
            void execute(final SingleResultCallback<T> callback) {
                wrapped.executeCommand(command, readPreference, clazz, callback);
            }
        };
    }

    @Override
    public Publisher<Void> dropDatabase() {
        return new SingleResultPublisher<Void>() {
            @Override
            void execute(final SingleResultCallback<Void> callback) {
                wrapped.dropDatabase(callback);
            }
        };
    }

    @Override
    public Publisher<String> listCollectionNames() {
        return new MongoIterablePublisher<String>(wrapped.listCollectionNames());
    }

    @Override
    public ListCollectionsPublisher<Document> listCollections() {
        return listCollections(Document.class);
    }

    @Override
    public <C> ListCollectionsPublisher<C> listCollections(final Class<C> clazz) {
        return new ListCollectionsPublisherImpl<C>(wrapped.listCollections(clazz));
    }

    @Override
    public Publisher<Void> createCollection(final String collectionName) {
        return createCollection(collectionName, new CreateCollectionOptions());
    }

    @Override
    public Publisher<Void> createCollection(final String collectionName, final CreateCollectionOptions options) {
        return new SingleResultPublisher<Void>() {
            @Override
            void execute(final SingleResultCallback<Void> callback) {
                wrapped.createCollection(collectionName, options, callback);
            }
        };
    }
}
