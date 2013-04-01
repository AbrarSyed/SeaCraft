package com.github.AbrarSyed.SeaCraft.items;

import com.github.AbrarSyed.SeaCraft.SeaCraft;

import net.minecraft.item.Item;

public class ItemAnchor extends Item
{
	public ItemAnchor(int par1, boolean hasRope)
	{
		super(par1);
		this.setMaxStackSize(1);
		this.setCreativeTab(SeaCraft.tab);
		this.setNoRepair();
		setHasSubtypes(false);
		setUnlocalizedName(SeaCraft.MODID + ":anchor"+ (hasRope ? "" : "_norope"));
		setCreativeTab(SeaCraft.tab);
	}

}
