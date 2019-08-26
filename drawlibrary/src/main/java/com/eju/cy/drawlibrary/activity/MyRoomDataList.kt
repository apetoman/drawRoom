package com.eju.cy.drawlibrary.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.eju.cy.drawlibrary.R
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.eju.cy.drawlibrary.adapter.MyRoomDataAdapter
import com.eju.cy.drawlibrary.bean.MyRoomData
import com.eju.cy.drawlibrary.bean.OpenRoomDto
import com.eju.cy.drawlibrary.bean.ResultDto
import com.eju.cy.drawlibrary.net.DrawRoomInterface
import com.eju.cy.drawlibrary.plug.EjuDrawBleEventCar
import com.eju.cy.drawlibrary.utils.GridSpacingItemDecoration
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_my_room_data_list.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.HashMap


class MyRoomDataList : AppCompatActivity(), OnRefreshListener, OnLoadMoreListener {

    lateinit var adapter: MyRoomDataAdapter
    var dataList = arrayListOf<MyRoomData.DataBean.RecordsBean>()
    var start_index: Int = 0

    lateinit var compositeDisposable: CompositeDisposable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_room_data_list)

        initView()
    }

    private fun initView() {


        iv_back.setOnClickListener {

            finish()

        }

        compositeDisposable = CompositeDisposable()
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)
        adapter = MyRoomDataAdapter(R.layout.item_room_data_list_layout, dataList)
        val layoutManager = GridLayoutManager(this, 2)
        rl_list.layoutManager = layoutManager
        rl_list.adapter = adapter
        rl_list.addItemDecoration(GridSpacingItemDecoration(2, 30, false))
        getData(true)

        adapter.onItemClickListener = object : BaseQuickAdapter.OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {

                getRoomDetail(dataList.get(position).no)


            }

        }


    }


    override fun onLoadMore(refreshLayout: RefreshLayout) {
        refreshLayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
        start_index = dataList.size
        getData(false)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        start_index = 0
        getData(true)
    }


    private fun getRoomDetail(no: String) {

        val headersMap = HashMap<String, String>()

        headersMap["User-Id"] = "9027"
        headersMap["User-Token"] = "cbcf71e1f8538fd11a0761fc759ffc5918bc092f"
        headersMap["X-REQUESTED-WITH"] = "json"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://yun.jiandanhome.com/")
            .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
            .build()

        val obRequest = retrofit.create(DrawRoomInterface::class.java)

        obRequest.getDetail(headersMap, no)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ResultDto<String>> {
                override fun onComplete() {
                    LogUtils.w("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                    LogUtils.w("onSubscribe")
                }

                override fun onNext(t: ResultDto<String>) {


                    if (t.isOk) {
                        LogUtils.w("onNext" + t.data+"\n啥线程"+Thread.currentThread().name)

                        var roomData = OpenRoomDto()
                        roomData.no = no
                        roomData.data = t.data
                        roomData.addOrUpdata = true
                        EjuDrawBleEventCar.getDefault().post(roomData)
                        finish()
                    }

                }

                override fun onError(e: Throwable) {
                    LogUtils.w("onError")
                }
            })


    }

    private fun getData(isRefresh: Boolean) {


        val headersMap = HashMap<String, String>()

        headersMap["User-Id"] = "9027"
        headersMap["User-Token"] = "cbcf71e1f8538fd11a0761fc759ffc5918bc092f"
        headersMap["X-REQUESTED-WITH"] = "json"


        val retrofit = Retrofit.Builder()
            .baseUrl("https://yun.jiandanhome.com/")
            .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
            .build()


        val obRequest = retrofit.create(DrawRoomInterface::class.java)


        obRequest.getMyRoomList(
            headersMap,
            start_index.toString(), "20"
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MyRoomData> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                    compositeDisposable.add(d)
                }

                override fun onNext(t: MyRoomData) {


                    if (null != t && t.code == "10000" || t.code == "0" && null != t.data.records && t.data.records.size > 0) {

                        if (isRefresh) {
                            dataList.clear()
                            dataList.addAll(t!!.data.records)
                            adapter!!.notifyDataSetChanged()
                        } else {
                            dataList.addAll(t!!.data.records)
                            adapter!!.notifyDataSetChanged()
                        }

                    }


                    LogUtils.w("dataList大小--" + t.data.records.size)

                }

                override fun onError(e: Throwable) {

                }

            })


    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


}
