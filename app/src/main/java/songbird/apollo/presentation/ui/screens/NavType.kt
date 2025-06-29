package songbird.apollo.presentation.ui.screens

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.json.Json
import songbird.apollo.presentation.model.SongPreviewUi
import kotlin.reflect.typeOf

object SongPreviewUiNavType : NavType<SongPreviewUi>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): SongPreviewUi? {
        return bundle.getString(key)?.let { Json.decodeFromString<SongPreviewUi>(it) }
    }

    override fun parseValue(value: String): SongPreviewUi {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: SongPreviewUi) {
        bundle.putString(key, Json.encodeToString(value))
    }
}

val navTypeMap = mapOf(typeOf<SongPreviewUi>() to SongPreviewUiNavType)