package com.example.barcodee

import android.content.pm.PackageManager
import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView

class ScanActivity : AppCompatActivity() {
    private lateinit var barcodeView: CompoundBarcodeView
    private lateinit var outputScan: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        barcodeView = findViewById(R.id.barcode_scanner)
        barcodeView.decodeContinuous(callback)
        outputScan = findViewById(R.id.output)
    }

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            result?.let {
                val barcodeValue = result.text
                outputScan.text = barcodeValue

                outputScan.setOnClickListener {
                    // Salin teks ke clipboard
                    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Barcode Result", barcodeValue)
                    clipboardManager.setPrimaryClip(clipData)

                    Toast.makeText(this@ScanActivity, "Teks berhasil disalin ke clipboard", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint?>?) {}
    }

    override fun onResume() {
        super.onResume()

        // Meminta izin kamera jika belum diberikan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Izin kamera diperlukan untuk pemindaian barcode",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 100
    }
}