package radixcore.modules;

import java.lang.reflect.Field;

public final class RadixReflect 
{
	public static <T> T getInstanceObjectOfTypeFromClass (Class<T> type, Class containingClass, Object containingClassInstance)
	{
		Field returnField = null;

		for (Field f : containingClass.getDeclaredFields())
		{
			if (type.isAssignableFrom(f.getType()))
			{
				returnField = f;
				break;
			}
		}

		if (returnField != null)
		{
			try 
			{
				if (!returnField.isAccessible())
				{
					returnField.setAccessible(true);
				}
				
				return (T) returnField.get(containingClassInstance);
			} 

			catch (IllegalArgumentException e) 
			{
				e.printStackTrace();
			} 

			catch (IllegalAccessException e) 
			{
				e.printStackTrace();
			}
		}

		return null;
	}

	public static <T> T getStaticObjectOfTypeFromClass (Class<T> type, Class containingClass)
	{
		Field returnField = null;

		for (Field f : containingClass.getDeclaredFields())
		{
			if (type.isAssignableFrom(f.getType()))
			{
				returnField = f;
				break;
			}
		}

		if (returnField != null)
		{
			try 
			{
				if (!returnField.isAccessible())
				{
					returnField.setAccessible(true);
				}
				
				return (T) returnField.get(null);
			} 

			catch (IllegalArgumentException e) 
			{
				e.printStackTrace();
			} 

			catch (IllegalAccessException e) 
			{
				e.printStackTrace();
			}
		}

		return null;
	}

	private RadixReflect()
	{
	}
}
