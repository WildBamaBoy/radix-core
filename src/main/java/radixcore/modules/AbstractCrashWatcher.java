package radixcore.modules;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;

import radixcore.core.RadixCore;

public abstract class AbstractCrashWatcher 
{
	private final long startupTimestamp;

	public AbstractCrashWatcher()
	{
		startupTimestamp = new Date().getTime();
	}

	public void checkForCrashReports()
	{
		//Build a list of crash reports
		File crashReportsFolder = new File(RadixCore.getRunningDirectory() + "/crash-reports/");
		File[] crashReportFiles = crashReportsFolder.listFiles(new FileFilter() 
		{			
			public boolean accept(File file) 
			{
				return file.isFile();
			}
		});

		if (crashReportFiles != null)
		{
			//Find the one with the latest timestamp
			long lastModifiedTime = Long.MIN_VALUE;
			File lastModifiedFile = null;
	
			for (File file : crashReportFiles) 
			{
				if (file.lastModified() > lastModifiedTime) 
				{
					lastModifiedFile = file;
					lastModifiedTime = file.lastModified();
				}
			}
	
			//Handle the crash
			if (lastModifiedTime > startupTimestamp)
			{
				onCrash(lastModifiedFile);
			}
		}
	}

	protected abstract void onCrash(File crashFile);
}
