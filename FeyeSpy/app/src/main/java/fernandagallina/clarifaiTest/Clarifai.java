package fernandagallina.clarifaiTest;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.clarifai.api.exception.ClarifaiException;

import java.io.ByteArrayOutputStream;

/**
 * Created by fernanda on 09/09/16.
 */

public class Clarifai {

    private final CameraFragment cameraFragment;
    private static final String TAG = null;
    private final ClarifaiClient client = new ClarifaiClient(Credentials.CLIENT_ID, Credentials.CLIENT_SECRET);

    public Clarifai(CameraFragment cameraFragment) {
        this.cameraFragment = cameraFragment;
    }

    public void recognizePicture() {
        Bitmap bitmap = cameraFragment.textureView.getBitmap();
        if (bitmap != null) {
            cameraFragment.textView.setText("Recognizing...");
            cameraFragment.takePictureButton.setEnabled(false);

            // Run recognition on a background thread since it makes a network call.
            new AsyncTask<Bitmap, Void, RecognitionResult>() {
                @Override protected RecognitionResult doInBackground(Bitmap... bitmaps) {
                    return recognizeBitmap(bitmaps[0]);
                }
                @Override protected void onPostExecute(RecognitionResult result) {
                    updateUIForResult(result);
                }
            }.execute(bitmap);
        } else {
            cameraFragment.textView.setText("Unable to load selected image.");
        }
    }

    /** Sends the given bitmap to Clarifai for recognition and returns the result. */
    private RecognitionResult recognizeBitmap(Bitmap bitmap) {
        try {
            // Scale down the image. This step is optional. However, sending large images over the
            // network is slow and  does not significantly improve recognition performance.
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 320,
                    320 * bitmap.getHeight() / bitmap.getWidth(), true);

            // Compress the image as a JPEG.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, out);
            byte[] jpeg = out.toByteArray();

            // Send the JPEG to Clarifai and return the result.
            return client.recognize(new RecognitionRequest(jpeg)).get(0);
        } catch (ClarifaiException e) {
            Log.e(TAG, "Clarifai error", e);
            return null;
        }
    }

    /** Updates the UI by displaying tags for the given result. */
    private void updateUIForResult(RecognitionResult result) {
        if (result != null) {
            if (result.getStatusCode() == RecognitionResult.StatusCode.OK) {
                // Display the list of tags in the UI.
                StringBuilder b = new StringBuilder();
                for (Tag tag : result.getTags()) {
                    b.append(b.length() > 0 ? ", " : "").append(tag.getName());
                }
                cameraFragment.textView.setText("Tags:\n" + b);
            } else {
                Log.e(TAG, "Clarifai: " + result.getStatusMessage());
                cameraFragment.textView.setText("Sorry, there was an error recognizing your image.");
            }
        } else {
            cameraFragment.textView.setText("Sorry, there was an error recognizing your image.");
        }
        cameraFragment.takePictureButton.setEnabled(true);
    }
}

