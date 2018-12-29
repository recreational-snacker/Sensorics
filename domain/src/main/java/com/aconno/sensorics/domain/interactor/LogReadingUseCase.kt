package com.aconno.sensorics.domain.interactor

import com.aconno.sensorics.domain.FileStorage
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
//import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import io.reactivex.Completable
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LogReadingUseCase(
        private val fileStorage: FileStorage
) : CompletableUseCaseWithParameter<List<Reading>> {

//    override fun execute(parameter: List<Reading>): Completable {
//        for (reading in parameter) {
//            logReading(reading)
//        }
//        return Completable.complete()
//    }



    // @! Deesha Singh - Create a constant to convert nanoseconds to seconds.
    // private val GYRO_DPS_DIGIT_500DPS = 0.01750f
    private val NS2S = 1.0f / 1000000000.0f
    private val deltaRotationVector = FloatArray(4) { 0f }
    private var timestamp: Float = 0f

    // @! Deesha Singh - original calc - with event to be removed (added for reference)
    override fun onSensorChanged(event: SensorEvent?) {
        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0f && event != null) {
            val dT = (event.timestamp - timestamp) * NS2S
            // Axis of the rotation sample, not normalized yet.
            var axisX: Float = event.values[0]
            var axisY: Float = event.values[1]
            var axisZ: Float = event.values[2]

            // Calculate the angular speed of the sample
            val omegaMagnitude: Float = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)
            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude
                axisY /= omegaMagnitude
                axisZ /= omegaMagnitude
            }

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
        timestamp = event?.timestamp?.toFloat() ?: 0f
        val deltaRotationMatrix = FloatArray(9) { 0f }
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector)
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix
    }

    override fun execute(parameter: List<Reading>): Completable {
        for (reading in parameter) {
            logReading(reading)


            // @! Deesha Singh -  based on your original idea - is this still correct?
            if ("gyroscope z".equals(reading.name.toLowerCase())) {

                // @! Deesha Singh -  removed the event
                // Sensor Manager = problem
                // EPSILON = problem
                if (timestamp != 0f && event != null) {
                    val dT = (event.timestamp - timestamp) * NS2S
                    // Axis of the rotation sample, not normalized yet.
                    var axisX: Float = event.values[0]
                    var axisY: Float = event.values[1]
                    var axisZ: Float = event.values[2]

                    // Calculate the angular speed of the sample
                    val omegaMagnitude: Float = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)

                    // Normalize the rotation vector if it's big enough to get the axis
                    // (that is, EPSILON should represent your maximum allowable margin of error)
                    if (omegaMagnitude > EPSILON) {
                        axisX /= omegaMagnitude
                        axisY /= omegaMagnitude
                        axisZ /= omegaMagnitude
                    }

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
                timestamp = event?.timestamp?.toFloat() ?: 0f
                val deltaRotationMatrix = FloatArray(9) { 0f }
                SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector)
                // User code should concatenate the delta rotation we computed with the current rotation
                // in order to get the updated rotation.
                // rotationCurrent = rotationCurrent * deltaRotationMatrix;

            }
        }
        return Completable.complete()
    }

    fun subtract(a: Float, b: Float): Float {
        return a - b
    }

    private fun logReading(reading: Reading) {
        val fileName = "${reading.name.toLowerCase()}.csv"
        fileStorage.storeReading(reading, fileName)
    }
}