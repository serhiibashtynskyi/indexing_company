package simplesolution.dev.controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

import lombok.Cleanup;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;

@Controller
public class FileUploadController {

    private final String UPLOAD_DIRECTORY = "";

    @GetMapping("/")
    public String uploadFileForm() {
        return "index";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, HttpServletResponse response)
            throws IOException {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/";
        }

        Path path = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        Files.write(path, file.getBytes());
        redirectAttributes.addFlashAttribute("successMessage", "File upload successfully, uploaded file name: " + file.getOriginalFilename());
        InputStream initialStream = file.getInputStream();
        byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);

        File targetFile = new File("src/main/resources/targetFile.xlsx");

        try (OutputStream outStream = new FileOutputStream(targetFile)) {
            outStream.write(buffer);
        }
        handleExcel(targetFile, response);
        return "redirect:/";
    }

    public void handleExcel(File file, HttpServletResponse response) throws IOException {

        @Cleanup FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet worksheet = workbook.getSheetAt(0);
        int i = 1;
        int readCell = 1;
        int writeCell = 0;
        Hashtable balance = new Hashtable();
        for (int j = 1; j < worksheet.getLastRowNum(); j++) {
            String currentCompany = worksheet.getRow(j).getCell(readCell).toString();
            if (!balance.containsKey(currentCompany)) {
                balance.put(worksheet.getRow(j).getCell(readCell).toString(), i);
                worksheet.getRow(j).getCell(writeCell).setCellValue(i);
                i++;
                continue;
            }
            worksheet.getRow(j).getCell(writeCell).setCellValue(balance.get(currentCompany).toString());
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        workbook.write(buffer);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.toByteArray());

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=file_output_final.xlsx");
        IOUtils.copy(byteArrayInputStream, response.getOutputStream());
    }
}