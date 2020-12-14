package sensor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sensor.dto.SensorMeasurementDto;
import sensor.exception.EntityNotFoundException;
import sensor.repository.SensorMeasurement;
import sensor.repository.SensorMetrics;
import sensor.repository.SensorStatus;
import sensor.service.SensorMeasurementService;
import sensor.service.SensorStatusService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sensors")
public class SensorController {

    @Autowired
    SensorMeasurementService sensorMeasurementService;

    @Autowired
    SensorStatusService sensorStatusService;

    @PostMapping("/{uuid}/measurements")
    @ResponseStatus(value = HttpStatus.OK)
    public void saveMeasurements(@PathVariable String uuid, @RequestBody SensorMeasurementDto sensorMeasurementDto) {
       sensorMeasurementService.saveMeasurement(uuid, sensorMeasurementDto);
    }

    @GetMapping("/{uuid}")
    @ResponseBody
    public SensorStatus getStatus(@PathVariable String uuid) {
        SensorStatus sensorStatus = sensorStatusService.getStatus(uuid);

        if (sensorStatus == null)
            throw new EntityNotFoundException();

        return sensorStatus;
    }

    @GetMapping("/{uuid}/metrics")
    @ResponseBody
    public SensorMetrics getMetrics(@PathVariable String uuid) {
        SensorMetrics sensorMetrics = sensorMeasurementService.getMetrics(uuid);

        if (sensorMetrics == null)
            throw new EntityNotFoundException();

        return sensorMetrics;
    }
}
