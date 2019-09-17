package com.eju.cy.drawlibrary.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.eju.cy.drawlibrary.R
import com.eju.cy.drawlibrary.adapter.MyRoomDataAdapter
import com.eju.cy.drawlibrary.bean.DelectDrawRoomDto
import com.eju.cy.drawlibrary.bean.MyRoomData
import com.eju.cy.drawlibrary.bean.OpenRoomDto
import com.eju.cy.drawlibrary.bean.ResultDto
import com.eju.cy.drawlibrary.dialog.DeleteRoomDalog
import com.eju.cy.drawlibrary.net.RetrofitManager
import com.eju.cy.drawlibrary.plug.DialogInterface
import com.eju.cy.drawlibrary.plug.EjuDrawBleEventCar
import com.eju.cy.drawlibrary.utils.GridSpacingItemDecoration
import com.eju.cy.drawlibrary.utils.ParameterUtils
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_my_room_data_list.*


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


        adapter.onItemLongClickListener =
            BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->


                var deleteRoomDalog = DeleteRoomDalog(object : DialogInterface {
                    override fun dialogCommit(msg: String?) {

                        deleteRoom(dataList[position].no, position)

                    }

                    override fun dialogFinish(msg: String?) {

                    }

                    override fun dialogFinish() {


                    }
                }, "提示", "是否确定删除该户型？", "确定", "取消")


                deleteRoomDalog.show(supportFragmentManager, "deleteRoomDalog")


                true
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


    private fun deleteRoom(no: String, position: Int) {

        val obRequest = RetrofitManager.getDefault().provideClientApi(this)
        ParameterUtils.prepareFormData(no)?.let {
            obRequest.delectDrawingRoom(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<DelectDrawRoomDto> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: DelectDrawRoomDto) {


                        if (t?.code != null && t.code == "10000") {
                            ToastUtils.showShort("户型删除成功")
                            dataList.removeAt(position)
                            adapter.notifyDataSetChanged()
                        } else {
                            ToastUtils.showShort("户型删除失败")
                        }


                    }

                    override fun onError(e: Throwable) {
                        ToastUtils.showShort("户型删除失败")
                    }

                })
        }

    }


    private fun getRoomDetail(no: String) {

        val obRequest = RetrofitManager.getDefault().provideClientApi(this)

        obRequest.getDetail(no)
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


                        var roomData = OpenRoomDto()
                        roomData.no = no
                        roomData.data = t.data
                        roomData.addOrUpdata = true
                        EjuDrawBleEventCar.getDefault().post(roomData)
                        finish()
                    } else {
                        ToastUtils.showShort(t.msg)
                    }


                }

                override fun onError(e: Throwable) {
                    LogUtils.w("onError")
                }
            })


    }

    private fun getData(isRefresh: Boolean) {


        val obRequest = RetrofitManager.getDefault().provideClientApi(this)


        obRequest.getMyRoomList(
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


                    if (null != t && t.code == "10000" || t.code == "0" && null != t.data && null != t.data.records && t.data.records.size > 0) {

                        if (isRefresh) {
                            dataList.clear()
                            dataList.addAll(t!!.data.records)
                            adapter!!.notifyDataSetChanged()
                        } else {
                            dataList.addAll(t!!.data.records)
                            adapter!!.notifyDataSetChanged()
                        }

                    } else {
                        ToastUtils.showShort(t.msg)
                    }


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
