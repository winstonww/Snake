package models.snake

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


class SegmentArrayDequeSerializer(
    private val serializer: KSerializer<Segment>) : KSerializer<ArrayDeque<Segment>>{
    val lSerializer = ListSerializer<Segment>(serializer)
    override val descriptor = lSerializer.descriptor

    override fun deserialize(decoder: Decoder): ArrayDeque<Segment> {
        val ls: List<Segment> = decoder.decodeSerializableValue(lSerializer)
        val arrayDeque = ArrayDeque<Segment>()
        arrayDeque.addAll(ls)
        return arrayDeque
    }

    override fun serialize(encoder: Encoder, value: ArrayDeque<Segment>) {
        encoder.encodeSerializableValue(lSerializer,value.toList())
    }

}
