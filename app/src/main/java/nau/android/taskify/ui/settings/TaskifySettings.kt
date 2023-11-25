package nau.android.taskify.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nau.android.taskify.R
import nau.android.taskify.ui.extensions.noRippleClickable
import nau.android.taskify.ui.theme.TaskifyTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskifySettings() {

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.settings))
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
    }) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(start = 15.dp, end = 15.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .clickable { }
                .padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 5.dp)
                .fillMaxWidth()
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_avatar),
                    contentDescription = stringResource(id = R.string.avatar),
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "Bakkeldi Orozbekov",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                Appearance {

                }
                SoundsNotifications {

                }
                Spacer(modifier = Modifier.height(5.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .noRippleClickable {

                    }
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(vertical = 5.dp)
            ) {
                AboutSection {

                }
            }
            Spacer(modifier = Modifier.height(50.dp))
            OutlinedButton(
                onClick = { /*TODO*/ },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(0.dp, MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.sign_out),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun Appearance(onAppearanceClicked: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .noRippleClickable {

        }
        .padding(start = 15.dp, end = 15.dp, top = 15.dp, bottom = 15.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_appearance),
                contentDescription = stringResource(
                    id = R.string.appearance
                ),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = stringResource(id = R.string.appearance),
                fontWeight = FontWeight.Medium
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = stringResource(
                id = R.string.arrow_right
            )
        )
    }
}

@Composable
fun SoundsNotifications(onSoundsNotificationsClicked: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .noRippleClickable { }
            .padding(start = 15.dp, end = 15.dp, top = 15.dp, bottom = 15.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sounds),
                contentDescription = stringResource(
                    id = R.string.sounds_and_notifications
                ),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = stringResource(id = R.string.sounds_and_notifications),
                fontWeight = FontWeight.Medium
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = stringResource(
                id = R.string.arrow_right
            ), tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AboutSection(onAboutSectionClicked: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(
                    id = R.string.about
                ),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = stringResource(id = R.string.about),
                fontWeight = FontWeight.Medium
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = stringResource(
                id = R.string.arrow_right
            ), tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsRowPreview() {
    TaskifyTheme {
        Appearance {}
    }
}