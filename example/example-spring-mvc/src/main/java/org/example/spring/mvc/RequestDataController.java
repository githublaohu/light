package org.example.spring.mvc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requesData")
public class RequestDataController {

	@GetMapping("pathData")
	public ReturnObject pathData(ReturnObject returnObject, @RequestHeader("name") String name ) {
		
		return returnObject;
	}
	
}
