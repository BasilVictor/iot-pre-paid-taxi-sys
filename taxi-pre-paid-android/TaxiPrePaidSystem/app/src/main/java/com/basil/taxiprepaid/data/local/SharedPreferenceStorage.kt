package com.basil.taxiprepaid.data.local

import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * Local Preference Storage for app
 */
interface PreferenceStorage {

    var loginFlag: Boolean
    var token: String
    var usertype: Int
    var latitude: Float
    var longitude: Float

    fun clear()

}

class SharedPreferenceStorage(private val prefs: SharedPreferences) : PreferenceStorage {

    override fun clear() {
        prefs.edit{
            clear()
            commit()
        }
    }
    override var loginFlag by BooleanPreference(prefs, PREF_LOGIN_FLAG, false)
    override var token by StringPreference(prefs, PREF_TOKEN, "")
    override var usertype by IntPreference(prefs, PREF_USERTYPE, 2)
    override var latitude by FloatPreference(prefs, PREF_LATITUDE, 0.0f)
    override var longitude by FloatPreference(prefs, PREF_LONGITUDE, 0.0f)

    companion object {
        const val PREFS_NAME = "taxi_pref"
        const val PREF_LOGIN_FLAG = "pref_login_flag"
        const val PREF_TOKEN = "pref_token"
        const val PREF_USERTYPE = "pref_type"
        const val PREF_LATITUDE = "latitude"
        const val PREF_LONGITUDE = "logitude"
    }

}

class BooleanPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.edit { putBoolean(name, value) }
    }
}

class IntPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Int
) : ReadWriteProperty<Any, Int> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return preferences.getInt(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        preferences.edit { putInt(name, value) }
    }
}


class LongPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Long
) : ReadWriteProperty<Any, Long> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return preferences.getLong(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        preferences.edit { putLong(name, value) }
    }
}

class StringPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: String
) : ReadWriteProperty<Any, String> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return preferences.getString(name, defaultValue)?:""
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        preferences.edit { putString(name, value) }
    }
}

class FloatPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Float
) : ReadWriteProperty<Any, Float> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Float {
        return preferences.getFloat(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Float) {
        preferences.edit { putFloat(name, value) }
    }
}