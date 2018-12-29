package com.aconno.sensorics

import android.hardware.SensorManager
import com.aconno.sensorics.domain.interactor.LogReadingUseCase
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.sensorics.domain.model.Reading
import io.reactivex.Completable
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class SensorAnalyser(
        private val logReadingUseCase: LogReadingUseCase
) : CompletableUseCaseWithParameter<List<Reading>> {

    // @! Deesha Singh - Create a constant to convert nanoseconds to seconds.
    // private val GYRO_DPS_DIGIT_500DPS = 0.01750f //TODO figure out what to do with this
    private val NS2S = 1.0f / 1000000000.0f
    private val deltaRotationVector = FloatArray(4) { 0f }
    private var timestamp: Float = 0f
    private val angularDisplFileName = "Angular Displacement"

    override fun execute(parameter: List<Reading>): Completable {
        logReadingUseCase.execute(parameter) //Log all the readings first
        for (reading in parameter) {
            // @! Deesha Singh -  based on your original idea - is this still correct?  @! Duvan De Koker the readings are individual to the
            //gyroscope axis in this case, not sure how we'll get a full set of data (would have to match by timestamp or something?)
            if ("gyroscope z".equals(reading.name.toLowerCase())) {
                // EPSILON = problem  @! Duvan De Koker yep, dunno what the margin of error should be
                if (timestamp != 0f && reading.timestamp != null) {
                    val dT = (reading.timestamp - timestamp) * NS2S
                    // Axis of the rotation sample, not normalized yet.
                    var axisX: Float = 0f
                    var axisY: Float = 0f
                    var axisZ: Float = reading.value.toFloat()

                    // Calculate the angular speed of the sample
                    val omegaMagnitude: Float = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)

                    // Normalize the rotation vector if it's big enough to get the axis
                    // (that is, EPSILON should represent your maximum allowable margin of error)
                    //TODO figure out this margin of error? Experimentation?
//                    if (omegaMagnitude > EPSILON) {
//                        axisX /= omegaMagnitude
//                        axisY /= omegaMagnitude
//                        axisZ /= omegaMagnitude
//                    }

                    // Integrate around this axis with the angular speed by the timestep
                    // in order to get a delta rotation from this sample over the timestep
                    // We will convert this axis-angle representation of the delta rotation
                    // into a quaternion before turning it into the rotation matrix.
                    val thetaOverTwo: Float = omegaMagnitude * dT / 2.0f
                    val sinThetaOverTwo: Float = sin(thetaOverTwo)
                    val cosThetaOverTwo: Float = cos(thetaOverTwo)
                    deltaRotationVector[0] = sinThetaOverTwo * axisX
                    deltaRotationVector[1] = sinThetaOverTwo * axisY
                    deltaRotationVector[2] = sinThetaOverTwo * axisZ
                    deltaRotationVector[3] = cosThetaOverTwo
                }
                timestamp = reading?.timestamp?.toFloat() ?: 0f
                val deltaRotationMatrix = FloatArray(9) { 0f }
                SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector)
                // User code should concatenate the delta rotation we computed with the current rotation
                // in order to get the updated rotation.
                // rotationCurrent = rotationCurrent * deltaRotationMatrix;
                //TODO calc angle change with previous rotation matrix - where to store this so it can be retrieved?
                val angleChange = FloatArray(3) { 0f }
                val previousDeltaRotationMatrix = FloatArray(9) { 0f }
                SensorManager.getAngleChange(angleChange, deltaRotationMatrix, previousDeltaRotationMatrix)
                val deltaZ: Float = angleChange[2]
                val currentZ: Float = deltaZ// + previousZ //@! Duvan still in the issue with getting the previous dataset
                val angle = Reading(reading.timestamp, reading.device, currentZ, angularDisplFileName)
                val angleList: MutableList<Reading> = mutableListOf(angle)
                logReadingUseCase.execute(angleList)
            }
        }
        return Completable.complete()
    }
}