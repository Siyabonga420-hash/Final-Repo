package com.example.rentmate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

private const val SETTINGS_PREFS = "rentmate_settings"
private const val ALARM_SOUND_URI = "alarm_sound_uri"
private const val NOTIFICATIONS_ENABLED = "notifications_enabled"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    val context = LocalContext.current

    val preferences = remember {
        context.getSharedPreferences(
            SETTINGS_PREFS,
            Context.MODE_PRIVATE
        )
    }

    var notificationsEnabled by remember {
        mutableStateOf(
            preferences.getBoolean(
                NOTIFICATIONS_ENABLED,
                true
            )
        )
    }

    var alarmSoundUri by remember {
        mutableStateOf(
            preferences.getString(
                ALARM_SOUND_URI,
                null
            )
        )
    }

    var showSoundDialog by remember {
        mutableStateOf(false)
    }

    /*
     * Android ringtone picker
     */
    val ringtonePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val uri: Uri? =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                        result.data?.getParcelableExtra(
                            RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
                            Uri::class.java
                        )

                    } else {

                        @Suppress("DEPRECATION")
                        result.data?.getParcelableExtra<Uri>(
                            RingtoneManager.EXTRA_RINGTONE_PICKED_URI
                        )
                    }

                if (uri != null) {

                    alarmSoundUri = uri.toString()

                    preferences.edit()
                        .putString(
                            ALARM_SOUND_URI,
                            uri.toString()
                        )
                        .apply()
                }
            }
        }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }

    ) { paddingValues ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            /*
             * SETTINGS HEADER
             */
            item {

                Text(
                    text = "App Settings",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Manage your RentMate preferences.",
                    fontSize = 14.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }

            /*
             * NOTIFICATIONS
             */
            item {

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme
                                    .surfaceVariant
                        )
                ) {

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(18.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Notifications,

                            contentDescription =
                                "Notifications",

                            modifier =
                                Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.size(14.dp)
                        )

                        Column(

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(

                                text =
                                    "Notifications",

                                fontSize =
                                    17.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(

                                text =
                                    "Turn RentMate notifications on or off.",

                                fontSize =
                                    13.sp,

                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                            )
                        }

                        Switch(

                            checked =
                                notificationsEnabled,

                            onCheckedChange = { enabled ->

                                notificationsEnabled =
                                    enabled

                                preferences.edit()
                                    .putBoolean(
                                        NOTIFICATIONS_ENABLED,
                                        enabled
                                    )
                                    .apply()
                            }
                        )
                    }
                }
            }

            /*
             * ALARM SOUND
             */
            item {

                Card(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                showSoundDialog = true
                            },

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme
                                    .surfaceVariant
                        )
                ) {

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(18.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Alarm,

                            contentDescription =
                                "Alarm Sound",

                            modifier =
                                Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.size(14.dp)
                        )

                        Column(

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(

                                text =
                                    "Alarm Sound",

                                fontSize =
                                    17.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(

                                text =
                                    getAlarmSoundName(
                                        context,
                                        alarmSoundUri
                                    ),

                                fontSize =
                                    13.sp,

                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                            )
                        }
                    }
                }
            }

            /*
             * ANDROID NOTIFICATION SETTINGS
             */
            item {

                Card(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {

                                val intent =
                                    Intent(
                                        Settings
                                            .ACTION_APP_NOTIFICATION_SETTINGS
                                    ).apply {

                                        putExtra(
                                            Settings
                                                .EXTRA_APP_PACKAGE,
                                            context.packageName
                                        )
                                    }

                                context.startActivity(intent)
                            },

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme
                                    .surfaceVariant
                        )
                ) {

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(18.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Settings,

                            contentDescription =
                                "Android Settings",

                            modifier =
                                Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.size(14.dp)
                        )

                        Column(

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(

                                text =
                                    "Android Notification Settings",

                                fontSize =
                                    17.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(

                                text =
                                    "Manage RentMate notification permissions and settings.",

                                fontSize =
                                    13.sp,

                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                            )
                        }
                    }
                }
            }

            /*
             * LOGOUT
             */
            item {

                Divider()

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Button(

                    onClick =
                        onLogoutClick,

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(

                        text =
                            "Logout",

                        fontWeight =
                            FontWeight.Bold
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )
            }
        }
    }

    /*
     * ALARM SOUND DIALOG
     */
    if (showSoundDialog) {

        AlertDialog(

            onDismissRequest = {

                showSoundDialog = false
            },

            title = {

                Text(

                    text =
                        "Choose Alarm Sound",

                    fontWeight =
                        FontWeight.Bold
                )
            },

            text = {

                Column(

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    /*
                     * DEFAULT ALARM
                     */
                    SoundChoiceRow(

                        title =
                            "Default Alarm",

                        selected =
                            alarmSoundUri ==
                                    RingtoneManager
                                        .getDefaultUri(
                                            RingtoneManager
                                                .TYPE_ALARM
                                        )
                                        .toString(),

                        onClick = {

                            val uri =
                                RingtoneManager
                                    .getDefaultUri(
                                        RingtoneManager
                                            .TYPE_ALARM
                                    )

                            alarmSoundUri =
                                uri.toString()

                            preferences.edit()
                                .putString(
                                    ALARM_SOUND_URI,
                                    uri.toString()
                                )
                                .apply()

                            showSoundDialog =
                                false
                        }
                    )

                    /*
                     * DEFAULT NOTIFICATION
                     */
                    SoundChoiceRow(

                        title =
                            "Default Notification",

                        selected =
                            alarmSoundUri ==
                                    RingtoneManager
                                        .getDefaultUri(
                                            RingtoneManager
                                                .TYPE_NOTIFICATION
                                        )
                                        .toString(),

                        onClick = {

                            val uri =
                                RingtoneManager
                                    .getDefaultUri(
                                        RingtoneManager
                                            .TYPE_NOTIFICATION
                                    )

                            alarmSoundUri =
                                uri.toString()

                            preferences.edit()
                                .putString(
                                    ALARM_SOUND_URI,
                                    uri.toString()
                                )
                                .apply()

                            showSoundDialog =
                                false
                        }
                    )

                    /*
                     * DEFAULT RINGTONE
                     */
                    SoundChoiceRow(

                        title =
                            "Default Ringtone",

                        selected =
                            alarmSoundUri ==
                                    RingtoneManager
                                        .getDefaultUri(
                                            RingtoneManager
                                                .TYPE_RINGTONE
                                        )
                                        .toString(),

                        onClick = {

                            val uri =
                                RingtoneManager
                                    .getDefaultUri(
                                        RingtoneManager
                                            .TYPE_RINGTONE
                                    )

                            alarmSoundUri =
                                uri.toString()

                            preferences.edit()
                                .putString(
                                    ALARM_SOUND_URI,
                                    uri.toString()
                                )
                                .apply()

                            showSoundDialog =
                                false
                        }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    /*
                     * CHOOSE FROM PHONE
                     */
                    Button(

                        onClick = {

                            val intent =
                                Intent(
                                    RingtoneManager
                                        .ACTION_RINGTONE_PICKER
                                ).apply {

                                    putExtra(

                                        RingtoneManager
                                            .EXTRA_RINGTONE_TYPE,

                                        RingtoneManager
                                            .TYPE_ALARM
                                    )

                                    putExtra(

                                        RingtoneManager
                                            .EXTRA_RINGTONE_TITLE,

                                        "Choose Alarm Sound"
                                    )

                                    putExtra(

                                        RingtoneManager
                                            .EXTRA_RINGTONE_SHOW_SILENT,

                                        false
                                    )

                                    if (
                                        alarmSoundUri != null
                                    ) {

                                        putExtra(

                                            RingtoneManager
                                                .EXTRA_RINGTONE_EXISTING_URI,

                                            Uri.parse(
                                                alarmSoundUri
                                            )
                                        )
                                    }
                                }

                            ringtonePickerLauncher
                                .launch(intent)

                            showSoundDialog =
                                false
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text =
                                "Choose From Phone"
                        )
                    }
                }
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        showSoundDialog =
                            false
                    }
                ) {

                    Text(
                        text =
                            "Close"
                    )
                }
            }
        )
    }
}

/*
 * SOUND OPTION ROW
 */
@Composable
private fun SoundChoiceRow(

    title: String,

    selected: Boolean,

    onClick: () -> Unit

) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(
                    vertical = 6.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        RadioButton(

            selected =
                selected,

            onClick =
                onClick
        )

        Spacer(
            modifier =
                Modifier.size(8.dp)
        )

        Text(

            text =
                title,

            fontSize =
                15.sp
        )
    }
}

/*
 * GET SELECTED SOUND NAME
 */
private fun getAlarmSoundName(

    context: Context,

    uriString: String?

): String {

    if (
        uriString.isNullOrEmpty()
    ) {

        return "Default alarm sound"
    }

    return try {

        val uri =
            Uri.parse(
                uriString
            )

        val ringtone =
            RingtoneManager.getRingtone(
                context,
                uri
            )

        ringtone?.getTitle(
            context
        ) ?: "Selected alarm sound"

    } catch (
        e: Exception
    ) {

        "Selected alarm sound"
    }
}

/*
 * PREVIEW
 */
@Preview(
    showBackground = true
)
@Composable
fun SettingsScreenPreview() {

    MaterialTheme {

        SettingsScreen(

            onBackClick = {},

            onLogoutClick = {}
        )
    }
}