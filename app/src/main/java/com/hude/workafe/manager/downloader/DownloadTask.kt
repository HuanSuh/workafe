package com.hude.workafe.manager.downloader

import android.os.AsyncTask
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by huansuh on 2018. 7. 17..
 */
class DownloadTask(private val onProgressListener: OnProgressListener? = null) : AsyncTask<String, Int, String>() {
    private val SUCCESS = "SUCCESS"

    override fun doInBackground(vararg sUrl: String): String? {
        var input: InputStream? = null
        var output: OutputStream? = null
        var connection: HttpURLConnection? = null
        try {
            val url = URL(sUrl[0])
            connection = url.openConnection() as HttpURLConnection
            connection.connect()

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return ("Server returned HTTP " + connection.responseCode
                        + " " + connection.responseMessage)
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            val fileLength = connection.contentLength

            // download the file
            input = connection.inputStream
            output = FileOutputStream(sUrl[1])
            input?.let{

                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int
                do {
                    count = it.read(data)
                    if(count == -1) break
                    // allow canceling with back button
                    if (isCancelled) {
                        it.close()
                        return null
                    }
                    total += count.toLong()
                    // publishing the progress....
                    if (fileLength > 0) {
                        // only if total length is known
                        publishProgress((total * 99 / fileLength).toInt())
                    }
                    output.write(data, 0, count)
                } while (count != -1)
            }
        } catch (e: Exception) {
            return e.toString()
        } finally {
            try {
                if (output != null)
                    output.close()
                if (input != null)
                    input.close()
            } catch (ignored: IOException) { }

            if (connection != null)
                connection.disconnect()
        }
        return SUCCESS
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        onProgressListener?.onProgressUpdate(values[0]?:-1)
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        onProgressListener?.onProgressUpdate(100)
        Thread.sleep(1000)
        if(SUCCESS == result) {
            onProgressListener?.onFinish()
        } else {
            onProgressListener?.onFail()
        }
    }

    public interface OnProgressListener {
        fun onProgressUpdate(progress: Int)
        fun onFinish()
        fun onFail()
    }
}