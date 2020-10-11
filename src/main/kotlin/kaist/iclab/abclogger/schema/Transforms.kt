package kaist.iclab.abclogger.schema

import com.google.protobuf.Message as ProtoMessage

interface ProtoSerializer<O: Any, P: ProtoMessage> {
    fun toProto(o: O, isMd5Hashed: Boolean): P
}

interface ObjectSerializer<O: Any, P: ProtoMessage> {
    fun toObject(p: P): O
}

interface TwoWaySerializer<O: Any, P: ProtoMessage>: ProtoSerializer<O, P>, ObjectSerializer<O, P>
