package com.hude.workafe.manager.downloader

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.hude.workafe.manager.preference.PrefConst
import com.hude.workafe.manager.preference.PreferenceManager
import com.hude.workafe.model.CafeData
import com.hude.workafe.utils.Constants
import com.hude.workafe.utils.Utils
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset


/**
 * Created by huansuh on 2018. 7. 17..
 */
class CafeDataLoader {
    fun loadCafeData(context: Context): CafeData {
        return readCafeData(context)
    }

    fun checkForUpdate(context: Context, listener: UpdateCheckListener) {
        val prefManager = PreferenceManager(context)
        val localVersion = prefManager.getString(PrefConst.VERSION_LOCAL, "")
        val fDb = FirebaseDatabase.getInstance()
        fDb.getReference("latest_data").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val serverVersion = snapshot.value as String
                if (localVersion < serverVersion && serverVersion != prefManager.getString(PrefConst.SKIP_VERSION)) {
                    listener.needUpdate(serverVersion)
                }
            }
        })
    }

    fun downloadCafeData(context: Context, versionDate: String?, onProgressListener: DownloadTask.OnProgressListener) {
        if(versionDate == null) {
            checkForUpdate(context, object : UpdateCheckListener {
                override fun needUpdate(versionDate: String) {
                    requestCafeData(context, versionDate, onProgressListener)
                }
            })
        } else {
            requestCafeData(context, versionDate, onProgressListener)
        }
    }
    private fun requestCafeData(context: Context, versionDate: String, onProgressListener: DownloadTask.OnProgressListener) {
        val destFile = File(context.filesDir,"cafeItems.json")
        val downloadTask = DownloadTask(onProgressListener)
        downloadTask.execute(Constants.CAFE_ITEM_URL + "cafeItems_$versionDate.json", destFile.absolutePath)
    }

    private fun readCafeData(context: Context): CafeData {
        val localVersion = PreferenceManager(context).getString(PrefConst.VERSION_LOCAL, "")
        var jsonStr = if(Utils.isEmptyString(localVersion)) loadJSONFromAsset(context) else loadJsonFromFile(context)
        if(jsonStr == null) {
            jsonStr = loadJSONFromAsset(context)
            PreferenceManager(context).put(PrefConst.VERSION_LOCAL, "")
        }
//        var cafeData: CafeData? = null
//        try {
             val cafeData = Gson().fromJson(jsonStr, CafeData::class.java)
//        } catch (e : Exception) {
//
//        }
        return cafeData
    }

    private fun loadJsonFromFile(context: Context): String? {
        val fis = context.openFileInput("cafeItems.json")
        val isr = InputStreamReader(fis)
        val bufferedReader = BufferedReader(isr)
        val sb = StringBuilder()
        var line: String?
        do {
            line = bufferedReader.readLine()
            if(line == null) break
            sb.append(line)
        } while (line != null)

        return sb.toString()
    }

    private fun loadJSONFromAsset(context: Context): String? {
        val json: String
        try {
            val `is` = context.assets.open("cafeItems.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }

    public interface UpdateCheckListener {
        fun needUpdate(versionDate: String)
    }
}