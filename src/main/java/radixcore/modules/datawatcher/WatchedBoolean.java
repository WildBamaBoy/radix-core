package radixcore.modules.datawatcher;


public class WatchedBoolean extends AbstractWatched
{
	public WatchedBoolean(boolean value, int dataWatcherId, DataWatcherEx dataWatcher)
	{
		super(value, dataWatcher, dataWatcherId);
	}
}
