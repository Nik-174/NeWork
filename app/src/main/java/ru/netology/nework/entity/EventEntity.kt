package ru.netology.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.enumeration.EventType
import ru.netology.nework.dto.Coordinates
import ru.netology.nework.dto.EventResponse
import ru.netology.nework.dto.ListIds

@Entity
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates?,
    val type: EventType,
    val likeOwnerIds: ListIds,
    val likedByMe: Boolean,
    val speakerIds: ListIds,
    val participantsIds: ListIds,
    val participatedByMe: Boolean,
    @Embedded
    val attachment: AttachmentEmbedded?,
    val link: String?,
    val ownerByMe: Boolean,


    ) {

    fun toDto() = EventResponse(id, authorId, author, authorAvatar, authorJob, content,
        datetime, published, coords,type, likeOwnerIds.list,likedByMe, speakerIds.list,
            participantsIds.list, participatedByMe, attachment?.toDto(), link, ownerByMe)

        companion object {
            fun fromDto(dto: EventResponse) =
                EventEntity(dto.id, dto.authorId, dto.author, dto.authorAvatar, dto.authorJob,
                    dto.content, dto.datetime, dto.published, dto.coords,
                    dto.type, ListIds(dto.likeOwnerIds), dto.likedByMe,
                    ListIds(dto.speakerIds), ListIds(dto.participantsIds),
                    dto.participatedByMe, AttachmentEmbedded.fromDto(dto.attachment),
                    dto.link, dto.ownerByMe)

            fun fromDtoFlow(dto: EventResponse) =
                EventEntity(dto.id, dto.authorId, dto.author, dto.authorAvatar, dto.authorJob,
                    dto.content, dto.datetime, dto.published, dto.coords,
                    dto.type, ListIds(dto.likeOwnerIds), dto.likedByMe,
                    ListIds(dto.speakerIds), ListIds(dto.participantsIds),
                    dto.participatedByMe, AttachmentEmbedded.fromDto(dto.attachment),
                    dto.link, dto.ownerByMe)
        }
    }

//data class TypeEventEmbeddable()

    fun List<EventEntity>.toDto(): List<EventResponse> = map(EventEntity::toDto)
    fun List<EventResponse>.toEntity(): List<EventEntity> = map(EventEntity.Companion::fromDto)
    fun List<EventResponse>.toEntityFlow(): List<EventEntity> = map(EventEntity.Companion::fromDtoFlow)