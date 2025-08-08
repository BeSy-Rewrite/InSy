package com.hs_esslingen.insy.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hs_esslingen.insy.service.ExcelService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/download")
public class DownloadController {

    private final ExcelService excelService;

    @GetMapping("/xls")
    public ResponseEntity<Resource> downloadXlsx(
            @RequestParam(name = "tags", required = false) List<Integer> tags,
            @RequestParam(name = "minId", required = false) Integer minId,
            @RequestParam(name = "maxId", required = false) Integer maxId,
            @RequestParam(name = "minPrice", required = false) Integer minPrice,
            @RequestParam(name = "maxPrice", required = false) Integer maxPrice,
            @RequestParam(name = "isDeinventoried", required = false) Boolean isDeinventoried,
            @RequestParam(name = "orderer", required = false) List<String> orderers,
            @RequestParam(name = "company", required = false) List<String> companies,
            @RequestParam(name = "location", required = false) List<String> locations,
            @RequestParam(name = "costCenter", required = false) List<String> costCenters,
            @RequestParam(name = "serialNumber", required = false) List<String> serialNumbers,
            @RequestParam(name = "createdAfter", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAfter,
            @RequestParam(name = "createdBefore", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdBefore,
            @RequestParam(name = "searchText", required = false) String searchText) throws IOException {
        return excelService.exportExcel(
                tags,
                minId,
                maxId,
                minPrice,
                maxPrice,
                isDeinventoried,
                orderers,
                companies,
                locations,
                costCenters,
                serialNumbers,
                createdAfter,
                createdBefore,
                searchText);
    }
}
