package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.utils.DOWNLOAD_ID
import com.udacity.utils.FILE_NAME


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding


    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.contentDetail.okButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey(DOWNLOAD_ID)) {
                val downloadId = extras.getLong(DOWNLOAD_ID)
                val fileName = extras.getString(FILE_NAME)
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                if (downloadId == -1L) return
                // query download status
                val cursor: Cursor =
                    downloadManager.query(downloadId.let {
                        DownloadManager.Query().setFilterById(it)
                    })
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                    binding.contentDetail.fileName.text = fileName
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        binding.contentDetail.status.text = getString(R.string.success)
                        binding.contentDetail.status.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    } else {
                        binding.contentDetail.status.text = getString(R.string.fail)
                        binding.contentDetail.status.setTextColor(resources.getColor(R.color.fail_status_color))
                    }
                }
            }
        }
    }
}
