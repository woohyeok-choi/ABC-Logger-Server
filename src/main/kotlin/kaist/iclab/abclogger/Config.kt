package kaist.iclab.abclogger

import kaist.iclab.abclogger.schema.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.litote.kmongo.ascending
import org.litote.kmongo.div

object Config {
    val serializersModule = SerializersModule {
        polymorphic(Value::class, Value.serializer()) {
            subclass(PhysicalActivity::class, PhysicalActivity.serializer())
            subclass(PhysicalActivityTransition::class, PhysicalActivityTransition.serializer())
            subclass(PhysicalActivity::class, PhysicalActivity.serializer())
            subclass(AppUsageEvent::class, AppUsageEvent.serializer())
            subclass(Battery::class, Battery.serializer())
            subclass(Bluetooth::class, Bluetooth.serializer())
            subclass(CallLog::class, CallLog.serializer())
            subclass(DeviceEvent::class, DeviceEvent.serializer())
            subclass(EmbeddedSensor::class, EmbeddedSensor.serializer())
            subclass(ExternalSensor::class, ExternalSensor.serializer())
            subclass(InstalledApp::class, InstalledApp.serializer())
            subclass(KeyLog::class, KeyLog.serializer())
            subclass(Location::class, Location.serializer())
            subclass(Media::class, Media.serializer())
            subclass(Message::class, Message.serializer())
            subclass(Notification::class, Notification.serializer())
            subclass(Fitness::class, Fitness.serializer())
            subclass(Survey::class, Survey.serializer())
            subclass(DataTraffic::class, DataTraffic.serializer())
            subclass(Wifi::class, Wifi.serializer())
        }
    }

    val datumIndices = listOf(
        ascending(Datum::datumType, Datum::subject, Datum::timestamp),
        ascending(Datum::timestamp, Datum::subject / Subject::groupName),
        ascending(Datum::timestamp, Datum::subject / Subject::email),
        ascending(Datum::timestamp, Datum::subject / Subject::hashedEmail),
        ascending(Datum::timestamp, Datum::subject / Subject::instanceId),
        ascending(Datum::timestamp, Datum::subject / Subject::source),
        ascending(Datum::timestamp, Datum::subject / Subject::deviceManufacturer),
        ascending(Datum::timestamp, Datum::subject / Subject::deviceModel),
        ascending(Datum::timestamp, Datum::subject / Subject::deviceVersion),
        ascending(Datum::timestamp, Datum::subject / Subject::deviceOs),
        ascending(Datum::timestamp, Datum::subject / Subject::appId),
        ascending(Datum::timestamp, Datum::subject / Subject::appVersion)
    )

    val heartBeatsIndices = listOf(
        ascending(HeartBeat::dataStatus / DataStatus::datumType, HeartBeat::subject, HeartBeat::timestamp),
        ascending(HeartBeat::timestamp, HeartBeat::subject / Subject::groupName),
        ascending(HeartBeat::timestamp, HeartBeat::subject / Subject::email),
        ascending(HeartBeat::timestamp, HeartBeat::subject / Subject::hashedEmail),
        ascending(HeartBeat::timestamp, HeartBeat::subject / Subject::instanceId),
        ascending(HeartBeat::timestamp, HeartBeat::subject / Subject::source),
        ascending(HeartBeat::timestamp, HeartBeat::subject / Subject::deviceManufacturer),
        ascending(HeartBeat::timestamp, HeartBeat::subject / Subject::deviceModel),
        ascending(HeartBeat::timestamp, HeartBeat::subject / Subject::deviceVersion),
        ascending(HeartBeat::timestamp, HeartBeat::subject / Subject::deviceOs),
        ascending(HeartBeat::timestamp, HeartBeat::subject / Subject::appId),
        ascending(HeartBeat::timestamp, HeartBeat::subject / Subject::appVersion)
    )
}