package org.refit.spring.ceo.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.ceo.dto.ReceiptExceptMerchandiseDto;
import org.refit.spring.mapper.CeoMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;

import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptExportService {
    private final CeoMapper ceoMapper;
    private final JavaMailSender javaMailSender;

    public void generateAndSendCsvByEmail(Long userId, String emailAddress) throws Exception {
        // 1. 처리 완료된 영수증 리스트 조회
        List<ReceiptExceptMerchandiseDto> receiptList = ceoMapper.getCompletedReceiptDetails(userId);

        // 2. CSV 파일 생성
        File csvFile = exportToCsv(receiptList);

        // 3. 이메일 전송
        sendEmailWithAttachment(emailAddress, csvFile);
    }

    private File exportToCsv(List<ReceiptExceptMerchandiseDto> receiptList) throws IOException {
        String fileName = "processed_receipts_" + System.currentTimeMillis() + ".csv";
        File file = new File(System.getProperty("java.io.tmpdir"), fileName);

        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            // Header
            writer.writeNext(new String[]{
                    "영수증ID", "사용자ID", "사업자번호", "상호명", "대표자명", "주소", "총금액", "공급가", "부가세",
                    "거래유형", "처리상태", "카드번호", "법인카드여부", "거절사유", "생성일시"
            });

            for (ReceiptExceptMerchandiseDto dto : receiptList) {
                writer.writeNext(new String[]{
                        String.valueOf(dto.getReceiptId()),
                        String.valueOf(dto.getUserId()),
                        String.valueOf(dto.getCompanyId()),
                        dto.getCompanyName(),
                        dto.getCeoName(),
                        dto.getAddress(),
                        String.valueOf(dto.getTotalPrice()),
                        String.valueOf(dto.getSupplyPrice()),
                        String.valueOf(dto.getSurtax()),
                        dto.getTransactionType(),
                        dto.getProcessState(),
                        dto.getCardNumber(),
                        String.valueOf(dto.getIsCorporate()),
                        dto.getRejectedReason() != null ? dto.getRejectedReason() : "",
                        dto.getCreatedAt().toString()
                });
            }

            writer.flush();
        }

        return file;
    }

    private void sendEmailWithAttachment(String toEmail, File file) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("처리 완료된 영수증 내역 CSV");

        // 1. HTML load
        String htmlContent = loadEmailHtmlTemplate();
        helper.setText(htmlContent, true); //html = true

        // 2. logo.png 이미지 추가
        ClassPathResource imageResource = new ClassPathResource("static/images/logo.png");
        helper.addInline("logo", imageResource);

        // 3. CSV 첨부
        helper.addAttachment(file.getName(), file);

        javaMailSender.send(message);
    }

    //HTML을 String으로 읽어서 메일 본문으로 사용
    private String loadEmailHtmlTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/refitCeoEmail.html");

        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }

            return contentBuilder.toString();
        }
    }
}
