package com.github.AbrarSyed.SeaCraft.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HandlerClient extends HandlerBase
{

	@Override
	protected void doAction(EntityPlayerMP player, PacketSCBase packet)
	{
		World world = FMLClientHandler.instance().getClient().theWorld;
		packet.actionClient(world, player);
	}
}
