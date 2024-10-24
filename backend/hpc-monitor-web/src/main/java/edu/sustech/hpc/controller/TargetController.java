//package edu.sustech.hpc.controller;
//
//import edu.sustech.hpc.annotation.PathController;
//import edu.sustech.hpc.model.param.HardwareParam;
//import edu.sustech.hpc.model.vo.HardwareReply;
//import edu.sustech.hpc.result.ApiResponse;
//import edu.sustech.hpc.service.HardwareService;
//import edu.sustech.hpc.service.PrometheusService;
//import jakarta.annotation.Resource;
//import org.json.JSONArray;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.List;
//
//@PathController("/api/target")
//public class TargetController {
//    @Resource
//    private PrometheusService prometheusService;
//
//    @Resource
//    private HardwareService hardwareService;
//    @GetMapping
//    public ApiResponse getAllTargetsReply(@RequestParam(name="job", required=false) String jobName){
//        List<HardwareReply> hardwareReplyList = null;
//        try {
//            hardwareReplyList = prometheusService.getAllHardwareReply(jobName);
//        } catch (IOException e) {
//            return ApiResponse.internalServerError();
//        }
//        JSONArray jsonArray = new JSONArray(hardwareReplyList);
//        String message = jsonArray.toString();
//        return ApiResponse.success(message);
//    }
//
//    @PostMapping("/{id}")
//    public ApiResponse deleteTarget(@PathVariable Integer id) {
//        hardwareService.delete(id);
//        return ApiResponse.success("delete " + id);
//    }
//
//    @PostMapping
//    public ApiResponse addTarget(@Validated @RequestBody HardwareParam hardwareParam) {
//        HardwareParam hardwareVo = hardwareService.add(hardwareParam);
//        return ApiResponse.success(hardwareVo);
//    }
//}
