package com.github.snuffix.pdfdsl

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import java.io.FileOutputStream


fun createPDF(block: PDFDocument.(PDFDocument) -> Unit): PDFDocument {
    val document = PDFDocument()
    document.block(document)
    return document
}

class PDFDocument : Iterable<PDFPart> {
    private val document = Document()
    private val pdfParts = mutableListOf<PDFPart>()

    operator fun get(index: Int) = pdfParts[index]
    override fun iterator(): Iterator<PDFPart> = pdfParts.iterator()

    var path: String? = null
        set(value) {
            value?.let {
                PdfWriter.getInstance(document, FileOutputStream(it))
            }
            field = value
        }

    var pageSize: PDFPageSize? = null
        set(value) {
            value?.let {
                document.pageSize = it.pageSize
            }
        }

    fun write(block: PDFDocument.(PDFDocument) -> Unit) {
        document.open()
        this.block(this)
        document.close()
    }

    fun addNewLine(storePdfPart: Boolean = false) {
        add(PDFPart.NewLine, storePdfPart)
    }

    fun addSeparator(color: String, storePdfPart: Boolean = false) {
        add(PDFPart.Separator(color))
    }

    fun addText(storePdfPart : Boolean = true, block: PDFPart.Text.(PDFPart.Text) -> Unit): PDFPart.Text {
        val text = PDFPart.Text()
        text.block(text)
        add(text, storePdfPart)
        return text
    }

    // For testing purposes we might to store generated parts.
    // However generating extra objects on production code might now be a good thing to do
    private fun add(pdfPart: PDFPart, storePdfPart: Boolean = false) {
        if (storePdfPart) {
            pdfParts.add(pdfPart)
        }

        when (pdfPart) {
            is PDFPart.Text -> {
                addText(pdfPart.font, pdfPart.text)
            }
            is PDFPart.NewLine -> {
                addNewLine()
            }
            is PDFPart.Separator -> {
                addSeparator(pdfPart.color)
            }
        }
    }

    private fun addText(font: Font, text: String) {
        val chunk = Chunk(text.convertPolishCharactersToUnicode(), font)
        val paragraph = Paragraph(chunk)
        document.add(paragraph)
    }

    private fun addNewLine() {
        document.add(Paragraph("\n"))
    }

    private fun addSeparator(separatorColorInHex: String) {
        val separator = LineSeparator();
        separator.lineColor = hex2Color(separatorColorInHex)
        document.add(Chunk(separator));
    }
}

fun hex2Color(colorStr: String): BaseColor {
    val r = colorStr.substring(1, 3).toInt(16)
    val g = colorStr.substring(3, 5).toInt(16)
    val b = colorStr.substring(5, 7).toInt(16)

    return BaseColor(r, g, b)
}

private fun String.convertPolishCharactersToUnicode() = this
    .replace("ń", "\u0144")
    .replace("ę", "\u0119")
    .replace("ó", "\u00F3")
    .replace("ł", "\u0142")
    .replace("ż", "\u017C")
    .replace("Ż", "\u017B")
    .replace("ź", "\u017A")
    .replace("ć", "\u0107")
    .replace("ś", "\u015B")
    .replace("Ś", "\u015A")
    .replace("ą", "\u0105")

enum class PDFPageSize(val pageSize: Rectangle) {
    A4(pageSize = PageSize.A4)
}

sealed class PDFPart {
    object NewLine : PDFPart()
    data class Separator(val color: String) : PDFPart()

    class Text : PDFPart() {
        lateinit var font: Font
        lateinit var text: String
    }
}
