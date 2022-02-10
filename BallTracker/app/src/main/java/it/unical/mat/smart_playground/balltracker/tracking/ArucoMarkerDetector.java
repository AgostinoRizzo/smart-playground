package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.aruco.Aruco;
import org.opencv.aruco.DetectorParameters;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by utente on 04/10/2020.
 */
public class ArucoMarkerDetector
{
    private static final int ARUCO_MARKER_DICT = Aruco.DICT_4X4_250;
    private static final int[] DETECTABLE_ARUCO_IDS = { 0, 1, 2, 3, 4 };
    private static ArucoMarkerDetector instance = null;

    private final Dictionary dictionary;
    private final DetectorParameters parameters;

    public static ArucoMarkerDetector getInstance()
    {
        if ( instance == null )
            instance = new ArucoMarkerDetector();
        return instance;
    }

    private ArucoMarkerDetector()
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
                    if ( isDetectableId(cornerId) )
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

    private static boolean isDetectableId( final int id )
    {
        for ( int i=0; i<DETECTABLE_ARUCO_IDS.length; ++i )
            if ( id == DETECTABLE_ARUCO_IDS[i] )
                return true;
        return false;
    }
}
