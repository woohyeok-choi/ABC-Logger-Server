package kaist.iclab.abclogger.schema

import com.google.protobuf.Message as ProtoMessage

interface ProtoSerializer<O: Any, P: ProtoMessage> {
    fun toProto(o: O): P
    fun toObject(p: P): O
}
