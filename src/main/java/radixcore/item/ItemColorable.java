package radixcore.item;

import java.util.List;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import radixcore.constant.Color16;

public abstract class ItemColorable extends Item implements IItemColor
{
	public ItemColorable()
	{
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemstack(ItemStack stack, int tintIndex)
	{
		int damage = stack.getItemDamage();
		return Color16.fromId(damage).getRGBValue();
	}

	public String getUnlocalizedName(ItemStack stack)
	{
		int meta = MathHelper.clamp(stack.getItemDamage(), 0, 15);
		return super.getUnlocalizedName() + "." + Color16.fromId(meta).getName();
	}

	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List list)
	{
		for (int i = 0; i < 16; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}
}