package radixcore.modules.datawatcher;

public class WatchedFloat extends AbstractWatched
{
	public WatchedFloat(float value, int dataWatcherId, DataWatcherEx dataWatcher)
	{
		super(value, dataWatcher, dataWatcherId);
	}
}
