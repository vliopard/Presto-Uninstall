package com.otdshco.uninstall;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.otdshco.common.MyAdapter;
import com.otdshco.uninstall.R;
import com.otdshco.tools.Logger;
import com.otdshco.tools.Utilities;
import com.otdshco.common.Common;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;

public class Uninstall extends
		ListActivity implements
					OnClickListener
{
	private static final String			LOG_MAIN							="Uninstall";
	private String						THREAD_WORKING						=Common.THREAD_WORKING;
	private String						DONE_WORKING						=Common.DONE_WORKING;
	private String						SETTINGS_ID							=Common.SETTINGS_ID;
	private String						ACTION_VALUE						=Common.ACTION_VALUE;
	private final String				TEXT_KEY_3							=Common.TEXT_KEY_3;
	private final String				ITEM_ID								=Common.ITEM_ID;
	private final String				IMG_KEY								=Common.IMG_KEY;
	private final String				SELECTED_ITEM_KEY					=Common.SELECTED_ITEM_KEY;
	private final static String			TEXT_KEY_1							=Common.TEXT_KEY_1;
	private final static String			TEXT_KEY_2							=Common.TEXT_KEY_2;
	private int							SETTINGS_ACTIVITY					=Common.SETTINGS_ACTIVITY;
	private final int					CREATE_DIALOG						=Common.CREATE_DIALOG;
	private final int					ACTION_VALUE_REFRESH_LIST_ADAPTER	=Common.ACTION_VALUE_REFRESH_LIST_ADAPTER;
	private final int					ACTION_VALUE_ON_CLICK				=Common.ACTION_VALUE_ON_CLICK;
	private String						translate_TOTAL_APPS				=Common.translate_TOTAL_APPS;
	private String						translate_SELECTED_APPS				=Common.translate_SELECTED_APPS;
	private String						translate_UNINSTALLING_MESSAGE		=Common.translate_UNINSTALLING_MESSAGE;
	private String						translate_LOADING_TITLE				=Common.translate_LOADING_TITLE;
	private String						translate_LOADING_MESSAGE			=Common.translate_LOADING_MESSAGE;
	private String						translate_DONE_STATUS				=Common.translate_DONE_STATUS;
	private String						translate_UNINSTALL_BTN				=Common.translate_UNINSTALL_BTN;
	private String						translate_SETTINGS_BTN				=Common.translate_SETTINGS_BTN;
	private String						translate_CURRENT_WORKING			=Common.translate_CURRENT_WORKING;
	private String						translate_WAIT_MSG					=Common.translate_WAIT_MSG;
	private String						translate_IO_ERROR					=Common.translate_IO_ERROR;
	private String						translate_NO_FILE_SELECTION			=Common.translate_NO_FILE_SELECTION;
	private String						UNINSTALL_CHECKBOX					=Common.UNINSTALL_CHECKBOX;
	private String						UNINSTALL_LIST_TYPE					=Common.UNINSTALL_LIST_TYPE;
	private String						UNINSTALL_LIST_ORDER				=Common.UNINSTALL_LIST_ORDER;
	private String						UNINSTALL_LIST_ORDER_VALUE			=Common.UNINSTALL_LIST_ORDER_VALUE;
	private String						UNINSTALL_LIST_TYPE_VALUE			=Common.UNINSTALL_LIST_TYPE_VALUE;
	private String						ACTION								=String.valueOf(R.layout.uninstall_row);
	private int							listTotalItemsCount					=0;
	private boolean						uninstallBlinking					=true;
	private boolean						UNINSTALL_CHECKBOX_VALUE			=false;
	private String						auxMessage;
	private String						uninstallStringView;
	private Button						uninstallButton;
	private Button						uninstallSettingsButton;
	private TextView					uninstallTextView;
	private Thread						uninstallProcess;
	private SharedPreferences			sharedPreferences;
	private ArrayList<String>			applicationsArray					=new ArrayList<String>();
	private ArrayList<Integer>			selectedItems						=new ArrayList<Integer>();
	private ArrayList<Drawable>			drawableArray						=new ArrayList<Drawable>();
	private Handler						uninstallHandler					=new Handler();
	private MessageReceiver				receiver							=new MessageReceiver();
	private MyAdapter					notes								=null;
	private List<Map<String,Object>>	resourceNames						=null;

	private void startUserInterfaceElements()
	{
		log("                              ");
		setContentView(R.layout.uninstall_main);
		uninstallButton=(Button)findViewById(R.id.uninstall_button);
		uninstallButton.setOnClickListener(this);
		uninstallSettingsButton=(Button)findViewById(R.id.uninstall_settings_button);
		uninstallSettingsButton.setOnClickListener(this);
		uninstallTextView=(TextView)findViewById(R.id.text_v);
	}

	private void registerBroadcastReceiver()
	{
		log("                              ");
		IntentFilter filter=new IntentFilter(ACTION);
		registerReceiver(	receiver,
							filter);
	}

	private void unregisgerBroadcastReceiver()
	{
		log("                              ");
		unregisterReceiver(receiver);
	}

	private void executeLoadApplicationsListBackgroundTask()
	{
		log("                              ");
		new TaskLoader().execute();
	}

	private void generateApplicationsList()
	{
		log("                              ");
		getApplicationsListData();
		createApplicationsListAdapter();
	}

	private void getApplicationsListData()
	{
		log("                              ");
		resourceNames=new ArrayList<Map<String,Object>>();
		listTotalItemsCount=Utilities.generateData(	resourceNames,
													getSort(),
													drawableArray,
													applicationsArray,
													this);
	}

	private void createApplicationsListAdapter()
	{
		log("                              ");
		notes=new MyAdapter(this,
							resourceNames,
							R.layout.uninstall_row,
							new String[]
							{
									TEXT_KEY_1,
									TEXT_KEY_2,
									TEXT_KEY_3,
									IMG_KEY,
									ITEM_ID
							},
							new int[]
							{
									R.id.text1,
									R.id.text2,
									R.id.text3,
									R.id.img
							},
							selectedItems,
							drawableArray);
	}

	public void updateStatusBarItemsCount()
	{
		log("                              ");
		printMessage(translate_TOTAL_APPS+
						getListTotalItemsCount()+
						" ] - [ "+
						translate_SELECTED_APPS+
						selectedItems.size());
	}

	private UninstallThread getUninstallProcess()
	{
		log("                              ");
		return (UninstallThread)uninstallProcess;
	}

	private Runnable	uninstallUpdateTask	=new Runnable()
												{
													public void run()
													{
														if((uninstallProcess!=null))
														{
															if(!isRoot())
															{
																getUninstallProcess().update();
															}
															auxMessage=translate_UNINSTALLING_MESSAGE+
																		getUninstallProcess().get()+
																		"...";
															if((getUninstallProcess().isWorking().equalsIgnoreCase(THREAD_WORKING)))
															{
																if(uninstallBlinking)
																{
																	setMessage(auxMessage);
																	uninstallBlinking=false;
																}
																else
																{
																	setMessage("");
																	uninstallBlinking=true;
																}
															}
															if((getUninstallProcess().isWorking().equalsIgnoreCase(DONE_WORKING)))
															{
																uninstallProcess.stop();
																uninstallProcess=null;
																if(uninstallButton!=null)
																{
																	uninstallButton.setText(translate_UNINSTALL_BTN);
																	uninstallButton.setClickable(true);
																	uninstallSettingsButton.setText(translate_SETTINGS_BTN);
																	uninstallSettingsButton.setClickable(true);
																}
																removeUninstalledItemsFromApplicationList();
																removeUninstalledApplicationsFromSelectedItemsList();
																refreshListAdapterUsingInvalidateAndNotifyDataSetChanged();
																setMessage(translate_DONE_STATUS);
															}
														}
														uninstallHandler.postDelayed(	this,
																						750);
													}
												};

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		log("                              ");
		startUserInterfaceElements();
		registerBroadcastReceiver();
		executeLoadApplicationsListBackgroundTask();
	}

	@Override
	protected void onRestoreInstanceState(Bundle state)
	{
		super.onRestoreInstanceState(state);
		log("                              ");
		selectedItems.addAll(state.getIntegerArrayList(SELECTED_ITEM_KEY));
		setMessage(state.getString(SETTINGS_ID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		log("                              ");
		outState.putIntegerArrayList(	SELECTED_ITEM_KEY,
										selectedItems);
		outState.putString(	SETTINGS_ID,
							uninstallStringView);
	}

	public void onClick(View target)
	{
		log("                              ");
		Button restoreButton=(Button)target.findViewById(R.id.uninstall_button);
		Button settingsButton=(Button)target.findViewById(R.id.uninstall_settings_button);
		if(restoreButton!=null)
		{
			ArrayList<String> apkList=new ArrayList<String>();
			for(int i=selectedItems.size()-1; i>=0; i--)
			{
				apkList.add(applicationsArray.get(selectedItems.get(i)));
			}
			if((!(apkList.isEmpty()))||
				((apkList.size()>0)))
			{
				try
				{
					uninstallProcess=new UninstallThread(	isRoot(),
															apkList,
															this);
				}
				catch(IOException ioe)
				{
					setMessage(translate_IO_ERROR);
				}
				uninstallProcess.start();
				uninstallButton.setText(translate_CURRENT_WORKING);
				uninstallButton.setClickable(false);
				uninstallSettingsButton.setText(translate_WAIT_MSG);
				uninstallSettingsButton.setClickable(false);
			}
			else
			{
				printMessage(translate_NO_FILE_SELECTION);
			}
		}
		if(settingsButton!=null)
		{
			startActivityForResult(	new Intent(	this,
												UninstallSettings.class),
									SETTINGS_ACTIVITY);
		}
	}

	@Override
	public void onActivityResult(	int requestCode,
									int resultCode,
									Intent data)
	{
		log("                              ");
		Utilities.sortList(	resourceNames,
							getSort());
		refreshListAdapterUsingInvalidateAndNotifyDataSetChanged();
	}

	private void refreshSetListAdapterNotes()
	{
		log("                              ");
		if(notes!=null)
		{
			log("                              [setListAdapter]");
			setListAdapter(notes);
		}
		updateStatusBarItemsCount();
	}

	private void refreshListAdapterUsingInvalidateAndNotifyDataSetChanged()
	{
		log("                              ");
		if(notes!=null)
		{
			log("                              [notifyDataSetChanged]");
			// this.getListView().invalidate();
			// notes.notifyDataSetInvalidated();
			notes.notifyDataSetChanged();
		}
		updateStatusBarItemsCount();
	}

	private void removeUninstalledApplicationsFromSelectedItemsList()
	{
		log("                              ");
		selectedItems.clear();
	}

	private void removeUninstalledItemsFromApplicationList()
	{
		log("                              ");
		if(isRoot())
		{
			for(int i=0; i<resourceNames.size(); i++)
			{
				Map<String,?> currentData=resourceNames.get(i);
				if(selectedItems.contains((Integer)currentData.get("ID1")))
				{
					// applicationsArray.remove(selectedItems.get(i));
					resourceNames.remove(i);
					listTotalItemsCount--;
				}
			}
		}
		else
		{
			executeLoadApplicationsListBackgroundTask();
		}
	}

	private int getListTotalItemsCount()
	{
		log("                              ");
		return listTotalItemsCount;
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		log("                              ");
		uninstallHandler.removeCallbacks(uninstallUpdateTask);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		log("                              ");
		uninstallHandler.removeCallbacks(uninstallUpdateTask);
		uninstallHandler.postDelayed(	uninstallUpdateTask,
										800);
	}

	@Override
	protected void onPostResume()
	{
		super.onPostResume();
		log("                              ");
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		log("                              ");
		if(uninstallHandler!=null)
		{
			uninstallHandler.removeCallbacks(uninstallUpdateTask);
			uninstallHandler=null;
		}
		if(uninstallProcess!=null)
		{
			getUninstallProcess().exit();
			uninstallProcess=null;
		}
		unregisgerBroadcastReceiver();
	}

	private boolean isRoot()
	{
		log("                              ");
		sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		return sharedPreferences.getBoolean(UNINSTALL_CHECKBOX,
											UNINSTALL_CHECKBOX_VALUE);
	}

	public int getSort()
	{
		log("                              ");
		sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String types=sharedPreferences.getString(	UNINSTALL_LIST_TYPE,
													UNINSTALL_LIST_TYPE_VALUE);
		String orders=sharedPreferences.getString(	UNINSTALL_LIST_ORDER,
													UNINSTALL_LIST_ORDER_VALUE);
		return Utilities.getOrder(	types,
									orders);
	}

	public void printMessage(String message)
	{
		log("                              ");
		uninstallTextView.setText("[ "+
									message+
									" ]");
		uninstallStringView="[ "+
							message+
							" ]";
	}

	private void setMessage(String message)
	{
		log("                              ");
		uninstallTextView.setText(message);
		uninstallStringView=message;
	}

	private void sendBroadcastCommandToShowList()
	{
		log("                              ");
		Intent intent=new Intent();
		intent.setAction(ACTION);
		intent.putExtra(ACTION_VALUE,
						ACTION_VALUE_REFRESH_LIST_ADAPTER);
		sendBroadcast(intent);
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
					Logger.MAIN_SOFTWARE);
	}

	public class MessageReceiver extends
			BroadcastReceiver
	{
		@Override
		public void onReceive(	Context context,
								Intent intent)
		{
			if(intent.getAction()
						.equals(ACTION))
			{
				log("MessageReceiver               [onReceive]");
				switch(intent.getExtras()
								.getInt(ACTION_VALUE))
				{
					case ACTION_VALUE_REFRESH_LIST_ADAPTER:
						log("MessageReceiver               [refreshSetListAdapterNotes]");
						refreshSetListAdapterNotes();
					break;
					case ACTION_VALUE_ON_CLICK:
						log("MessageReceiver               [updateStatusBarItemsCount]");
						updateStatusBarItemsCount();
					break;
				}
			}
		}
	}

	private ProgressDialog createDialog()
	{
		log("                              ");
		ProgressDialog builder=new ProgressDialog(this);
		builder.setIndeterminate(true);
		builder.setCancelable(true);
		builder.setTitle(translate_LOADING_TITLE);
		builder.setMessage(translate_LOADING_MESSAGE);
		return builder;
	}

	protected Dialog onCreateDialog(int id)
	{
		log("                              ");
		switch(id)
		{
			case CREATE_DIALOG:
				return createDialog();
			default:
		}
		return null;
	}

	private class TaskLoader extends
			AsyncTask<Object,Object,Object>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			log("TaskLoader                    [onPreExecute]");
			showDialog(CREATE_DIALOG);
		}

		@Override
		protected Object doInBackground(Object... arg0)
		{
			log("TaskLoader                    [doInBackground]");
			generateApplicationsList();
			sendBroadcastCommandToShowList();
			return arg0;
		}

		@Override
		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			log("TaskLoader                    [onPostExecute]");
			log("TaskLoader                    [dismissDialog] IN");
			dismissDialog(CREATE_DIALOG);
			log("TaskLoader                    [dismissDialog] OUT");
		}
	}
}
