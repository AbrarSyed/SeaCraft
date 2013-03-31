package com.github.AbrarSyed.SeaCraft.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketSC0MountEntity extends PacketSCBase
{
	public final int		entityID;

	/**
	 * FROM CLIENT ONLY!
	 */
	public PacketSC0MountEntity(Entity mounted)
	{
		super();
		entityID = mounted.entityId;
	}

	public PacketSC0MountEntity(ObjectInputStream stream) throws IOException
	{
		super(stream);
		entityID = stream.readInt();
	}

	@Override
	public void writeToStream(ObjectOutputStream stream) throws IOException
	{
		stream.writeInt(entityID);
	}

	@Override
	public int getID()
	{
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void actionClient(World world, EntityPlayerMP player)
	{
		Entity e = world.getEntityByID(entityID);
		player.mountEntity(e);
	}

	@Override
	public void actionServer(World world, EntityPlayerMP player)
	{
		Entity e = world.getEntityByID(entityID);
		player.mountEntity(e);
	}

}
