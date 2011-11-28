package com.otdshco.common;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.otdshco.uninstall.R;
import com.otdshco.tools.Logger;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MyAdapter extends
		SimpleAdapter
{
	private static final String				LOG_MAIN	="ListAdapter";
	private List<? extends Map<String,?>>	resourceNames;
	private OnItemClickListener				listener	=null;
	private ArrayList<Integer>				selectedItems;
	private String[]						strKeys;
	private ArrayList<Drawable>				drawableArray;
	private int								res;

	public MyAdapter(	Context context,
						List<? extends Map<String,?>> data,
						int resource,
						String[] from,
						int[] to,
						ArrayList<Integer> selItems,
						ArrayList<Drawable> da)
	{
		super(	context,
				data,
				resource,
				from,
				to);
		res=resource;
		selectedItems=selItems;
		resourceNames=data;
		strKeys=from;
		drawableArray=da;
		log("---------------------------MyAdapter");
	}

	@Override
	public View getView(int position,
						View convertView,
						ViewGroup parent)
	{
		ViewHolder holder;
		if(listener==null)
		{
			listener=new OnItemClickListener(	selectedItems,
												res);
		}
		if(convertView==null)
		{
			holder=new ViewHolder();
			convertView=LayoutInflater.from(parent.getContext())
										.inflate(	res,
													null);
			holder.tv1=(TextView)convertView.findViewById(R.id.text1);
			holder.tv2=(TextView)convertView.findViewById(R.id.text2);
			holder.tv3=(TextView)convertView.findViewById(R.id.text3);
			holder.img=(ImageView)convertView.findViewById(R.id.img);
			holder.ckb=(CheckBox)convertView.findViewById(R.id.ckb);
			convertView.setTag(holder);
		}
		else
		{
			holder=(ViewHolder)convertView.getTag();
		}
		Map<String,?> currentData=resourceNames.get(position);
		holder.tv1.setText(currentData.get(strKeys[0])
										.toString());
		holder.tv2.setText(currentData.get(strKeys[1])
										.toString());
		holder.tv3.setText(currentData.get(strKeys[2])
										.toString());
		// draw=convertView.getContext().getApplicationInfo().loadIcon(convertView.getContext().getPackageManager());
		holder.img.setImageDrawable(drawableArray.get((Integer)currentData.get(strKeys[3])));
		holder.ckb.setChecked(selectedItems.contains((Integer)currentData.get(strKeys[4])));
		convertView.setId((Integer)currentData.get(strKeys[4]));
		convertView.setOnClickListener(listener);
		return convertView;
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
