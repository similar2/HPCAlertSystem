package edu.sustech.hpc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.sustech.hpc.annotation.PathController;
import edu.sustech.hpc.model.param.DeviceParam;
import edu.sustech.hpc.result.ApiResponse;
import edu.sustech.hpc.po.*;
import edu.sustech.hpc.service.AlertService;
import edu.sustech.hpc.service.DatabaseService;
import edu.sustech.hpc.service.DeviceService;
import jakarta.annotation.Resource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@PathController("/api/devices")
public class DeviceController {

    @Resource
    private DeviceService deviceService;
    @Resource
    private AlertService alertService;

    @Resource
    private DatabaseService databaseService;

    @GetMapping
    public ApiResponse<List<DeviceParam>> all() {
        List<DeviceParam> devices = deviceService.all();
        List<Alert> alerts = alertService.getAll(
                null, null, null, Boolean.FALSE);
        List<String> deviceNames = new ArrayList<>();
        for(Alert alert : alerts)
            deviceNames.add(alert.getDeviceName());
        for(DeviceParam device : devices) {
            if(device.other == null) {
                ObjectMapper objectMapper = new ObjectMapper();
                device.other = objectMapper.createObjectNode();
            }
            if(deviceNames.contains(device.name)) {
                ((ObjectNode)device.other).put("alert", Boolean.TRUE);
            }
        }
        return ApiResponse.success(devices);
    }

    @GetMapping("/{id}")
    public ApiResponse<DeviceParam> get(@PathVariable Integer id) {
        DeviceParam device = deviceService.get(id);
        return ApiResponse.success(device);
    }

    @PostMapping
    public ApiResponse<DeviceParam> add(@Validated @RequestBody DeviceParam deviceParam) {
        DeviceParam deviceVo = deviceService.add(deviceParam);
        return ApiResponse.success(deviceVo);
    }

    @GetMapping("/assignees/get")
    public ApiResponse<List<Assignee>> getAssignees(
            @RequestParam(name = "device_id", required = false) Integer deviceId
    ) {
        return ApiResponse.success(deviceService.getAssignees(deviceId));
    }

    @PostMapping("/assignees/add/{deviceId}")
    public ApiResponse addAssignees(
            @PathVariable Integer deviceId,
            @RequestBody ArrayNode userIdList
            ) {
        for(JsonNode objNode : userIdList) {
            Integer userId = objNode.asInt();
            deviceService.addAssignee(new Assignee(deviceId, userId));
        }
        return ApiResponse.success();
    }

    @PostMapping("/assignees/delete/{deviceId}")
    public ApiResponse deleteAssignees(
            @PathVariable Integer deviceId,
            @RequestBody ArrayNode userIdList
    ) {
        for(JsonNode objNode : userIdList) {
            Integer userId = objNode.asInt();
            deviceService.deleteAssignee(new Assignee(deviceId, userId));
        }
        return ApiResponse.success();
    }

    @PatchMapping("/{id}")
    public ApiResponse modify(@PathVariable Integer id, @Validated @RequestBody DeviceParam deviceParam) {
        DeviceParam deviceVo = deviceService.modify(id, deviceParam);
        return ApiResponse.success(deviceVo);
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        deviceService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/file")
    public ApiResponse handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workBook = new XSSFWorkbook(inputStream);
        XSSFSheet workSheet = workBook.getSheetAt(0);
        Row headerRow = workSheet.getRow(0);
        Map<String, Integer> headerIndex = new HashMap<>();
        Iterator<Cell> headerRowCellIt = headerRow.cellIterator();
        while(headerRowCellIt.hasNext()) {
            Cell cell = headerRowCellIt.next();
            headerIndex.put(cell.getStringCellValue(), cell.getColumnIndex());
        }
        List<Cluster> clusterList = databaseService.getClusterInfo();
        Map<String, Integer> clusterMap = new HashMap<>();
        for(Cluster cluster : clusterList) {
            clusterMap.put(cluster.getName(), cluster.getId());
        }
        for (int i = workSheet.getFirstRowNum() + 1;
             i < workSheet.getLastRowNum() + 1; i ++) {
            Row row = workSheet.getRow(i);
            ObjectMapper mapper = new ObjectMapper();
            Cell otherCell = row.getCell(headerIndex.get("other"));
            String otherString = otherCell == null ? "{}" :
                    otherCell.getStringCellValue();
            JsonNode other = mapper.readTree(otherString);
            DeviceParam deviceParam =
                    DeviceParam.builder()
                            .name(row.getCell(headerIndex.get("name")).getStringCellValue())
                            .position(row.getCell(headerIndex.get("position")).getStringCellValue())
                            .type(DeviceType.valueOf(row.getCell(headerIndex.get("type")).getStringCellValue()))
                            .clusterId(clusterMap.get(
                                    row.getCell(headerIndex.get("cluster_name")).getStringCellValue()
                            )).other(other).build();
            add(deviceParam);
        }
        return ApiResponse.success();
    }
}
