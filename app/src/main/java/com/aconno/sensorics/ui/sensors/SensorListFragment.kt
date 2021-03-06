package com.aconno.sensorics.ui.sensors

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.aconno.sensorics.R
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.ui.LiveGraphActivity
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.viewmodel.SensorListViewModel
import kotlinx.android.synthetic.main.fragment_sensor_list.*
import kotlinx.android.synthetic.main.view_sensor_card.view.*
import javax.inject.Inject

class SensorListFragment : Fragment() {

    private var macAddress: String = ""

    @Inject
    lateinit var sensorListViewModel: SensorListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivity: MainActivity? = activity as MainActivity
        mainActivity?.mainActivityComponent?.inject(this)

        setHasOptionsMenu(true)

        arguments?.let {
            macAddress = it.getString(MAC_ADDRESS_EXTRA)
            sensorListViewModel.setMacAddress(macAddress)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        activity?.menuInflater?.inflate(R.menu.menu_readings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        context?.let { context ->
            when (item.itemId) {
                R.id.action_start_actions_activity -> {
                    ActionListActivity.start(context)
                    return true
                }
                else -> {
                    //Do nothing
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        sensorListViewModel.getSensorValuesLiveData()
            .observe(this, Observer { displaySensorValues(it) })
        initValues()

        val mainActivity: MainActivity? = context as MainActivity
        mainActivity?.supportActionBar?.title = getDeviceName()
        mainActivity?.supportActionBar?.subtitle = macAddress
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sensor_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setClickListeners()
    }

    private fun setClickListeners() {
        context?.let { context ->
            sensor_temperature.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Temperature")
            }
            sensor_light.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Light")
            }
            sensor_humidity.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Humidity")
            }
            sensor_pressure.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Pressure")
            }
            sensor_magnetometer_x.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Magnetometer")
            }
            sensor_magnetometer_y.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Magnetometer")
            }
            sensor_magnetometer_z.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Magnetometer")
            }
            sensor_accelerometer_x.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Accelerometer")
            }
            sensor_accelerometer_y.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Accelerometer")
            }
            sensor_accelerometer_z.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Accelerometer")
            }
            sensor_gyroscope_x.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Gyroscope")
            }
            sensor_gyroscope_y.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Gyroscope")
            }
            sensor_gyroscope_z.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Gyroscope")
            }
            sensor_battery_level.setOnClickListener {
                LiveGraphActivity.start(context, macAddress, "Battery Level")
            }
        }
    }

    private fun initView() {
        sensor_temperature.icon.setImageResource(R.drawable.ic_temperature)
        sensor_temperature.name.text = getString(R.string.temperature)

        sensor_light.icon.setImageResource(R.drawable.ic_light)
        sensor_light.name.text = getString(R.string.light)

        sensor_humidity.icon.setImageResource(R.drawable.ic_humidity)
        sensor_humidity.name.text = getString(R.string.humidity)

        sensor_pressure.icon.setImageResource(R.drawable.ic_pressure)
        sensor_pressure.name.text = getString(R.string.pressure)

        sensor_magnetometer_x.icon.setImageResource(R.drawable.ic_compass)
        sensor_magnetometer_x.name.text = getString(R.string.magnetometer_x)

        sensor_magnetometer_y.icon.setImageResource(R.drawable.ic_compass)
        sensor_magnetometer_y.name.text = getString(R.string.magnetometer_y)

        sensor_magnetometer_z.icon.setImageResource(R.drawable.ic_compass)
        sensor_magnetometer_z.name.text = getString(R.string.magnetometer_z)

        sensor_accelerometer_x.icon.setImageResource(R.drawable.ic_acc)
        sensor_accelerometer_x.name.text = getString(R.string.accelerometer_x)

        sensor_accelerometer_y.icon.setImageResource(R.drawable.ic_acc)
        sensor_accelerometer_y.name.text = getString(R.string.accelerometer_y)

        sensor_accelerometer_z.icon.setImageResource(R.drawable.ic_acc)
        sensor_accelerometer_z.name.text = getString(R.string.accelerometer_z)

        sensor_gyroscope_x.icon.setImageResource(R.drawable.ic_gyro)
        sensor_gyroscope_x.name.text = getString(R.string.gyro_x)

        sensor_gyroscope_y.icon.setImageResource(R.drawable.ic_gyro)
        sensor_gyroscope_y.name.text = getString(R.string.gyro_y)

        sensor_gyroscope_z.icon.setImageResource(R.drawable.ic_gyro)
        sensor_gyroscope_z.name.text = getString(R.string.gyro_z)

        sensor_battery_level.icon.setImageResource(R.drawable.ic_pressure)
        sensor_battery_level.name.text = getString(R.string.battery_level)

        initValues()
    }

    private fun initValues() {
        sensor_temperature.value.text = getString(R.string.default_temperature)
        sensor_light.value.text = getString(R.string.default_light)
        sensor_humidity.value.text = getString(R.string.default_humidity)
        sensor_pressure.value.text = getString(R.string.default_pressure)
        sensor_magnetometer_x.value.text = getString(R.string.default_magnetometer_x)
        sensor_magnetometer_y.value.text = getString(R.string.default_magnetometer_y)
        sensor_magnetometer_z.value.text = getString(R.string.default_magnetometer_z)
        sensor_accelerometer_x.value.text = getString(R.string.default_accelerometer_x)
        sensor_accelerometer_y.value.text = getString(R.string.default_accelerometer_y)
        sensor_accelerometer_z.value.text = getString(R.string.default_accelerometer_z)
        sensor_gyroscope_x.value.text = getString(R.string.default_gyro_x)
        sensor_gyroscope_y.value.text = getString(R.string.default_gyro_y)
        sensor_gyroscope_z.value.text = getString(R.string.default_gyro_z)
        sensor_battery_level.value.text = getString(R.string.default_battery_level)
    }

    private fun displaySensorValues(values: Map<String, Number>?) {
        values?.let {
            val temperatureLabel = getString(R.string.temperature)
            val lightLabel = getString(R.string.light)
            val humidityLabel = getString(R.string.humidity)
            val pressureLabel = getString(R.string.pressure)
            val magnetoXLabel = getString(R.string.magnetometer_x)
            val magnetoYLabel = getString(R.string.magnetometer_y)
            val magnetoZLabel = getString(R.string.magnetometer_z)
            val accelerometerXLabel = getString(R.string.accelerometer_x)
            val accelerometerYLabel = getString(R.string.accelerometer_y)
            val accelerometerZLabel = getString(R.string.accelerometer_z)
            val gyroXLabel = getString(R.string.gyro_x)
            val gyroYLabel = getString(R.string.gyro_y)
            val gyroZLabel = getString(R.string.gyro_z)
            val batteryLabel = getString(R.string.battery_level)

            val temperature = values["Temperature"]
            val light = values["Light"]
            val humidity = values["Humidity"]
            val pressure = values["Pressure"]
            val magnetometerX = values["Magnetometer X"]
            val magnetometerY = values["Magnetometer Y"]
            val magnetometerZ = values["Magnetometer Z"]
            val accelerometerX = values["Accelerometer X"]
            val accelerometerY = values["Accelerometer Y"]
            val accelerometerZ = values["Accelerometer Z"]
            val gyroscopeX = values["Gyroscope X"]
            val gyroscopeY = values["Gyroscope Y"]
            val gyroscopeZ = values["Gyroscope Z"]
            val batteryLevel = values["Battery Level"]

            temperature?.let {
                sensor_temperature.update(
                    temperatureLabel,
                    "${String.format("%.2f", temperature.toFloat())}°C"
                )
            }

            light?.let {
                sensor_light.update(
                    lightLabel,
                    "${String.format("%.2f", light.toFloat())}%"
                )
            }

            humidity?.let {
                sensor_humidity.update(
                    humidityLabel,
                    "${String.format("%.2f", humidity.toFloat())}%"
                )
            }

            pressure?.let {
                sensor_pressure.update(
                    pressureLabel,
                    "${String.format("%.2f", pressure.toFloat())}hPa"
                )
            }

            magnetometerX?.let {
                sensor_magnetometer_x.update(
                    magnetoXLabel,
                    "${String.format("%.2f", magnetometerX.toFloat())}µT"
                )
            }
            magnetometerY?.let {
                sensor_magnetometer_y.update(
                    magnetoYLabel,
                    "${String.format("%.2f", magnetometerY.toFloat())}µT"
                )
            }
            magnetometerZ?.let {
                sensor_magnetometer_z.update(
                    magnetoZLabel,
                    "${String.format("%.2f", magnetometerZ.toFloat())}µT"
                )
            }

            accelerometerX
                ?.let {
                    sensor_accelerometer_x.update(
                        accelerometerXLabel,
                        "${String.format("%.2f", accelerometerX.toFloat())}mg"
                    )
                }
            accelerometerY
                ?.let {
                    sensor_accelerometer_y.update(
                        accelerometerYLabel,
                        "${String.format("%.2f", accelerometerY.toFloat())}mg"
                    )
                }
            accelerometerZ
                ?.let {
                    sensor_accelerometer_z.update(
                        accelerometerZLabel,
                        "${String.format("%.2f", accelerometerZ.toFloat())}mg"
                    )
                }

            gyroscopeX?.let {
                sensor_gyroscope_x.update(
                    gyroXLabel,
                    "${String.format("%.2f", gyroscopeX.toFloat())}dps"
                )
            }
            gyroscopeY?.let {
                sensor_gyroscope_y.update(
                    gyroYLabel,
                    "${String.format("%.2f", gyroscopeY.toFloat())}dps"
                )
            }
            gyroscopeZ?.let {
                sensor_gyroscope_z.update(
                    gyroZLabel,
                    "${String.format("%.2f", gyroscopeZ.toFloat())}dps"
                )
            }

            batteryLevel?.let { sensor_battery_level.update(batteryLabel, "$batteryLevel%") }
        }
    }

    private fun getDeviceName(): String {
        arguments?.let {
            return it.getString(DEVICE_NAME_EXTRA) ?: ""
        }
        return ""
    }


    companion object {

        private const val MAC_ADDRESS_EXTRA = "mac_address"
        private const val DEVICE_NAME_EXTRA = "device_name"

        fun newInstance(macAddress: String, deviceName: String): SensorListFragment {
            val bundle = Bundle()
            bundle.putString(MAC_ADDRESS_EXTRA, macAddress)
            bundle.putString(DEVICE_NAME_EXTRA, deviceName)
            val fragment = SensorListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}