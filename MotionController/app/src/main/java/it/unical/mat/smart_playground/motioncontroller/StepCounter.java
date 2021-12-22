package it.unical.mat.smart_playground.motioncontroller;

import android.hardware.SensorManager;

/**
 * Created by utente on 21/12/2021.
 */
public class StepCounter
{
    private static final int h = 480;

    private float   mLimit = 10f;   // sensitivity
    private float   mLastValues[] = new float[3*2];
    private float   mScale[] = new float[2];
    private float   mYOffset;

    private float   mLastDirections[] = new float[3*2];
    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
    private float   mLastDiff[] = new float[3*2];
    private int     mLastMatch = -1;

    private int steps = 0;

    public StepCounter()
    {
        mYOffset = h * 0.5f;
        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    public int getSteps()
    {
        return steps;
    }

    public int onAccelValues( final float[] values )
    {
        float vSum = 0;
        for (int i=0 ; i<3 ; i++) {
            final float v = mYOffset + values[i] * mScale[1];
            vSum += v;
        }
        int k = 0;
        float v = vSum / 3;

        float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
        if (direction == - mLastDirections[k]) {

            // direction changed
            int extType = (direction > 0 ? 0 : 1);
            mLastExtremes[extType][k] = mLastValues[k];
            float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

            if (diff > mLimit) {

                boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
                boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
                boolean isNotContra = (mLastMatch != 1 - extType);

                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                    ++steps;
                    mLastMatch = extType;
                }
                else {
                    mLastMatch = -1;
                }
            }
            mLastDiff[k] = diff;
        }
        mLastDirections[k] = direction;
        mLastValues[k] = v;

        return steps;
    }
}
