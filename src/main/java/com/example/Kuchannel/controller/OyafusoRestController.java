package com.example.Kuchannel.controller;

import com.example.Kuchannel.service.OyafusoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OyafusoRestController {

    @Autowired
    private OyafusoService oyafusoService;

    @GetMapping("/getUrl")
    public String getUrl(@RequestParam("name") Integer id) {
        System.out.println("コミュニティURL:" + oyafusoService.getUrl(id).url());
        return oyafusoService.getUrl(id).url();
    }

}
