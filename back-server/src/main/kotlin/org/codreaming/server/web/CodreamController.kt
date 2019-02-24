package org.codreaming.server.web

import org.codreaming.server.logger
import org.codreaming.server.service.PartnersSearcherService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CodreamController (
        @Autowired val partnersSearcherService: PartnersSearcherService
) {
    companion object {
        val log by logger()
    }

    @PostMapping("/findPartners")
    fun findPartners() {
        partnersSearcherService.findPartners()
    }
}