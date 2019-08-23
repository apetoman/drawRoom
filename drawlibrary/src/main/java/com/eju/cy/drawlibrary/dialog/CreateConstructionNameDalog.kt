package com.eju.cy.drawlibrary.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.ToastUtils
import com.eju.cy.drawlibrary.R
import com.eju.cy.drawlibrary.plug.DialogInterface
import kotlinx.android.synthetic.main.dialog_crea_name_layout.*
import kotlinx.android.synthetic.main.dialog_crea_name_layout.view.*

class CreateConstructionNameDalog(dialogInterface: DialogInterface) : DialogFragment(),
    View.OnClickListener {

    lateinit var dialogInterface: DialogInterface

    init {
        this.dialogInterface = dialogInterface
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val params = dialog.window!!
            .attributes
        params.gravity = Gravity.CENTER//居中
        params.windowAnimations = R.style.bottomSheet_animation
        dialog.window!!.attributes = params
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)//边缘不可消失
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val view = inflater.inflate(R.layout.dialog_crea_name_layout, container, false)
        view.tv_create_construction.setOnClickListener(this)
        view.iv_close.setOnClickListener(this)


        return view
    }

    override fun onClick(v: View?) {

        when (v) {
            tv_create_construction -> {

                if (ed_ed_construction_name.text.toString().isNotEmpty()) {

                    dialogInterface.dialogCommit(ed_ed_construction_name.text.toString())
                    dismiss()
                }else{
                    ToastUtils.showShort("工地名称不能为空")
                }

            }

            iv_close -> {
                dismiss()
            }


        }

    }


}