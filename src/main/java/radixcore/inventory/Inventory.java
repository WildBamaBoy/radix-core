package radixcore.inventory;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * This is a simple inventory class containing common functions present in other inventories.
 */
public class Inventory extends InventoryBasic
{
	public Inventory(String name, boolean displayCustomName, int size)
	{
		super(name, displayCustomName, size);
	}

	private int getFirstEmptyStack()
	{
		for (int i = 0; i < getSizeInventory(); i++)
		{
			if (getStackInSlot(i) == null)
			{
				return i;
			}
		}
		return -1;
	}

	public boolean addItemStackToInventory(ItemStack itemStack)
	{
		int slotId;

		if (itemStack.getCount() > 0)
		{
			if (itemStack.isItemDamaged())
			{
				slotId = getFirstEmptyStack();
				
				if (slotId >= 0)
				{
					setInventorySlotContents(slotId, itemStack.copy()); 
					itemStack.setCount(0);
					combinePartialStacks();
					return true;
				}
				
				else
				{
					combinePartialStacks();
					return false;
				}
			}
			
			else
			{
				do
				{
					slotId = itemStack.getCount();
					itemStack.setCount(storePartialItemStack(itemStack));
				}
				while (itemStack.getCount() > 0 && itemStack.getCount() < slotId);
				combinePartialStacks();
				return itemStack.getCount() < slotId;
			}
		}
		
		else
		{
			return false;
		}
	}

	public boolean contains(Class clazz)
	{
		for (int i = 0; i < this.getSizeInventory(); ++i)
		{
			final ItemStack stack = this.getStackInSlot(i);

			if (stack != null)
			{
				final Item item = stack.getItem();

				if (item.getClass() == clazz)
				{
					return true;
				}
			}
		}

		return false;
	}

	public boolean contains(Item item)
	{
		return contains(item.getClass());
	}

	public boolean contains(Block block)
	{
		return contains(block.getClass());
	}

	public boolean containsCountOf(Item item, int threshold)
	{
		int totalCount = 0;
		
		for (int i = 0; i < this.getSizeInventory(); ++i)
		{
			final ItemStack stack = this.getStackInSlot(i);

			if (stack != null)
			{
				final Item stackItem = stack.getItem();

				if (stackItem.getClass() == item.getClass())
				{
					totalCount += stack.getCount();
				}
			}
		}

		return totalCount >= threshold;
	}
	
	public void loadInventoryFromNBT(NBTTagList tagList)
	{
		for (int i = 0; i < this.getSizeInventory(); ++i)
		{
			this.setInventorySlotContents(i, (ItemStack)null);
		}

		for (int i = 0; i < tagList.tagCount(); ++i)
		{
			NBTTagCompound nbt = tagList.getCompoundTagAt(i);
			int slot = nbt.getByte("Slot") & 255;

			if (slot >= 0 && slot < this.getSizeInventory())
			{
				this.setInventorySlotContents(slot, new ItemStack(nbt));
			}
		}
	}

	public NBTTagList saveInventoryToNBT()
	{
		NBTTagList tagList = new NBTTagList();

		for (int i = 0; i < this.getSizeInventory(); ++i)
		{
			ItemStack itemstack = this.getStackInSlot(i);

			if (itemstack != null)
			{
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setByte("Slot", (byte)i);
				itemstack.writeToNBT(nbt);
				tagList.appendTag(nbt);
			}
		}

		return tagList;
	}

	/**
	 * Gets the best quality (max damage) item of the specified type that is in the inventory.
	 *
	 * @param type The class of item that will be returned.
	 * @return The item stack containing the item of the specified type with the highest max damage.
	 */
	public ItemStack getBestItemOfType(Class type)
	{
		return getStackInSlot(getBestItemOfTypeSlot(type));
	}

	public int getBestItemOfTypeSlot(Class type)
	{
		int highestMaxDamage = 0;

		for (int i = 0; i < this.getSizeInventory(); ++i)
		{
			ItemStack stackInInventory = this.getStackInSlot(i);

			if (stackInInventory != null)
			{
				final String itemClassName = stackInInventory.getItem().getClass().getName();

				if (itemClassName.equals(type.getName()) && highestMaxDamage < stackInInventory.getMaxDamage())
				{
					highestMaxDamage = stackInInventory.getMaxDamage();					
					return i;
				}
			}
		}

		return -1;
	}

	/**
	 * Damages item in the provided slot for the specified amount.
	 * 
	 * @return True if the item was destroyed.
	 */
	public boolean damageItem(int slotId, int amount)
	{
		ItemStack stack = getStackInSlot(slotId);

		if (stack != null)
		{
			stack.attemptDamageItem(amount, new Random());

			if (stack.getItemDamage() >= stack.getMaxDamage())
			{
				stack.setCount(0);
				setInventorySlotContents(slotId, null);
				return true;
			}
		}

		return false;
	}

	private void combinePartialStacks()
	{
		for (int i = 0; i < getSizeInventory(); i++)
		{
			final ItemStack currentStack = getStackInSlot(i);
			if (currentStack != null)
			{
				if (currentStack.getCount() != currentStack.getMaxStackSize())
				{
					for (int i2 = 0; i2 < getSizeInventory(); i2++)
					{
						final ItemStack searchingStack = getStackInSlot(i2);
						if (searchingStack != null)
						{
							if (currentStack.getItem() == searchingStack.getItem() && i != i2)
							{
								if (currentStack.getItemDamage() == searchingStack.getItemDamage())
								{
									while (searchingStack.getCount() < searchingStack.getMaxStackSize())
									{
										currentStack.setCount(currentStack.getCount() + 1);
										searchingStack.setCount(currentStack.getCount() - 1);
										if (searchingStack.getCount() == 0)
										{
											setInventorySlotContents(i2, null);
											break;
										}
									}
								}
							}
							else
							{
								continue;
							}
						}
					}
				}
			}
		}
	}

	private int storePartialItemStack(ItemStack itemStack)
	{
		int stackSize = itemStack.getCount();
		if (itemStack.getMaxStackSize() == 1)
		{
			final int slotId = getFirstEmptyStack();
			if (slotId < 0)
			{
				return stackSize;
			}

			if (getStackInSlot(slotId) == null)
			{
				setInventorySlotContents(slotId, itemStack.copy());
			}

			return 0;
		}

		int slotId = storeItemStack(itemStack);

		if (slotId < 0)
		{
			slotId = getFirstEmptyStack();
		}

		if (slotId < 0)
		{
			return stackSize;
		}

		if (getStackInSlot(slotId) == null)
		{
			setInventorySlotContents(slotId, new ItemStack(itemStack.getItem(), 0, itemStack.getItemDamage()));

			if (itemStack.hasTagCompound())
			{
				final ItemStack stack = getStackInSlot(slotId);
				stack.setTagCompound((NBTTagCompound) itemStack.getTagCompound().copy());
				setInventorySlotContents(slotId, stack);
			}
		}

		int itemStackSize = stackSize;

		if (itemStackSize > getStackInSlot(slotId).getMaxStackSize() - getStackInSlot(slotId).getCount())
		{
			itemStackSize = getStackInSlot(slotId).getMaxStackSize() - getStackInSlot(slotId).getCount();
		}

		if (itemStackSize > getInventoryStackLimit() - getStackInSlot(slotId).getCount())
		{
			itemStackSize = getInventoryStackLimit() - getStackInSlot(slotId).getCount();
		}

		if (itemStackSize == 0)
		{
			return stackSize;
		}

		else
		{
			stackSize -= itemStackSize;

			ItemStack oldStack = getStackInSlot(slotId);
			oldStack.setCount(oldStack.getCount() + itemStackSize);
			oldStack.setAnimationsToGo(5);
			setInventorySlotContents(slotId, oldStack);

			return stackSize;
		}
	}

	private int storeItemStack(ItemStack itemStack)
	{
		for (int i = 0; i < this.getSizeInventory(); ++i)
		{
			ItemStack stack = getStackInSlot(i);

			if (stack != null && stack == itemStack && stack.isStackable() && stack.getCount() < stack.getMaxStackSize() && stack.getCount() < getInventoryStackLimit() && (!stack.getHasSubtypes() || stack.getItemDamage() == itemStack.getItemDamage()) && ItemStack.areItemStacksEqual(stack, itemStack))
			{
				return i;
			}
		}

		return -1;
	}

	public int getFirstSlotContainingItem(Item item)
	{
		int slot = 0;

		for (int i = 0; i < getSizeInventory(); i++)
		{
			final ItemStack stack = getStackInSlot(i);
			
			if (stack != null && stack.getItem() == item)
			{
				return slot;
			}
			
			slot++;
		}
		
		return -1;
	}

	public void removeCountOfItem(Item item, int qtyToRemove) 
	{
		for (int i = 0; i < getSizeInventory(); i++)
		{
			final ItemStack stack = getStackInSlot(i);
			
			if (stack != null && stack.getItem() == item)
			{
				while (stack.getCount() != 0)
				{
					stack.setCount(stack.getCount() - 1);
					qtyToRemove--;
					
					if (stack.getCount() == 0)
					{
						setInventorySlotContents(i, null);
					}
					
					if (qtyToRemove == 0)
					{
						break;
					}
				}
			}
		}
	}
}
