package ru.netology.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.enumeration.AttachmentType
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Coordinates
import ru.netology.nework.dto.ListIds
import ru.netology.nework.dto.PostResponse

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val published: String,
    val coords: Coordinates?,
    val link: String?,
    val likeOwnerIds: ListIds,
    val mentionIds: ListIds,
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    @Embedded
    val attachment: AttachmentEmbedded?,
    val ownerByMe: Boolean,
) {
    fun toDto() = PostResponse(
        id, authorId, author, authorAvatar, authorJob, content,
        published, coords, link, likeOwnerIds.list, mentionIds.list, mentionedMe,
        likedByMe, attachment?.toDto(), ownerByMe
    )

    companion object {
        fun fromDto(dto: PostResponse) =
            PostEntity(
                dto.id, dto.authorId, dto.author, dto.authorAvatar, dto.authorJob,
                dto.content, dto.published, dto.coords,
                dto.link, ListIds(dto.likeOwnerIds), ListIds(dto.mentionIds), dto.mentionedMe,
                dto.likedByMe, AttachmentEmbedded.fromDto(dto.attachment), dto.ownerByMe
            )
    }
}

data class AttachmentEmbedded(
    var url: String,
    var typeAttach: AttachmentType,
) {
    fun toDto() = Attachment(url, typeAttach)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbedded(it.url, it.type)
        }
    }
}

fun List<PostResponse>.toEntity(): List<PostEntity> = map(PostEntity.Companion::fromDto)