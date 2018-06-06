package com.dyf.apiadmin.web

import com.dyf.core.utils.HttpResponse
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/index")
class AdminIndexController {

    @RequestMapping("/index")
    def index() {
        return HttpResponse.success("hello world, this is admin service")
    }

}
