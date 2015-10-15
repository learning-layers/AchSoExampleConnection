package fi.legroup.aalto.achsoexampleconnection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.GeneralSecurityException;
import java.util.UUID;

// See app/build.gradle
// : compile 'fi.aalto.legroup:cryptohelper:0.1.0'
import fi.legroup.aalto.cryptohelper.CryptoHelper;

public class ExampleActivity extends ActionBarActivity {


    private EditText layersBoxUrlTextField;
    private Button saveButton;
    private Button openUuidButton;
    private Button openMp4Button;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        // Retrieve views.
        layersBoxUrlTextField = (EditText)findViewById(R.id.layers_box_url_text_field);
        saveButton = (Button)findViewById(R.id.save_button);
        openUuidButton = (Button)findViewById(R.id.open_uuid_button);
        openMp4Button = (Button)findViewById(R.id.open_mp4_button);

        // Load or create the _publicly visible_ shared preferences.
        String key = getString(R.string.layers_box_shared_preferences_key);
        int mode = Context.MODE_WORLD_READABLE;
        sharedPreferences = this.getSharedPreferences(key, mode);

        // Save the url from the text field when the button is clicked.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = layersBoxUrlTextField.getText().toString();
                saveLayersBoxUrl(url);
            }
        });

        // Open a sample mp4 url in Ach so when the button is clicked.
        openMp4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(getString(R.string.video_mp4_url));
                openAchSoWithMp4(uri);
            }
        });

        // Open a sample UUID in Ach so when the button is clicked.
        openUuidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID id = UUID.fromString(getString(R.string.video_uuid));
                openAchSoWithUUID(id);
            }
        });

        // Load the stored url to the text field on startup.
        String url = loadLayersBoxUrl();
        layersBoxUrlTextField.setText(url);
    }

    /**
     * Try to load the encrypted Layers Box URL from the shared preferences.
     */
    private String loadLayersBoxUrl() {
        // Default URL in case none is found/possible to decrypt
        String url = getString(R.string.example_layers_box_url);

        // Get the encrypted URL from the publicly visible shared preferences
        String urlEncrypted = sharedPreferences.getString("LAYERS_BOX_URL", null);

        if (urlEncrypted != null) {
            try {
                // Try to decrypt the stored layers box url with a key that is shared between the
                // integrated Learning Layers Android applications.
                String secret = getString(R.string.shared_preferences_secret_key);
                url = CryptoHelper.decrypt(urlEncrypted, secret);

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        return url;
    }

    /**
     * Try encrypt and tore the Layers Box URL to the shared preferences.
     */
    private void saveLayersBoxUrl(String layersBoxUrl) {

        try {
            // Try to encrypt the layers box url with a key that is shared between the integrated
            // Learning Layers Android applications.
            String secret = getString(R.string.shared_preferences_secret_key);
            String urlEncrypted = CryptoHelper.encrypt(layersBoxUrl, secret);

            // Store it in the publicly visible shared preferences if the encryption succeeded.
            sharedPreferences.edit()
                    .putString("LAYERS_BOX_URL", urlEncrypted)
                    .apply();

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            Toast.makeText(ExampleActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void openAchSoWithUUID(UUID id) {
        // The achso: -protocol can use the generic VIEW intent since no other apps respond to the
        // specific protocol.
        // Also the specific `fi.aalto.legroup.achso.action.VIEW` works.
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Open an AchSo URI (eg. achso:2de064a5-cb38-4c04-9fde-502ab619fc46).
        // This will search all the available videos for the ID and if found play it.
        // Does not need a MIME type.
        Uri uri = Uri.fromParts("achso", id.toString(), null);
        intent.setData(uri);

        startActivity(intent);
    }

    private void openAchSoWithMp4(Uri uri) {
        // Use the Ach so -specific intent for mp4, so that the picker doesn't open up.
        // Currently Ach so won't even open up implicit mp4 events, but it might be changed in the
        // future if that behavior is wanted.
        Intent intent = new Intent("fi.aalto.legroup.achso.action.VIEW");

        // Open an video URI (eg. example.com/video.mp4)
        // This will search all the available videos if they have the set video uri. If not found
        // query if the video is has public annotations and retrieve them.
        // Possible future feature: On opening a video stream without annotations, the user could
        // start annotating the video from scratch and upload the annotations.
        // Note: MIME type _must_ be set to `video/mp4`.
        intent.setDataAndType(uri, "video/mp4");

        startActivity(intent);
    }
}
