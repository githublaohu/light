package org.example.spring.mvc;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/testInterface")
public class InterfaceController {

    @PostMapping("/testBody")
    public ReturnObject testBody(@RequestBody ReturnObject exampleInfo) {
        exampleInfo.setKey("testBody");
        return exampleInfo;
    }

    @PostMapping("/testHeader")
    public ReturnObject testHeader(@RequestHeader String key, @RequestHeader String value, @RequestHeader String testStr) {
        ReturnObject serverReturn = new ReturnObject(key, value);
        serverReturn.setValue(testStr);
        return serverReturn;
    }

    @PostMapping("/testHeaders")
    public ReturnObject testHeaders(@RequestHeader String testHeader) {
        return createReturnObject(testHeader);
    }

    @GetMapping("/testPath/{key}/{value}/{path}")
    public ReturnObject testPath(@PathVariable("key") String key11, @PathVariable("value") String value22, @PathVariable("path") String path11) {
        ReturnObject serverReturn = new ReturnObject(key11, value22);
        serverReturn.setValue(path11);

        return serverReturn;
    }

    @GetMapping("/testQuery")
    public ReturnObject testQuery(String key, String value, String path) {
        ReturnObject serverReturn = new ReturnObject(key, value);
        serverReturn.setValue(path);

        return serverReturn;
    }

    @DeleteMapping("/deleteTest")
    public ReturnObject deleteTest() {
        return createReturnObject("deleteTest");
    }

    @GetMapping("/getTest")
    public ReturnObject getTest() {
        return createReturnObject("getTest");
    }

    @GetMapping("/headTest")
    public ReturnObject headTest() {
        return createReturnObject("headTest");
    }

    @PatchMapping("/patchTest")
    public ReturnObject patchTest() {
        return createReturnObject("patchTest");
    }

    @PostMapping("/postTest")
    public ReturnObject postTest() {
        return createReturnObject("postTest");
    }

    @PutMapping("/putTest")
    public ReturnObject putTest() {
        return createReturnObject("putTest");
    }



    @PostMapping("queryExample")
    public ReturnObject queryExample() {
        return createReturnObject(null);
    }

    @GetMapping("queryExampleList")
    public List<ReturnObject> queryExampleList() {
        List<ReturnObject> list = new ArrayList<ReturnObject>();
        for (int i = 0; i < 10; i++) {
            list.add(createReturnObject(null));
        }
        return list;
    }

    private ReturnObject createReturnObject(String value) {
        ReturnObject exampleInfo = new ReturnObject();
        exampleInfo.setKey("createKey");
        exampleInfo.setValue(value == null ? "createValue" : value);
        return exampleInfo;
    }

}
