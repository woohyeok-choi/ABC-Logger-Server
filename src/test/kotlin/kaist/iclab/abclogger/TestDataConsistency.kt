package kaist.iclab.abclogger

import io.grpc.ManagedChannel
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCaseOrder
import kaist.iclab.abclogger.grpc.proto.DatumProtos
import kaist.iclab.abclogger.grpc.proto.SubjectProtos
import kaist.iclab.abclogger.grpc.service.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit


private const val START_TIME = 0L
private const val END_TIME = 400L
private val TIME_RANGE = START_TIME until END_TIME
private val DATA_TYPES = DatumProtos.DatumType.values().filter {
    it != DatumProtos.DatumType.ALL && it != DatumProtos.DatumType.UNRECOGNIZED
}

class TestDataConsistency : StringSpec() {
    private lateinit var channel: ManagedChannel
    private lateinit var dataStub: DataOperationsGrpcKt.DataOperationsCoroutineStub
    private lateinit var heartBeatStub: HeartBeatsOperationGrpcKt.HeartBeatsOperationCoroutineStub
    private lateinit var subjectStub: SubjectsOperationsGrpcKt.SubjectsOperationsCoroutineStub
    private lateinit var aggregateStub: AggregateOperationsGrpcKt.AggregateOperationsCoroutineStub

    private lateinit var app: App

    override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Sequential

    override fun beforeSpec(spec: Spec) {
        app = app()
        channel = channel()
        dataStub = DataOperationsGrpcKt.DataOperationsCoroutineStub(channel)
        heartBeatStub = HeartBeatsOperationGrpcKt.HeartBeatsOperationCoroutineStub(channel)
        subjectStub = SubjectsOperationsGrpcKt.SubjectsOperationsCoroutineStub(channel)
        aggregateStub = AggregateOperationsGrpcKt.AggregateOperationsCoroutineStub(channel)
    }

    override fun afterSpec(spec: Spec) {
        channel.shutdown()
        app.stop()
    }

    init {

        DATA_TYPES.forEach { checkConsistencyForDataType(it) }

        checkDataConsistencyForCreateDatum(DATA_TYPES, TIME_RANGE)
        checkDataConsistencyForCreateData(DATA_TYPES, TIME_RANGE)
        checkDataConsistencyForCreateDataAsStream(DATA_TYPES, TIME_RANGE)
        checkDataConsistencyForCreateDataAsStreamAndReadDataAsStream(DATA_TYPES, TIME_RANGE)
        checkDataConsistencyForQueryingSharedFields(DATA_TYPES, TIME_RANGE)
        checkDataConsistencyForQueryingMultipleFields(DATA_TYPES, TIME_RANGE)
/*
        DATA_TYPES.forEach { checkHeartBeatConsistencyForEachDataType(it, TIME_RANGE) }

        checkHeartBeatConsistencyForMultipleDataTypes(DATA_TYPES, TIME_RANGE)
        checkHeartBeatConsistencyForQueryingSharedFields(DATA_TYPES, TIME_RANGE)
        checkHeartBeatConsistencyForQueryingMultipleFields(DATA_TYPES, TIME_RANGE)*/
    }

    private fun checkConsistencyForDataType(dataType: DatumProtos.DatumType) {
        "Check a Consistency for Data $dataType" {
            val subject = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group0"
                this.email = "email0"
                this.instanceId = "한글테스트됨?"
                this.source = "source0"
                this.deviceManufacturer = "manufacturer0"
                this.deviceVersion = "version0"
                this.deviceModel = "model0"
                this.deviceOs = "os0"
                this.appId = "appid0"
                this.appVersion = "appversion0"
            }.build()

            val data = TIME_RANGE.map { timestamp ->
                datum(
                    datumType = dataType,
                    timestamp = timestamp,
                    subject = subject
                )
            }

            testCreateAndReadDatum(
                delay = TimeUnit.SECONDS.toMillis(20),
                create = {
                    data.forEach { dataStub.createDatum(it) }
                    data
                },
                read = {
                    dataStub.readData(
                        queryRead(setOf(dataType), setOf(subject), START_TIME, END_TIME)
                    ).datumList
                },
                subject = {
                    subjectStub.readSubjects(
                        queryRead(setOf(), setOf(subject), START_TIME, END_TIME)
                    ).subjectList
                },
                aggregate = {
                    aggregateStub.countData(
                        queryAggregate(setOf(dataType), setOf(subject), START_TIME, END_TIME)
                    )
                }
            )
        }
    }

    private fun checkDataConsistencyForCreateDatum(
        dataTypes: Collection<DatumProtos.DatumType>,
        timeRange: LongRange
    ) {
        "Check Consistencies for CreateDatum" {
            val subject = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group1"
                this.email = "email1"
                this.instanceId = "instanceid1"
                this.source = "source1"
                this.deviceManufacturer = "manufacturer1"
                this.deviceModel = "model1"
                this.deviceOs = "os1"
                this.appId = "appid1"
                this.appVersion = "appversion1"
            }.build()

            val data = dataTypes.map { dataType ->
                timeRange.map { timestamp ->
                    datum(
                        datumType = dataType,
                        timestamp = timestamp,
                        subject = subject
                    )
                }
            }.flatten()

            testCreateAndReadDatum(
                create = {
                    data.forEach { dataStub.createDatum(it) }
                    data
                },
                read = {
                    dataStub.readDataAsStream(
                        queryRead(setOf(), setOf(subject), START_TIME, END_TIME)
                    ).toList()
                },
                subject = {
                    subjectStub.readSubjectsAsStream(
                        queryRead(setOf(), setOf(subject), START_TIME, END_TIME)
                    ).toList()
                },
                aggregate = {
                    aggregateStub.countData(
                        queryAggregate(setOf(), setOf(subject), START_TIME, END_TIME)
                    )
                }
            )
        }
    }

    private fun checkDataConsistencyForCreateData(
        dataTypes: Collection<DatumProtos.DatumType>,
        timeRange: LongRange
    ) {
        "Check Consistencies for CreateData" {
            val subject = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group2"
                this.email = "email2"
                this.instanceId = "instanceid2"
                this.source = "source2"
                this.deviceManufacturer = "manufacturer2"
                this.deviceModel = "model2"
                this.deviceOs = "os2"
                this.appId = "appid2"
                this.appVersion = "appversion2"
            }.build()

            val data = dataTypes.map { dataType ->
                timeRange.map { timestamp ->
                    datum(
                        datumType = dataType,
                        timestamp = timestamp,
                        subject = subject
                    )
                }
            }.flatten()

            testCreateAndReadDatum(
                create = {
                    dataStub.createData(ServiceProtos.Bulk.Data.newBuilder().addAllDatum(data).build())
                    data
                },
                read = {
                    dataStub.readDataAsStream(
                        queryRead(setOf(), setOf(subject), START_TIME, END_TIME)
                    ).toList()
                },
                subject = {
                    subjectStub.readSubjects(
                        queryRead(setOf(), setOf(subject), START_TIME, END_TIME)
                    ).subjectList
                },
                aggregate = {
                    aggregateStub.countData(
                        queryAggregate(setOf(), setOf(subject), START_TIME, END_TIME)
                    )
                }
            )
        }
    }

    private fun checkDataConsistencyForCreateDataAsStream(
        dataTypes: Collection<DatumProtos.DatumType>,
        timeRange: LongRange
    ) {
        "Check Consistencies for CreateDataAsStream" {
            val subject = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group3"
                this.email = "email3"
                this.instanceId = "instanceid3"
                this.source = "source3"
                this.deviceManufacturer = "manufacturer3"
                this.deviceModel = "model3"
                this.deviceOs = "os3"
                this.appId = "appid3"
                this.appVersion = "appversion3"
            }.build()

            val data = dataTypes.map { dataType ->
                timeRange.map { timestamp ->
                    datum(
                        datumType = dataType,
                        timestamp = timestamp,
                        subject = subject
                    )
                }
            }.flatten()

            testCreateAndReadDatum(
                create = {
                    dataStub.createDataAsStream(data.asFlow())
                    data
                },
                read = {
                    dataStub.readDataAsStream(
                        queryRead(setOf(), setOf(subject), START_TIME, END_TIME)
                    ).toList()
                },
                subject = {
                    subjectStub.readSubjects(
                        queryRead(setOf(), setOf(subject), START_TIME, END_TIME)
                    ).subjectList
                },
                aggregate = {
                    aggregateStub.countData(
                        queryAggregate(setOf(), setOf(subject), START_TIME, END_TIME)
                    )
                }
            )
        }
    }

    private fun checkDataConsistencyForCreateDataAsStreamAndReadDataAsStream(
        dataTypes: Collection<DatumProtos.DatumType>,
        timeRange: LongRange
    ) {
        "Check Consistencies for CreateDataAsStreamAndReadDataAsStream" {
            val subject = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group4"
                this.email = "email4"
                this.instanceId = "instanceid4"
                this.source = "source4"
                this.deviceManufacturer = "manufacturer4"
                this.deviceModel = "model4"
                this.deviceOs = "os4"
                this.appId = "appid4"
                this.appVersion = "appversion4"
            }.build()

            val data = dataTypes.map { dataType ->
                timeRange.map { timestamp ->
                    datum(
                        datumType = dataType,
                        timestamp = timestamp,
                        subject = subject
                    )
                }
            }.flatten()

            testCreateAndReadDatum(
                create = {
                    dataStub.createDataAsStream(data.asFlow())
                    data
                },
                read = {
                    dataStub.readDataAsStream(
                        queryRead(setOf(), setOf(subject), START_TIME, END_TIME)
                    ).toList()
                },
                subject = {
                    subjectStub.readSubjects(
                        queryRead(setOf(), setOf(subject), START_TIME, END_TIME)
                    ).subjectList
                },
                aggregate = {
                    aggregateStub.countData(
                        queryAggregate(setOf(), setOf(subject), START_TIME, END_TIME)
                    )
                }
            )
        }
    }

    private fun checkDataConsistencyForQueryingSharedFields(
        dataTypes: Collection<DatumProtos.DatumType>,
        timeRange: LongRange
    ) {
        "Check Consistencies for Querying Shared Fields" {
            val subjectFirst = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group5"
                this.email = "email5"
                this.instanceId = "instanceid5"
                this.source = "source5"
                this.deviceManufacturer = "manufacturer5"
                this.deviceModel = "model5"
                this.deviceOs = "os5"
                this.appId = "appid5"
                this.appVersion = "appversion5"
            }.build()

            val subjectSecond = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group5"
                this.email = "email5"
                this.instanceId = "instanceid5"
                this.source = "source6"
                this.deviceManufacturer = "manufacturer6"
                this.deviceModel = "model6"
                this.deviceOs = "os6"
                this.appId = "appid6"
                this.appVersion = "appversion6"
            }.build()

            val dataFirst = dataTypes.map { dataType ->
                timeRange.map { timestamp ->
                    datum(
                        datumType = dataType,
                        timestamp = timestamp,
                        subject = subjectFirst
                    )
                }
            }.flatten()
            val dataSecond = dataTypes.map { dataType ->
                timeRange.map { timestamp ->
                    datum(
                        datumType = dataType,
                        timestamp = timestamp,
                        subject = subjectSecond
                    )
                }
            }.flatten()

            val data = dataFirst + dataSecond
            val subjectQuery = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group5"
                this.email = "email5"
                this.instanceId = "instanceid5"
            }.build()

            testCreateAndReadDatum(
                create = {
                    dataStub.createDataAsStream(data.asFlow())
                    data
                },
                read = {
                    dataStub.readDataAsStream(
                        queryRead(setOf(), setOf(subjectQuery), START_TIME, END_TIME)
                    ).toList()
                },
                subject = {
                    subjectStub.readSubjects(
                        queryRead(setOf(), setOf(subjectQuery), START_TIME, END_TIME)
                    ).subjectList
                },
                aggregate = {
                    aggregateStub.countData(
                        queryAggregate(setOf(), setOf(subjectQuery), START_TIME, END_TIME)
                    )
                }
            )
        }
    }

    private fun checkDataConsistencyForQueryingMultipleFields(
        dataTypes: Collection<DatumProtos.DatumType>,
        timeRange: LongRange
    ) {
        "Check Consistencies for Querying Multiple Fields" {
            val subjectFirst = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group7"
                this.email = "email7"
                this.instanceId = "instanceid7"
                this.source = "source7"
                this.deviceManufacturer = "manufacturer7"
                this.deviceModel = "model7"
                this.deviceOs = "os7"
                this.appId = "appid7"
                this.appVersion = "appversion7"
            }.build()

            val subjectSecond = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group8"
                this.email = "email8"
                this.instanceId = "instanceid8"
                this.source = "source8"
                this.deviceManufacturer = "manufacturer8"
                this.deviceModel = "model8"
                this.deviceOs = "os8"
                this.appId = "appid8"
                this.appVersion = "appversion8"
            }.build()

            val dataFirst = dataTypes.map { dataType ->
                timeRange.map { timestamp ->
                    datum(
                        datumType = dataType,
                        timestamp = timestamp,
                        subject = subjectFirst
                    )
                }
            }.flatten()
            val dataSecond = dataTypes.map { dataType ->
                timeRange.map { timestamp ->
                    datum(
                        datumType = dataType,
                        timestamp = timestamp,
                        subject = subjectSecond
                    )
                }
            }.flatten()

            val data = dataFirst + dataSecond
            val subjectQuery = setOf(
                SubjectProtos.Subject.newBuilder().apply {
                    this.groupName = "group7"
                    this.email = "email7"
                    this.instanceId = "instanceid7"
                }.build(),
                SubjectProtos.Subject.newBuilder().apply {
                    this.groupName = "group8"
                    this.email = "email8"
                    this.instanceId = "instanceid8"
                }.build()
            )

            testCreateAndReadDatum(
                create = {
                    dataStub.createDataAsStream(data.asFlow())
                    data
                },
                read = {
                    dataStub.readDataAsStream(
                        queryRead(setOf(), subjectQuery, START_TIME, END_TIME)
                    ).toList()
                },
                subject = {
                    subjectStub.readSubjects(
                        queryRead(setOf(), subjectQuery, START_TIME, END_TIME)
                    ).subjectList
                },
                aggregate = {
                    aggregateStub.countData(
                        queryAggregate(setOf(), subjectQuery, START_TIME, END_TIME)
                    )
                }
            )
        }
    }

    private fun checkHeartBeatConsistencyForEachDataType(
        dataType: DatumProtos.DatumType,
        timeRange: LongRange
    ) {
        "Check a Consistency for HeartBeat $dataType" {
            val subject = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group0"
                this.email = "email0"
                this.instanceId = "instanceid0"
                this.source = "source0"
                this.deviceManufacturer = "manufacturer0"
                this.deviceModel = "model0"
                this.deviceOs = "os0"
                this.appId = "appid0"
                this.appVersion = "appversion0"
            }.build()

            val heartBeats = timeRange.map { timestamp ->
                heartBeat(
                    dataTypes = listOf(dataType),
                    timestamp = timestamp,
                    subject = subject
                )
            }
            testCreateAndReadHeartBeat(
                delay = TimeUnit.SECONDS.toMillis(20),
                createHeartBeats = {
                    heartBeats.forEach { heartBeatStub.createHeartBeat(it) }
                    heartBeats
                },
                readHeartBeats = {
                    heartBeatStub.readHeartBeats(
                        queryRead(setOf(dataType), setOf(subject), START_TIME, END_TIME)
                    ).heartBeatList
                }
            )

        }
    }

    private fun checkHeartBeatConsistencyForMultipleDataTypes(
        dataTypes: Collection<DatumProtos.DatumType>,
        timeRange: LongRange
    ) {
        "Check a Consistency for HeartBeat with Multiple Data Types" {
            val subject = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group1"
                this.email = "email1"
                this.instanceId = "instanceid1"
                this.source = "source1"
                this.deviceManufacturer = "manufacturer1"
                this.deviceModel = "model1"
                this.deviceOs = "os1"
                this.appId = "appid1"
                this.appVersion = "appversion1"
            }.build()

            val heartBeats = timeRange.map { timestamp ->
                heartBeat(
                    dataTypes = dataTypes,
                    timestamp = timestamp,
                    subject = subject
                )
            }
            testCreateAndReadHeartBeat(
                delay = TimeUnit.SECONDS.toMillis(20),
                createHeartBeats = {
                    heartBeats.forEach { heartBeatStub.createHeartBeat(it) }
                    heartBeats
                },
                readHeartBeats = {
                    heartBeatStub.readHeartBeatsAsStream(
                        queryRead(dataTypes.toSet(), setOf(subject), START_TIME, END_TIME)
                    ).toList()
                }
            )
        }
    }

    private fun checkHeartBeatConsistencyForQueryingSharedFields(
        dataTypes: Collection<DatumProtos.DatumType>,
        timeRange: LongRange
    ) {
        "Check a Consistency for HeartBeat with Querying Shared Fields" {
            val subjectFirst = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group5"
                this.email = "email5"
                this.instanceId = "instanceid5"
                this.source = "source5"
                this.deviceManufacturer = "manufacturer5"
                this.deviceModel = "model5"
                this.deviceOs = "os5"
                this.appId = "appid5"
                this.appVersion = "appversion5"
            }.build()

            val subjectSecond = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group5"
                this.email = "email5"
                this.instanceId = "instanceid5"
                this.source = "source6"
                this.deviceManufacturer = "manufacturer6"
                this.deviceModel = "model6"
                this.deviceOs = "os6"
                this.appId = "appid6"
                this.appVersion = "appversion6"
            }.build()

            val heartBeatsFirst = timeRange.map { timestamp ->
                heartBeat(
                    dataTypes = dataTypes,
                    timestamp = timestamp,
                    subject = subjectFirst
                )
            }
            val heartBeatsSecond = timeRange.map { timestamp ->
                heartBeat(
                    dataTypes = dataTypes,
                    timestamp = timestamp,
                    subject = subjectSecond
                )
            }
            val heartBeats = heartBeatsFirst + heartBeatsSecond
            val subjectQuery = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group5"
                this.email = "email5"
                this.instanceId = "instanceid5"
            }.build()

            testCreateAndReadHeartBeat(
                delay = TimeUnit.SECONDS.toMillis(20),
                createHeartBeats = {
                    heartBeats.forEach { heartBeatStub.createHeartBeat(it) }
                    heartBeats
                },

                readHeartBeats = {
                    heartBeatStub.readHeartBeats(
                        queryRead(dataTypes.toSet(), setOf(subjectQuery), START_TIME, END_TIME)
                    ).heartBeatList
                }
            )
        }
    }

    private fun checkHeartBeatConsistencyForQueryingMultipleFields(
        dataTypes: Collection<DatumProtos.DatumType>,
        timeRange: LongRange
    ) {
        "Check a Consistency for HeartBeat with Querying Multiple Fields" {
            val subjectFirst = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group7"
                this.email = "email7"
                this.instanceId = "instanceid7"
                this.source = "source7"
                this.deviceManufacturer = "manufacturer7"
                this.deviceModel = "model7"
                this.deviceOs = "os7"
                this.appId = "appid7"
                this.appVersion = "appversion7"
            }.build()

            val subjectSecond = SubjectProtos.Subject.newBuilder().apply {
                this.groupName = "group8"
                this.email = "email8"
                this.instanceId = "instanceid8"
                this.source = "source8"
                this.deviceManufacturer = "manufacturer8"
                this.deviceModel = "model8"
                this.deviceOs = "os8"
                this.appId = "appid8"
                this.appVersion = "appversion8"
            }.build()

            val heartBeatsFirst = timeRange.map { timestamp ->
                heartBeat(
                    dataTypes = dataTypes,
                    timestamp = timestamp,
                    subject = subjectFirst
                )
            }
            val heartBeatsSecond = timeRange.map { timestamp ->
                heartBeat(
                    dataTypes = dataTypes,
                    timestamp = timestamp,
                    subject = subjectSecond
                )
            }
            val heartBeats = heartBeatsFirst + heartBeatsSecond
            val subjectQuery = setOf(
                SubjectProtos.Subject.newBuilder().apply {
                    this.groupName = "group7"
                    this.email = "email7"
                    this.instanceId = "instanceid7"
                }.build(),
                SubjectProtos.Subject.newBuilder().apply {
                    this.groupName = "group8"
                    this.email = "email8"
                    this.instanceId = "instanceid8"
                }.build()
            )
            testCreateAndReadHeartBeat(
                delay = TimeUnit.SECONDS.toMillis(20),
                createHeartBeats = {
                    heartBeats.forEach { heartBeatStub.createHeartBeat(it) }
                    heartBeats
                },

                readHeartBeats = {
                    heartBeatStub.readHeartBeatsAsStream(
                        queryRead(dataTypes.toSet(), subjectQuery, START_TIME, END_TIME)
                    ).toList()
                }
            )
        }
    }


}