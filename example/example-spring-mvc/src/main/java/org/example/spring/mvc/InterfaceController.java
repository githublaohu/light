package org.example.spring.mvc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testInterface")
public class InterfaceController {

	
	
	
	@PostMapping("testObject")
	public ReturnObject addExample(@RequestBody ReturnObject exampleInfo) {
		exampleInfo.setKey("11111");
		return exampleInfo;
	}
	
	@PostMapping("queryExample")
	public ReturnObject queryExample() {
		return createReturnObject();
	}
	
	@GetMapping("queryExampleList")
	public List<ReturnObject> queryExampleList(){
		List<ReturnObject> list = new ArrayList<ReturnObject>();
		for(int i = 0; i< 10 ; i++) {
			list.add(createReturnObject());
		}
		return list;
	}
	
	private ReturnObject createReturnObject() {
		ReturnObject exampleInfo = new ReturnObject();
		exampleInfo.setId(1);
		exampleInfo.setKey("111");
		return exampleInfo;
	}
	
}
