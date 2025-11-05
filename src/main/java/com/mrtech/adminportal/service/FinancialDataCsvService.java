package com.mrtech.adminportal.service;

import com.mrtech.adminportal.entity.FinancialData;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinancialDataCsvService {

    public List<FinancialData> loadFinancialData() {
        List<FinancialData> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource("static/financial_data.csv").getInputStream()))) {

            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                list.add(new FinancialData(values[0],
                        Integer.parseInt(values[1]),
                        Integer.parseInt(values[2])));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
