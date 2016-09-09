package fernandagallina.clarifaiTest;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.util.Log;
import android.util.Size;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

/**
 * Created by fernanda on 08/09/16.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraFragment extends Fragment {

    Camera2 camera2;
    public TextureView textureView;
    private CameraDevice mCameraDevice;
    public Button takePictureButton;
    public TextView textView;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.camera_fragment, container, false);

        final Clarifai clarifai = new Clarifai(this);
        textureView = (TextureView) rootView.findViewById(R.id.texture);
        textureView.setSurfaceTextureListener(mSurfaceTextureListener);

        textView = (TextView) rootView.findViewById(R.id.textView);

        camera2 = new Camera2(textureView);

        takePictureButton = (Button) rootView.findViewById(R.id.button);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera2.takePicture(getActivity(), getActivity().getWindowManager());
                clarifai.recognizePicture();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }



    @Override
    public void onPause() {

        Log.e(TAG, "onPause");
        super.onPause();
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener(){

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.e(TAG, "onSurfaceTextureAvailable, width="+width+",height="+height);
            camera2.openCamera(getActivity());
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {
            Log.e(TAG, "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            //Log.e(TAG, "onSurfaceTextureUpdated");
        }

    };
}
