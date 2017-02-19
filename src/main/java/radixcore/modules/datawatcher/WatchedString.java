package radixcore.modules.datawatcher;

@Deprecated
public class WatchedString extends AbstractWatched
{
	public WatchedString(String value, int dataWatcherId, DataWatcherEx dataWatcher)
	{
		super(value, dataWatcher, dataWatcherId);
	}
}
