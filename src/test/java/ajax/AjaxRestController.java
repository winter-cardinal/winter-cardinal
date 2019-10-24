/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package ajax;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AjaxRestController {
	@GetMapping( "/200" )
	ResponseEntity<String> get200(){
		return ResponseEntity.status(HttpStatus.OK).body("200");
	}

	@GetMapping( "/204" )
	ResponseEntity<String> get204(){
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
	}

	@GetMapping( "/304" )
	ResponseEntity<String> get304(){
		return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("304");
	}
	
	@GetMapping( "/400" )
	ResponseEntity<String> get400(){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	}
}
