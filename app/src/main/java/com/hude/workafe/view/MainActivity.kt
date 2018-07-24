package com.hude.workafe.view

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.hude.workafe.R
import com.hude.workafe.manager.downloader.CafeDataLoader
import com.hude.workafe.manager.downloader.DownloadTask
import com.hude.workafe.manager.preference.PrefConst
import com.hude.workafe.manager.preference.PreferenceManager
import com.hude.workafe.model.CafeData
import com.hude.workafe.model.CafeFilter
import com.hude.workafe.model.CafeItem
import com.hude.workafe.utils.BackPressCloseHandler
import com.hude.workafe.utils.Constants
import com.hude.workafe.utils.Utils
import com.hude.workafe.view.headerControlView.HeaderControlPresenter
import com.hude.workafe.view.headerControlView.HeaderControlViewContract
import com.hude.workafe.view.headerControlView.SWITCH_LIST
import com.hude.workafe.view.itemListView.ListViewPresenter
import com.hude.workafe.view.itemMapView.MapViewContract
import com.hude.workafe.view.itemMapView.MapViewPresenter
import com.polyak.iconswitch.IconSwitch
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : VuiActionBarActivity(),
        IconSwitch.CheckedChangeListener, HeaderControlViewContract.OnFilterSetListener,
        MapViewContract.WillSortItemsCallback, DownloadTask.OnProgressListener {

    private val cafeDataLoader = CafeDataLoader()
    private var versionDate: String? = null
    private var downloadProgressDialog: ProgressDialog? = null
    private val cafeList = ArrayList<CafeItem>()
    private var displayList: List<CafeItem> = ArrayList()
    private var backPressCloseHandler: BackPressCloseHandler = BackPressCloseHandler(this)

    private var mapViewPresenter: MapViewPresenter? = null
    private val listViewPresenter = ListViewPresenter()
    private val headerControlPresenter = HeaderControlPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initMapView()
        initListView()
        initHeaderControlView()
        loadItem()
        checkForUpdate()
    }

    override fun onBackPressed() {
        backPressCloseHandler.onBackPressed()
    }

    private fun loadItem() {
        val cafeData: CafeData = cafeDataLoader.loadCafeData(this)
        cafeList.addAll(cafeData.items.filterNotNull())
        mapViewPresenter?.setCafeData(cafeData)
        setCafeListToView()
    }
    private fun setCafeListToView(list: List<CafeItem> = cafeList) {
        displayList = list
        mapViewPresenter?.setCafeList(list)
        listViewPresenter.setCafeList(list)
    }

    private fun checkForUpdate() {
        cafeDataLoader.checkForUpdate(this, object : CafeDataLoader.UpdateCheckListener{
            override fun needUpdate(versionDate: String) {
                showFileUpdateDialog(versionDate)
            }
        })
    }

    private fun showFileUpdateDialog(versionDate: String) {
        this.versionDate = versionDate
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.confirm_update)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    cafeDataLoader.downloadCafeData(this, versionDate, this)

                    if(downloadProgressDialog == null) {
                        downloadProgressDialog = ProgressDialog(this)
                        downloadProgressDialog?.let {
                            it.setMessage(getString(R.string.dialog_downloading))
                            it.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                            it.max = 100
                            it.setCancelable(false)
                            it.isIndeterminate = false
                        }
                    }
                    downloadProgressDialog?.let {
                        if (!it.isShowing) {
                            it.show()
                        }
                    }
                }
                .setNegativeButton(R.string.cancel, { _, _ ->
                    PreferenceManager(this).put(PrefConst.SKIP_VERSION, versionDate)
                })
                .show()
    }

    override fun onFilterSet(filter: CafeFilter) {
        val filterList = cafeList.filter { item ->
            if(filter.opened && !item.isOpen()) {
                return@filter false
            }
            if(filter.smoking && !item.hasSmoking) {
                return@filter false
            }
            if(filter.wifi && !item.hasWifi) {
                return@filter false
            }
            true
        }
        setCafeListToView(filterList)
    }

    override fun sortItemsByLocation(latLng: LatLng) {
        cafeList.sortWith(Comparator { o1, o2 ->
            if(o1.distance == null) {
                o1.distance = Utils.distance(o1.latLng, latLng)
            }
            if(o2.distance == null) {
                o2.distance = Utils.distance(o2.latLng, latLng)
            }
            diffDistance(o1.distance, o2.distance)
        })
        setCafeListToView()
    }
    private fun diffDistance(d1: Double?, d2:Double?) : Int {
        if(d1 != null && d2 != null) {
            return (d1 - d2).toInt()
        }
        return 0
    }

    private fun initMapView() {
        val mapViewPresenter = MapViewPresenter(main_map)
        this.mapViewPresenter = mapViewPresenter
        mapViewPresenter.willSortItemsCallback = this

        main_map.setPresenter(mapViewPresenter)
        mapViewPresenter.initMap(this as AppCompatActivity)

        mapViewPresenter.setBannerView(main_detail_banner)
    }

    private fun initListView() {
        main_list.setPresenter(listViewPresenter)
    }

    private fun initHeaderControlView() {
        headerControlPresenter.checkedChangeListener = this
        headerControlPresenter.onFilterSetListener = this
        main_header_control.setPresenter(headerControlPresenter)
    }

    override fun onCheckChanged(current: IconSwitch.Checked?) {
        current?.let {
            main_list.visibility = when (it.ordinal) {
                SWITCH_LIST -> View.VISIBLE
                else -> View.INVISIBLE
            }
        }
    }

    /*
     * OnProgressListener for Download Task
     */
    override fun onProgressUpdate(progress: Int) {
        runOnUiThread({
            downloadProgressDialog?.progress = progress
        })
    }

    override fun onFinish() {
        PreferenceManager(this).put(PrefConst.VERSION_LOCAL, versionDate)
        loadItem()
        downloadProgressDialog?.dismiss()
    }

    override fun onFail() {
        downloadProgressDialog?.dismiss()
        Toast.makeText(this, getString(R.string.failed_to_download), Toast.LENGTH_SHORT).show()
    }

    /*
     * Permission request result
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in grantResults.indices) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.need_permission), Toast.LENGTH_SHORT).show()
                return
            }
        }
        if(requestCode == Constants.REQUEST_PERMISSIONS_LOCATION) {
            mapViewPresenter?.getMyLocation(this)
        }
    }
}