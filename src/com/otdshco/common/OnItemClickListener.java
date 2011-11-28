package com.otdshco.common;
import java.util.ArrayList;
import com.otdshco.tools.Logger;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import com.otdshco.uninstall.R;

public class OnItemClickListener implements
								OnClickListener
{
	private static final String	LOG_MAIN				="ClickListener";
	private ArrayList<Integer>	selectedItems;
	private String				ACTION_VALUE			=Common.ACTION_VALUE;
	private final int			ACTION_VALUE_ON_CLICK	=Common.ACTION_VALUE_ON_CLICK;
	private int					res;

	public OnItemClickListener(	ArrayList<Integer> selectItems,
								int r)
	{
		log("     [OnItemClickListener]");
		selectedItems=selectItems;
		res=r;
	}

	public void onClick(View v)
	{
		log("     [onClick]");
		CheckBox ckb=(CheckBox)v.findViewById(R.id.ckb);
		boolean checked=ckb.isChecked();
		if(checked)
		{
			selectedItems.remove(new Integer(v.getId()));
		}
		else
		{
			selectedItems.add(v.getId());
		}
		updateStatusBar(v.getContext());
		ckb.setChecked(!checked);
	}

	private void updateStatusBar(Context ctx)
	{
		log("     [updateStatusBar]");
		Intent intent=new Intent();
		intent.setAction(String.valueOf(res));
		intent.putExtra(ACTION_VALUE,
						ACTION_VALUE_ON_CLICK);
		ctx.sendBroadcast(intent);
	}

	private void log(String logMessage)
	{
		if(logMessage.startsWith(" ")||
			logMessage.startsWith("!"))
		{
			String clazz=Thread.currentThread()
								.getStackTrace()[3].getClassName();
			String metho=Thread.currentThread()
								.getStackTrace()[3].getMethodName();
			logMessage=logMessage+
						" ["+
						clazz.substring(clazz.lastIndexOf(".")+1)+
						"."+
						metho+
						"]";
		}
		Logger.log(	LOG_MAIN,
					logMessage,
					Logger.TOOLS_UTIL);
	}
}
