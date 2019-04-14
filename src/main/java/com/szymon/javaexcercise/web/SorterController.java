package com.szymon.javaexcercise.web;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
public class SorterController {


    @GetMapping("/")
    public String home() {
        return "home";

    }

    @GetMapping("/view")
    public String getAll(HttpSession session, Model model) {
        ArrayList<Triple<String, String, Date>> data = getData(session);
        data.sort(
                (Triple<String, String, Date> a, Triple<String, String, Date> b) -> {
                    if (a.getRight().before(b.getRight())) return 1;
                    else return -1;
                });
        model.addAttribute("data", data);
        return "table";

    }

    @PostMapping("/submitCSV")
    public RedirectView submit(Model model, @RequestParam("file") MultipartFile csv, HttpSession session, HttpServletResponse response) {
        List<List<String>> records;
        try {
            records = extractCSV(csv);
        } catch (IOException e) {
            model.addAttribute("errorMsg", "Couldn't parse csv");
            return new RedirectView("error");
        }

        for (int i = 1; i < records.size(); i++) {

            List<String> record = records.get(i);
            if (addEntry(model, record.get(0), String.format("%.4s-xxxx-xxxx-xxxx", record.get(1)), record.get(2), session))
                return new RedirectView("error");
        }

        response.setStatus(HttpServletResponse.SC_CREATED);
        return new RedirectView("/");

    }

    @PostMapping("/submitOne")
    public RedirectView submit(Model model, @RequestParam String bank, @RequestParam String number, @RequestParam String date, HttpSession session, HttpServletResponse response) {

        if (addEntry(model, bank, String.format("%.4s-xxxx-xxxx-xxxx", number), date, session))
            return new RedirectView("error");

        response.setStatus(HttpServletResponse.SC_CREATED);
        return new RedirectView("/");

    }

    private List extractCSV(MultipartFile csv) throws IOException {
        List<List<String>> records = new ArrayList<>();
        File convFile = new File(Objects.requireNonNull(csv.getOriginalFilename()));
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(csv.getBytes());
        fos.close();
        try (CSVReader csvReader = new CSVReader(new FileReader(convFile))) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        }
        return records;
    }

    private boolean addEntry(Model model, String bank, String number, String date, HttpSession session) {
        DateFormat format = new SimpleDateFormat("MMM-yyyy", Locale.ENGLISH);
        Date parsedDate;
        try {
            parsedDate = format.parse(date);
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return true;
        }
        Date finalDate = parsedDate;
        ArrayList<Triple<String, String, Date>> data = getData(session);
        data.add(new Triple<String, String, Date>() {
            @Override
            public String getLeft() {
                return bank;
            }

            @Override
            public String getMiddle() {
                return number;
            }

            @Override
            public Date getRight() {
                return finalDate;
            }

            public String getRightFormatted() {
                return format.format(finalDate);
            } //return date like Nov-2017
        });
        return false;
    }


    private ArrayList<Triple<String, String, Date>> getData(HttpSession session) {
        ArrayList<Triple<String, String, Date>> data = (ArrayList<Triple<String, String, Date>>) session.getAttribute("data");
        if (data == null) {
            data = new ArrayList<>();
            session.setAttribute("data", data);
        }
        return data;
    }

}