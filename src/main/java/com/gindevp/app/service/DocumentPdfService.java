package com.gindevp.app.service;

import com.gindevp.app.domain.*;
import com.gindevp.app.repository.*;
import com.gindevp.app.service.util.CodeDisplayUtils;
import com.gindevp.app.web.rest.errors.BadRequestAlertException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentPdfService {

    private static final String RECEIPT_ENTITY = "stockReceipt";

    private static final String ISSUE_ENTITY = "stockIssue";

    private static final float MARGIN = 40f;

    private static final float TITLE_PT = 13f;

    private static final float BODY_PT = 9.5f;

    private static final float LINE_STEP = 12f;

    private final StockReceiptRepository stockReceiptRepository;

    private final StockReceiptLineRepository stockReceiptLineRepository;

    private final StockIssueRepository stockIssueRepository;

    private final StockIssueLineRepository stockIssueLineRepository;

    public DocumentPdfService(
        StockReceiptRepository stockReceiptRepository,
        StockReceiptLineRepository stockReceiptLineRepository,
        StockIssueRepository stockIssueRepository,
        StockIssueLineRepository stockIssueLineRepository
    ) {
        this.stockReceiptRepository = stockReceiptRepository;
        this.stockReceiptLineRepository = stockReceiptLineRepository;
        this.stockIssueRepository = stockIssueRepository;
        this.stockIssueLineRepository = stockIssueLineRepository;
    }

    @Transactional(readOnly = true)
    public byte[] buildStockReceiptPdf(Long receiptId) {
        StockReceipt r = stockReceiptRepository
            .findById(receiptId)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy phiếu nhập", RECEIPT_ENTITY, "idnotfound"));
        List<StockReceiptLine> lines = stockReceiptLineRepository.findByReceipt_IdOrderByLineNoAsc(receiptId);
        List<String> body = new ArrayList<>();
        body.add("Phiếu nhập: " + nz(r.getCode()));
        body.add("Ngày: " + r.getReceiptDate());
        body.add("Nguồn: " + String.valueOf(r.getSource()));
        body.add("Trạng thái: " + String.valueOf(r.getStatus()));
        if (r.getNote() != null && !r.getNote().isBlank()) {
            body.add("Ghi chú: " + r.getNote());
        }
        body.add("");
        body.add("STT | Tài sản | SL | Đơn giá | Thành tiền");
        for (StockReceiptLine line : lines) {
            String itemName = line.getAssetItem() != null ? nz(line.getAssetItem().getName()) : "—";
            int qty = line.getQuantity() != null ? line.getQuantity() : 0;
            BigDecimal up = line.getUnitPrice() != null ? line.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal lineTotal = up.multiply(BigDecimal.valueOf(qty));
            body.add(
                String.format(
                    "%s | %s | %d | %s | %s",
                    line.getLineNo() != null ? line.getLineNo() : "—",
                    itemName,
                    qty,
                    up.toPlainString(),
                    lineTotal.toPlainString()
                )
            );
        }
        return renderPdf("Phiếu nhập kho", body);
    }

    @Transactional(readOnly = true)
    public byte[] buildStockIssuePdf(Long issueId) {
        StockIssue issue = stockIssueRepository
            .findById(issueId)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy phiếu xuất", ISSUE_ENTITY, "idnotfound"));
        List<StockIssueLine> lines = stockIssueLineRepository.findByIssue_IdOrderByLineNoAsc(issueId);
        List<String> body = new ArrayList<>();
        body.add("Phiếu xuất: " + nz(issue.getCode()));
        body.add("Ngày: " + issue.getIssueDate());
        body.add("Trạng thái: " + String.valueOf(issue.getStatus()));
        body.add("Đối tượng nhận: " + String.valueOf(issue.getAssigneeType()) + " — " + resolveAssigneeLabel(issue));
        if (issue.getNote() != null && !issue.getNote().isBlank()) {
            body.add("Ghi chú: " + issue.getNote());
        }
        body.add("");
        body.add("STT | Tài sản | Mã TB | SL");
        for (StockIssueLine line : lines) {
            String itemName = line.getAssetItem() != null ? nz(line.getAssetItem().getName()) : "—";
            String eqCode = "—";
            if (line.getEquipment() != null && line.getEquipment().getEquipmentCode() != null) {
                eqCode = CodeDisplayUtils.formatEquipmentCode(line.getEquipment().getEquipmentCode());
            }
            int qty = line.getQuantity() != null ? line.getQuantity() : 0;
            body.add(
                String.format(
                    "%s | %s | %s | %d",
                    line.getLineNo() != null ? line.getLineNo() : "—",
                    itemName,
                    eqCode,
                    qty
                )
            );
        }
        return renderPdf("Phiếu xuất kho", body);
    }

    private static String nz(String s) {
        return s != null ? s : "";
    }

    private static String resolveAssigneeLabel(StockIssue issue) {
        return switch (issue.getAssigneeType()) {
            case EMPLOYEE -> {
                Employee e = issue.getEmployee();
                yield e != null && e.getFullName() != null ? e.getFullName() : "";
            }
            case DEPARTMENT -> {
                Department d = issue.getDepartment();
                yield d != null && d.getName() != null ? d.getName() : "";
            }
            case LOCATION -> {
                Location loc = issue.getLocation();
                yield loc != null && loc.getName() != null ? loc.getName() : "";
            }
            case COMPANY -> "Công ty";
        };
    }

    private static byte[] renderPdf(String title, List<String> lines) {
        try (PDDocument doc = new PDDocument()) {
            FontPair fonts = FontPair.load(doc);
            float pageH = PDRectangle.A4.getHeight();
            float minY = MARGIN;

            int idx = 0;
            boolean needTitle = true;

            while (needTitle || idx < lines.size()) {
                PDPage page = new PDPage(PDRectangle.A4);
                doc.addPage(page);
                float y = pageH - MARGIN;
                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    if (needTitle) {
                        writeLine(cs, fonts, title, TITLE_PT, true, MARGIN, y);
                        y -= TITLE_PT + 10;
                        needTitle = false;
                    }
                    while (idx < lines.size()) {
                        float nextY = y - LINE_STEP;
                        if (nextY < minY) {
                            break;
                        }
                        String raw = lines.get(idx);
                        writeLine(cs, fonts, raw, BODY_PT, false, MARGIN, y);
                        y = nextY;
                        idx++;
                    }
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("PDF generation failed", e);
        }
    }

    private static void writeLine(PDPageContentStream cs, FontPair fonts, String text, float size, boolean bold, float x, float yTop)
        throws IOException {
        String t = truncateForPdf(text, bold ? 80 : 110);
        if (!fonts.hasUnicode) {
            t = foldAscii(t);
        }
        cs.beginText();
        cs.setFont(bold ? fonts.bold : fonts.regular, size);
        cs.newLineAtOffset(x, yTop - size);
        cs.showText(t);
        cs.endText();
    }

    private static String truncateForPdf(String s, int maxChars) {
        if (s == null) {
            return "";
        }
        if (s.length() <= maxChars) {
            return s;
        }
        return s.substring(0, Math.max(0, maxChars - 1)) + "…";
    }

    /** Fallback khi không có font Unicode (ASCII an toàn với Helvetica). */
    private static String foldAscii(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < n.length(); i++) {
            char c = n.charAt(i);
            if (Character.getType(c) == Character.NON_SPACING_MARK) {
                continue;
            }
            if (c >= 32 && c < 127) {
                b.append(c);
            } else if (Character.isWhitespace(c)) {
                b.append(' ');
            }
        }
        return b.toString().trim();
    }

    private static final class FontPair {

        final PDFont regular;

        final PDFont bold;

        final boolean hasUnicode;

        FontPair(PDFont regular, PDFont bold, boolean hasUnicode) {
            this.regular = regular;
            this.bold = bold;
            this.hasUnicode = hasUnicode;
        }

        static FontPair load(PDDocument doc) throws IOException {
            try (
                InputStream reg = DocumentPdfService.class.getResourceAsStream("/fonts/NotoSans-Regular.ttf");
                InputStream bld = DocumentPdfService.class.getResourceAsStream("/fonts/NotoSans-Bold.ttf")
            ) {
                if (reg != null && bld != null) {
                    return new FontPair(PDType0Font.load(doc, reg), PDType0Font.load(doc, bld), true);
                }
            }
            return new FontPair(
                new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                false
            );
        }
    }
}
