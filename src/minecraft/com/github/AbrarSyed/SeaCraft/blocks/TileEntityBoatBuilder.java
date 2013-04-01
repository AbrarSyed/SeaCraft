package com.github.AbrarSyed.SeaCraft.blocks;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;

import com.github.AbrarSyed.SeaCraft.FunctionHelper;
import com.github.AbrarSyed.SeaCraft.SeaCraft;
import com.github.AbrarSyed.SeaCraft.boats.EntityBoatBase;
import com.github.AbrarSyed.SeaCraft.boats.EntityBoatChest;
import com.github.AbrarSyed.SeaCraft.boats.EntityBoatFurnace;
import com.github.AbrarSyed.SeaCraft.boats.EntityBoatKayak;

public class TileEntityBoatBuilder extends TileEntity implements IInventory
{
	private static final String	UNLOCALIZED	= "SeaCraft.boatbuilder";
	private static final String	INVENTORY	= "inventory";

	/**
	 * Reads a tile entity from NBT.
	 */
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		stacks = FunctionHelper.readInventoryFromNBT(nbt.getTagList(INVENTORY), 10);
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setTag(INVENTORY, FunctionHelper.writeInventoryToNBT(stacks));
	}

	/*
	 * INVENTORY STUFF
	 * ---------------------------------------------------------------------------
	 */

	/*
	 * 0 = main input
	 * 1 = front
	 * 2 = center
	 * 3 = back
	 * 4 5 6 = right
	 * 7 8 9 = left
	 */
	private ItemStack[]			stacks			= new ItemStack[10];
	private static final int	SLOT_MAIN		= 0;
	private static final int	SLOT_COM_CENTER	= 2;
	private static final int[]	SLOTS_AUX		= new int[] {1, 3, 4, 5, 6, 7, 8, 9};

	@Override
	public int getSizeInventory()
	{
		return 10;
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		return stacks[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		return FunctionHelper.decrStackSize(i, j, stacks, this);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
		stacks[i] = itemstack;
	}

	@Override
	public String getInvName()
	{
		return UNLOCALIZED;
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return true;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return player.getDistance(xCoord, yCoord, zCoord) <= 10;
	}

	@Override
	public void openChest()
	{
		// useless
	}

	@Override
	public void closeChest()
	{
		// useless
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack)
	{
		return true;
	}

	public boolean canBuild()
	{
		if (stacks[SLOT_MAIN] == null)
			return false;

		if (stacks[SLOT_MAIN].itemID == Item.boat.itemID)
		{
			if (stacks[SLOT_COM_CENTER] == null)
			{
				// kayak
				return true;
			}
			else if (stacks[SLOT_COM_CENTER].itemID == Block.furnaceIdle.blockID)
			{
				// furnace boat.
				return true;
			}
			else if (stacks[SLOT_COM_CENTER].itemID == Block.chest.blockID)
			{
				// chest boat
				return true;
			}
		}

		return false;
	}

	public void build()
	{
		// no spawn on client
		if (this.worldObj.isRemote)
			return;

		EntityBoatBase output = null;
		boolean anchor = false;
		boolean[] clears = new boolean[10];
		Arrays.fill(clears, false);

		if (stacks[SLOT_MAIN].itemID == Item.boat.itemID)
		{
			if (stacks[SLOT_COM_CENTER] == null)
			{
				output = new EntityBoatKayak(this.worldObj);
				clears[SLOT_MAIN] = true;
			}
			else if (stacks[SLOT_COM_CENTER].itemID == Block.furnaceIdle.blockID)
			{
				output = new EntityBoatFurnace(this.worldObj);
				clears[SLOT_MAIN] = clears[SLOT_COM_CENTER] = true;
			}
			else if (stacks[SLOT_COM_CENTER].itemID == Block.chest.blockID)
			{
				// chest boat
				output = new EntityBoatChest(this.worldObj);
				clears[SLOT_MAIN] = clears[SLOT_COM_CENTER] = true;
			}
		}
		
		for (int i : SLOTS_AUX)
		{
			if (stacks[i] != null && stacks[i].itemID == SeaCraft.anchor.itemID)
			{
				clears[i] = true;
				anchor = true;
				break;
			}
		}

		ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata());
		double x = this.xCoord + dir.offsetX * output.width;
		double z = this.zCoord + dir.offsetZ * output.width;
		double y = this.yCoord + dir.offsetY * output.height;

		output.rotationYaw = (float) MathHelper.wrapAngleTo180_double((270f - Math.atan2(x, z) * 180.0f / Math.PI));
		output.setPosition(x, y, z);
		output.setAnchorable(anchor);

		// spawn it.
		this.worldObj.spawnEntityInWorld(output);

		for (int i = 0; i < this.getSizeInventory(); i++)
			if (clears[i])
				stacks[i] = null;
	}
}
