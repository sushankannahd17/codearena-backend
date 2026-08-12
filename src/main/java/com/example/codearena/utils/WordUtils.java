package com.example.codearena.utils;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WordUtils {
    public void addHeading(XWPFDocument document, String text) {
        XWPFParagraph paragraph=document.createParagraph();

        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run=paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(15);
        run.setFontFamily("Times New Roman");
    }

    public void addSubHeading(XWPFDocument document, String text) {
        XWPFParagraph paragraph=document.createParagraph();

        XWPFRun run=paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(11);
        run.setFontFamily("Times New Roman");
    }

    public void addLabelValue(XWPFDocument document, String text, String value, Boolean bold) {
        XWPFParagraph paragraph=document.createParagraph();

        XWPFRun run=paragraph.createRun();
        run.setText(text + ": " + value);
        run.setFontFamily("Times New Roman");
        run.setFontSize(10);
        run.setBold(bold);
    }
    
    public void addCode(XWPFDocument document, String code) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setSpacingBefore(50);
        paragraph.setSpacingAfter(100);
        XWPFRun run = paragraph.createRun();

        run.setFontFamily("Consolas");
        run.setFontSize(9);

        String[] lines = code.split("\\R", -1);
        for (int i=0; i< lines.length; i++) {
            run.setText(lines[i]);
            if (i < lines.length-1) {
                run.addBreak();
            }
        }
    }

    public void addText(XWPFDocument document, String text) {
        XWPFParagraph paragraph=document.createParagraph();

        XWPFRun run=paragraph.createRun();
        run.setFontFamily("Times New Roman");
        run.setFontSize(10);

        String[] lines = text.split("\\R", -1);
        for (int i=0; i< lines.length; i++) {
            run.setText(lines[i]);
            if (i < lines.length-1) {
                run.addBreak();
            }
        }
    }
}
