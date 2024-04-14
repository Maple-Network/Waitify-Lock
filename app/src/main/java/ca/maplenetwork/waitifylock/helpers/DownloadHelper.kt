package ca.maplenetwork.waitifylock.helpers

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import ca.maplenetwork.waitifylock.BuildConfig
import ca.maplenetwork.waitifylock.R
import ca.maplenetwork.waitifylock.Variables
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.security.MessageDigest
import java.util.regex.Pattern
import javax.net.ssl.HttpsURLConnection


object DownloadHelper {
    private const val TAG = "DownloadHelper"
    private fun downloadFile(fileUrl: URL, fileLocation: File): Boolean {
        var connection: HttpsURLConnection? = null
        try {
            connection = fileUrl.openConnection() as HttpsURLConnection
            connection.connect()

            val inputStream = connection.inputStream
            val outputStream = FileOutputStream(fileLocation)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            return true
        } catch (_: Exception) {}
        finally {
            connection?.disconnect()
        }
        return false
    }
    private fun readHashFromUrl(url: URL): String {
        var connection: HttpsURLConnection? = null
        try {
            connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            val inputStream = connection.inputStream

            val dirtyHash = inputStream.bufferedReader().use { it.readText() }

            return dirtyHash.replace("\\s+".toRegex(), "")
        } finally {
            connection?.disconnect()
        }
    }
    private fun getFileHash(file: File): String? {
        try {
            // Create a FileInputStream for the file
            val inputStream = FileInputStream(file)

            // Get an instance of MessageDigest for SHA-256
            val digest = MessageDigest.getInstance("SHA-256")

            // Create a buffer
            val buffer = ByteArray(1024)
            var bytesRead: Int

            // Read the file's bytes and update the digest
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }

            // Close the input stream
            inputStream.close()

            // Convert the digest bytes to a hex string
            return digest.digest().joinToString("") {
                String.format("%02x", it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Return null in case of error
        return null
    }
    private fun downloadWaitifyLock(versionInfo: ReleaseInfo, url: URL, context: Context): File {
        val downloadDir = File(context.getExternalFilesDir(null), "apk/waitify_lock")
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }

        val apkName = "${versionInfo.version}.apk"

        val fileLocation = File(downloadDir, apkName)
        if (!fileLocation.exists()) {
            if (!downloadFile(url, fileLocation)) throw Exception("Failed to download apk")
        }

        val hash = getFileHash(fileLocation)
        if (hash != versionInfo.hash) {
            deleteAllWaitifyLockApk(context)
            throw Exception("Hash mismatch")
        }

        return fileLocation
    }
    fun deleteAllWaitifyLockApk(context: Context) {
        val downloadDir = File(context.getExternalFilesDir(null), "apk/waitify_lock")
        if (downloadDir.exists()) {
            Log.d(TAG, "deleteAllWaitifyLockApk: Deleting apk files")
            downloadDir.deleteRecursively()
        }
    }
    private fun fetchLatestRelease(): ReleaseInfo? {
        val url = URL("https://waitify.maplenetwork.ca/waitifylock/api")
        var connection: HttpsURLConnection? = null
        try {
            connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(response)

                val tag = jsonObject.getString("tag_name")
                val pattern: Pattern = Pattern.compile("^v\\d+$")

                if (!pattern.matcher(tag).matches()) {
                    throw Exception("Invalid tag format")
                }

                val versionNumber = tag.replace("v", "").toInt()

                val assetArray = jsonObject.getJSONArray("assets")

                var apkDownloadUrl: URL? = null
                var hash: String? = null

                for (i in 0 until assetArray.length()) {
                    val asset = assetArray.getJSONObject(i)
                    val name = asset.getString("name")
                    if (name.contains(".apk")) {
                        apkDownloadUrl = URL(asset.getString("browser_download_url"))
                    } else if (name == "hash.txt") {
                        hash = readHashFromUrl(URL(asset.getString("browser_download_url")))
                    }

                    if (apkDownloadUrl != null && hash != null) {
                        break
                    }
                }

                if (apkDownloadUrl == null || hash == null) {
                    throw Exception("Failed to find apk download url or hash")
                }
                return ReleaseInfo(versionNumber, apkDownloadUrl, hash)
            }
        } catch (e: Exception) {
            Log.w(TAG, "fetchLatestRelease: ", e)
        } finally {
            connection?.disconnect()
        }
        return null
    }
    data class ReleaseInfo(
        val version: Int,
        val url: URL,
        val hash: String
    )
    private fun getCurrentAppVersion(): Int {
        return BuildConfig.VERSION_CODE
    }
    private fun installApk(file: File, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file), "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }
    fun downloadAndPromptInstall(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val releaseInfo = fetchLatestRelease()
            Log.d(TAG, "downloadAndInstall: ${releaseInfo?.version} ${releaseInfo?.url} ${releaseInfo?.hash}")
            if (releaseInfo != null && releaseInfo.version > getCurrentAppVersion()) {
                try {
                    val apk = downloadWaitifyLock(releaseInfo, releaseInfo.url, context)
                    if (Variables.AppLocked(context)) return@launch
                    CoroutineScope(Dispatchers.Main).launch {
                        AlertDialog.Builder(context).apply {
                            setTitle(context.getString(R.string.download_update_title))
                            setMessage(context.getString(R.string.download_update_message))
                            setPositiveButton(context.getString(R.string.install_button)) { _, _ ->
                                installApk(apk, context)
                            }
                            setNegativeButton(context.getString(R.string.cancel_button)) { dialog, _ -> dialog.dismiss() }
                            show()
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "downloadAndInstall: ", e)
                }
            }
        }
    }
}