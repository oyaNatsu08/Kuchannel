package com.example.Kuchannel.controller;

import com.example.Kuchannel.entity.UrlRecord;
import com.example.Kuchannel.service.KuchannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OyafusoRestController {

    @Autowired
    private KuchannelService oyafusoService;

    @GetMapping("/getUrl")
    public String getUrl(@RequestParam("name") Integer id) {
        System.out.println("コミュニティURL:" + oyafusoService.getUrl(id).url());
        return oyafusoService.getUrl(id).url();
    }

}
