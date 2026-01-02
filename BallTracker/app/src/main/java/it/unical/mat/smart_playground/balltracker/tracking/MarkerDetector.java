package it.unical.mat.smart_playground.balltracker.tracking;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.DetectorParameters;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by utente on 04/10/2020.
 */
public class MarkerDetector
{
    private static final int ARUCO_MARKER_DICT = Aruco.DICT_4X4_250;
    private static MarkerDetector instance = null;

    private final Dictionary dictionary;
    private final DetectorParameters parameters;

    public static MarkerDetector getInstance()
    {
        if ( instance == null )
            instance = new MarkerDetector();
        return instance;
    }

    private MarkerDetector()
    {
        dictionary = Aruco.getPredefinedDictionary(ARUCO_MARKER_DICT);
        parameters = DetectorParameters.create();
    }

    public List<Marker> detectMarkers( final Mat grayInputFrame  )
    {
        final List<Marker> detectedMarkers = new ArrayList<>();

        final List<Mat> corners = new LinkedList<>();
        final MatOfInt ids = new MatOfInt();

        Aruco.detectMarkers(grayInputFrame, dictionary, corners, ids);

        if ( !corners.isEmpty() )
        {
            final String cornersStr = corners.toString();
            final String idsStr = ids.toString();

            final int[] idsArray = ids.toArray();

            if ( corners.size() == idsArray.length )
            {
                int i = 0, j;
                for (final Mat corner : corners)
                {
                    final int cornerCoordsCount = corner.cols();
                    if (cornerCoordsCount != 4)
                        continue;

                    final Double[][] cornerCoords = new Double[cornerCoordsCount][2];
                    for ( j=0; j<cornerCoordsCount; ++j )
                        cornerCoords[j] = convertPrimitiveCoordsArray(corner.get(0, j));

                    final int cornerId = idsArray[i];
                    detectedMarkers.add( new Marker(cornerCoords, cornerId) );
                    ++i;
                }
            }

        }

        return detectedMarkers;
    }

    private static Double[] convertPrimitiveCoordsArray( final double[] coords )
    {
        final Double[] ans = new Double[coords.length];
        for ( int i=0; i<coords.length; ++i )
            ans[i] = coords[i];
        return ans;
    }
}
