package org.codreaming.server.service

import org.codreaming.server.logger
import org.springframework.stereotype.Service

@Service
class PartnersSearcherService {
    companion object {
        val log by logger()
    }

    fun findPartners() {
        log.info("Find partners method was invocated")
    }
}