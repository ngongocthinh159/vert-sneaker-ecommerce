package com.rmit.ecommerce.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.filament.Material;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.ArCoreApk;

import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.sceneview.ar.ArSceneView;
import io.github.sceneview.ar.node.ArModelNode;
import io.github.sceneview.ar.node.PlacementMode;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArFragment extends Fragment {

    View view;
    public static ArSceneView sceneView;
    public static ArModelNode arModelNode;
    MaterialButton btnTakePicture;

    private boolean mUserRequestedInstall = true;

    String MODEL_3D_SNEAKER = "https://drive.google.com/uc?export=download&id=1_fpd3WLuY1brNZnGKegpJvoV6t_MYkJs";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ArFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArFragment newInstance(String param1, String param2) {
        ArFragment fragment = new ArFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ar, container, false);

        // Get refs
        sceneView = view.findViewById(R.id.sceneView);
        btnTakePicture = view.findViewById(R.id.btnTakePicture);

        // Create new AR model
        arModelNode = new ArModelNode(PlacementMode.PLANE_HORIZONTAL,
                ArModelNode.Companion.getDEFAULT_HIT_POSITION(),
                true,
                 false);
        arModelNode.setRotationEditable(true);

        arModelNode.loadModelAsync(MainActivity.context,
                null,
                MODEL_3D_SNEAKER,
                true,
                0.25f,
                null,
                null,
                null);

        // Add model to AR view
        sceneView.addChild(arModelNode);

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!haveWritePermission()) {
                    getWritePermission();
                    return;
                }

                try {
//                    Image image = sceneView.getCurrentFrame().getFrame().acquireCameraImage();
//                    WriteImageInformation(image, "abc");
//                    image.close();
                    takePhoto();

                } catch (/*NotYetAvailableException | IOException e*/ Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Back button setup
        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sceneView.removeChild(arModelNode);
                MainActivity.navController.popBackStack();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        requireInstallGoogleARIfNeeded();
    }

    private boolean haveWritePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else return true;
    }

    private void getWritePermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                209);
    }

    private void requireInstallGoogleARIfNeeded() {
        try {
            switch (ArCoreApk.getInstance().requestInstall(getActivity(), mUserRequestedInstall)) {
                case INSTALLED:
                    // Success: Safe to create the AR session.
                    break;
                case INSTALL_REQUESTED:
                    // When this method returns `INSTALL_REQUESTED`:
                    // 1. ARCore pauses this activity.
                    // 2. ARCore prompts the user to install or update Google Play
                    //    Services for AR (market://details?id=com.google.ar.core).
                    // 3. ARCore downloads the latest device profile data.
                    // 4. ARCore resumes this activity. The next invocation of
                    //    requestInstall() will either return `INSTALLED` or throw an
                    //    exception if the installation or update did not succeed.
                    mUserRequestedInstall = false;
            }
        } catch (UnavailableUserDeclinedInstallationException e) {
            // Display an appropriate message to the user and return gracefully.
            Toast.makeText(getContext(), "Failed to install Google Play Services AR", Toast.LENGTH_LONG).show();
            cleanArScene();
            MainActivity.navController.popBackStack();
        } catch (UnavailableDeviceNotCompatibleException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static void cleanArScene() {
        sceneView.removeChild(arModelNode);
        arModelNode = null;
        sceneView = null;
    }

    private String generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + date + "_screenshot.jpeg";
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {
        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }

    private void takePhoto(){
        final String filename = generateFilename();

        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(),
                Bitmap.Config.ARGB_8888);

        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();

        PixelCopy.request(sceneView, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename);
                    Toast.makeText(MainActivity.context, "Picture taken!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast toast = Toast.makeText(MainActivity.context, e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
            } else {
                Toast toast = Toast.makeText(MainActivity.context,
                        "Cannot save image: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }
}