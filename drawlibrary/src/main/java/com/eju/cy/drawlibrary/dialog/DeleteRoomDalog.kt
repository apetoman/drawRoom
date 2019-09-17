package com.eju.cy.drawlibrary.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.eju.cy.drawlibrary.R
import com.eju.cy.drawlibrary.plug.DialogInterface
import kotlinx.android.synthetic.main.dialog_delete_room_layout.*
import kotlinx.android.synthetic.main.dialog_delete_room_layout.view.*


class DeleteRoomDalog(
    dialogInterface: DialogInterface,
    title: String,
    message: String,
    determine: String,
    cancel: String
) : DialogFragment(),
    View.OnClickListener {

    lateinit var dialogInterface: DialogInterface
    var title: String = ""
    var message: String = ""
    var determine: String = ""
    var cancel: String = ""


    init {
        this.dialogInterface = dialogInterface
        this.title = title
        this.message = message
        this.determine = determine
        this.cancel = cancel
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

        val view = inflater.inflate(R.layout.dialog_delete_room_layout, container, false)
        view.tv_cancel.setOnClickListener(this)
        view.tv_determine.setOnClickListener(this)

        view.tv_title.setText(title)
        view.tv_message.setText(message)
        view.tv_determine.setText(determine)
        view.tv_cancel.setText(cancel)


        return view
    }

    override fun onClick(v: View?) {
        when (v) {
            tv_cancel -> {
                dialogInterface.dialogFinish()
                dismiss()

            }

            tv_determine -> {
                dialogInterface.dialogCommit("")
                dismiss()
            }
        }

    }


}