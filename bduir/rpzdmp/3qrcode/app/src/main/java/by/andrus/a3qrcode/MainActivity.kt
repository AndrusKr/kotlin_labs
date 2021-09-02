package by.andrus.a3qrcode

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

class MainActivity : AppCompatActivity() {

    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var phone: EditText
    private lateinit var button1: Button
    private lateinit var create: Button
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firstName = findViewById(R.id.first_name)
        lastName = findViewById(R.id.last_name)
        phone = findViewById(R.id.phone_number)
        button1 = findViewById(R.id.generate_qr_btn)
        imageView = findViewById(R.id.image_view)
        create = findViewById(R.id.button_contacts)
        button1.setOnClickListener { initQRCode() }
    }

    private fun initQRCode() {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix: BitMatrix = multiFormatWriter.encode(
                    firstName.text.toString() + " " +
                            lastName.text.toString() + " " +
                            phone.text.toString(),
                    BarcodeFormat.QR_CODE, 600, 600)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            imageView.setImageBitmap(bitmap)
            imageView.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}