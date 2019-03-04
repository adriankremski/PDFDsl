package com.github.snuffix.pdfdsl

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.itextpdf.text.Font
import com.itextpdf.text.pdf.BaseFont
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private val blue = "#0085c1"
    private val black = "#000000"
    private val green = "#437a4c"

    private val titleFont = Font(
        BaseFont.createFont(BaseFont.HELVETICA, "UTF-8", BaseFont.EMBEDDED),
        20f,
        Font.NORMAL,
        hex2Color(black)
    )

    private val subtitleFont = Font(
        BaseFont.createFont(BaseFont.HELVETICA, "UTF-8", BaseFont.EMBEDDED),
        20f,
        Font.BOLD,
        hex2Color(green)
    )

    private val bodyFont = Font(
        BaseFont.createFont(BaseFont.TIMES_ROMAN, "UTF-8", BaseFont.EMBEDDED), 16f,
        Font.NORMAL,
        hex2Color(black)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        // Don't generate pdfs on main thread!
        val pdfDocument = createPDF {
            path = "${baseContext.cacheDir.path}/temp.pdf"
            pageSize = PDFPageSize.A4

            write {
                for (titleIndex in 1..5) {

                    // Store pdf part parameter should be omitted on production builds
                    //addText(storePdfPart = true) {
                    //   font = titleFont
                    //   text = "$titleIndex. Title"
                    //}
                    addText {
                        font = titleFont
                        text = "$titleIndex. Title"
                    }

                    addNewLine()
                    addSeparator(color = blue)

                    for (subtitleIndex in 1..10) {
                        addText {
                            font = subtitleFont
                            text = "$subtitleIndex. Subtitle"
                        }

                        addNewLine()

                        addText {
                            font = bodyFont
                            text =
                                "On the other hand, we denounce with righteous indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the moment, so blinded by desire, that they cannot foresee the pain and trouble that are bound to ensue; and equal blame belongs to those who fail in their duty through weakness of will, which is the same as saying through shrinking from toil and pain. These cases are perfectly simple and easy to distinguish. In a free hour, when our power of choice is untrammelled and when nothing prevents our being able to do what we like best, every pleasure is to be welcomed and every pain avoided. But in certain circumstances and owing to the claims of duty or the obligations of business it will frequently occur that pleasures have to be repudiated and annoyances accepted. The wise man therefore always holds in these matters to this principle of selection: he rejects pleasures to secure other greater pleasures, or else he endures pains to avoid worse pains"
                        }
                    }
                }

                addNewLine()
            }
        }

//        pdfDocument.forEach {
//            Log.i("PDF", it.toString())
//        }

        pdfDocument.path?.let {
            pdfViewPager.adapter = PDFPagerAdapter(this, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
