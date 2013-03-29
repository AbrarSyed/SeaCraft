package com.github.AbrarSyed.SeaCraft.Boats;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityBoatBase extends Entity
{
	private double	speedMultiplier;
	private int		boatPosRotationIncrements;
	private double	boatX;
	private double	boatY;
	private double	boatZ;
	private double	boatYaw;
	private double	boatPitch;
	
	@SideOnly(Side.CLIENT)
	private double	velocityX;
	@SideOnly(Side.CLIENT)
	private double	velocityY;
	@SideOnly(Side.CLIENT)
	private double	velocityZ;

	public EntityBoatBase(World par1World)
	{
		super(par1World);
		this.speedMultiplier = 0.07D;
		this.preventEntitySpawning = true;
		this.setSize(1.5F, 0.6F);
		this.yOffset = this.height / 2.0F;
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	protected boolean canTriggerWalking()
	{
		return false;
	}

	protected void entityInit()
	{
		this.dataWatcher.addObject(17, new Integer(0));
		this.dataWatcher.addObject(18, new Integer(1));
		this.dataWatcher.addObject(19, new Integer(0));
	}

	/**
	 * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
	 * pushable on contact, like boats or minecarts.
	 */
	public AxisAlignedBB getCollisionBox(Entity par1Entity)
	{
		return par1Entity.boundingBox;
	}

	/**
	 * returns the bounding box for this entity
	 */
	public AxisAlignedBB getBoundingBox()
	{
		return this.boundingBox;
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities when colliding.
	 */
	public boolean canBePushed()
	{
		return true;
	}

	public EntityBoatBase(World par1World, double par2, double par4, double par6)
	{
		this(par1World);
		this.setPosition(par2, par4 + (double) this.yOffset, par6);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = par2;
		this.prevPosY = par4;
		this.prevPosZ = par6;
	}

	/**
	 * Returns the Y offset from the entity's position for any entity riding this one.
	 */
	public double getMountedYOffset()
	{
		return (double) this.height * 0.0D - 0.30000001192092896D;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
	{
		if (this.isEntityInvulnerable())
		{
			return false;
		}
		else if (!this.worldObj.isRemote && !this.isDead)
		{
			this.setForwardDirection(-this.getForwardDirection());
			this.setTimeSinceHit(10);
			this.setDamageTaken(this.getDamageTaken() + par2 * 10);
			this.setBeenAttacked();
			boolean flag = par1DamageSource.getEntity() instanceof EntityPlayer && ((EntityPlayer) par1DamageSource.getEntity()).capabilities.isCreativeMode;

			if (flag || this.getDamageTaken() > getMaxDamage())
			{
				if (this.riddenByEntity != null)
				{
					this.riddenByEntity.mountEntity(this);
				}

				if (!flag)
				{
					this.dropItemsOnBreak();
				}

				this.setDead();
			}

			return true;
		}
		else
		{
			return true;
		}
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
	 */
	public void performHurtAnimation()
	{
		this.setForwardDirection(-this.getForwardDirection());
		this.setTimeSinceHit(10);
		this.setDamageTaken(this.getDamageTaken() * 11);
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	public boolean canBeCollidedWith()
	{
		return !this.isDead;
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
	 * posY, posZ, yaw, pitch
	 */
	public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
	{
		if (this.riddenByEntity == null)
		{
			this.boatPosRotationIncrements = par9 + 5;
		}
		else
		{
			double d3 = par1 - this.posX;
			double d4 = par3 - this.posY;
			double d5 = par5 - this.posZ;
			double d6 = d3 * d3 + d4 * d4 + d5 * d5;

			if (d6 <= 1.0D)
			{
				return;
			}

			this.boatPosRotationIncrements = 3;
		}

		this.boatX = par1;
		this.boatY = par3;
		this.boatZ = par5;
		this.boatYaw = (double) par7;
		this.boatPitch = (double) par8;
		this.motionX = this.velocityX;
		this.motionY = this.velocityY;
		this.motionZ = this.velocityZ;
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Sets the velocity to the args. Args: x, y, z
	 */
	public void setVelocity(double par1, double par3, double par5)
	{
		this.velocityX = this.motionX = par1;
		this.velocityY = this.motionY = par3;
		this.velocityZ = this.motionZ = par5;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate()
	{
		super.onUpdate();

		if (this.getTimeSinceHit() > 0)
		{
			this.setTimeSinceHit(this.getTimeSinceHit() - 1);
		}

		if (this.getDamageTaken() > 0)
		{
			this.setDamageTaken(this.getDamageTaken() - 1);
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		byte b0 = 5;
		double d0 = 0.0D;

		// check on water part.
		for (int i = 0; i < b0; ++i)
		{
			double d1 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double) (i + 0) / (double) b0 - 0.125D;
			double d2 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double) (i + 1) / (double) b0 - 0.125D;
			AxisAlignedBB axisalignedbb = AxisAlignedBB.getAABBPool().getAABB(this.boundingBox.minX, d1, this.boundingBox.minZ, this.boundingBox.maxX, d2, this.boundingBox.maxZ);

			if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.water))
			{
				d0 += 1.0D / (double) b0;
			}
		}

		double groundMotion = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		double d4;
		double d5;

		// minimum splash speed.
		if (groundMotion > 0.26249999999999996D)
		{
			d4 = Math.cos((double) this.rotationYaw * Math.PI / 180.0D);
			d5 = Math.sin((double) this.rotationYaw * Math.PI / 180.0D);

			for (int j = 0; (double) j < 1.0D + groundMotion * 60.0D; ++j)
			{
				double d6 = (double) (this.rand.nextFloat() * 2.0F - 1.0F);
				double d7 = (double) (this.rand.nextInt(2) * 2 - 1) * 0.7D;
				double d8;
				double d9;

				// random? wut?
				if (this.rand.nextBoolean())
				{
					d8 = this.posX - d4 * d6 * 0.8D + d5 * d7;
					d9 = this.posZ - d5 * d6 * 0.8D - d4 * d7;
					this.worldObj.spawnParticle("splash", d8, this.posY - 0.125D, d9, this.motionX, this.motionY, this.motionZ);
				}
				else
				{
					d8 = this.posX + d4 + d5 * d6 * 0.7D;
					d9 = this.posZ + d5 - d4 * d6 * 0.7D;
					this.worldObj.spawnParticle("splash", d8, this.posY - 0.125D, d9, this.motionX, this.motionY, this.motionZ);
				}
			}
		}

		double d10;
		double d11;

		// non-controlled movement
		if (this.worldObj.isRemote && this.riddenByEntity == null)
		{
			if (this.boatPosRotationIncrements > 0)
			{
				d4 = this.posX + (this.boatX - this.posX) / (double) this.boatPosRotationIncrements;
				d5 = this.posY + (this.boatY - this.posY) / (double) this.boatPosRotationIncrements;
				d11 = this.posZ + (this.boatZ - this.posZ) / (double) this.boatPosRotationIncrements;
				d10 = MathHelper.wrapAngleTo180_double(this.boatYaw - (double) this.rotationYaw);
				this.rotationYaw = (float) ((double) this.rotationYaw + d10 / (double) this.boatPosRotationIncrements);
				this.rotationPitch = (float) ((double) this.rotationPitch + (this.boatPitch - (double) this.rotationPitch) / (double) this.boatPosRotationIncrements);
				--this.boatPosRotationIncrements;
				this.setPosition(d4, d5, d11);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			}
			else
			{
				d4 = this.posX + this.motionX;
				d5 = this.posY + this.motionY;
				d11 = this.posZ + this.motionZ;
				this.setPosition(d4, d5, d11);

				if (this.onGround)
				{
					this.motionX *= 0.5D;
					this.motionY *= 0.5D;
					this.motionZ *= 0.5D;
				}

				this.motionX *= 0.9900000095367432D;
				this.motionY *= 0.949999988079071D;
				this.motionZ *= 0.9900000095367432D;
			}
		}
		// controlled movement?
		else
		{
			if (d0 < 1.0D)
			{
				d4 = d0 * 2.0D - 1.0D;
				this.motionY += 0.03999999910593033D * d4;
			}
			else
			{
				if (this.motionY < 0.0D)
				{
					this.motionY /= 2.0D;
				}

				this.motionY += 0.007000000216066837D;
			}

			if (this.riddenByEntity != null)
			{
				this.motionX += this.riddenByEntity.motionX * this.speedMultiplier;
				this.motionZ += this.riddenByEntity.motionZ * this.speedMultiplier;
			}

			d4 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

			if (d4 > 0.35D)
			{
				d5 = 0.35D / d4;
				this.motionX *= d5;
				this.motionZ *= d5;
				d4 = 0.35D;
			}

			if (d4 > groundMotion && this.speedMultiplier < 0.35D)
			{
				this.speedMultiplier += (0.35D - this.speedMultiplier) / 35.0D;

				if (this.speedMultiplier > 0.35D)
				{
					this.speedMultiplier = 0.35D;
				}
			}
			else
			{
				this.speedMultiplier -= (this.speedMultiplier - 0.07D) / 35.0D;

				if (this.speedMultiplier < 0.07D)
				{
					this.speedMultiplier = 0.07D;
				}
			}

			if (this.onGround)
			{
				this.motionX *= 0.5D;
				this.motionY *= 0.5D;
				this.motionZ *= 0.5D;
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);

			if (this.isCollidedHorizontally && groundMotion > 0.2D)
			{
				if (!this.worldObj.isRemote)
				{
					this.setDead();
					this.dropItemsOnCrash();
				}
			}
			else
			{
				this.motionX *= 0.9900000095367432D;
				this.motionY *= 0.949999988079071D;
				this.motionZ *= 0.9900000095367432D;
			}

			this.rotationPitch = 0.0F;
			d5 = (double) this.rotationYaw;
			d11 = this.prevPosX - this.posX;
			d10 = this.prevPosZ - this.posZ;

			if (d11 * d11 + d10 * d10 > 0.001D)
			{
				d5 = (double) ((float) (Math.atan2(d10, d11) * 180.0D / Math.PI));
			}

			double d12 = MathHelper.wrapAngleTo180_double(d5 - (double) this.rotationYaw);

			if (d12 > 20.0D)
			{
				d12 = 20.0D;
			}

			if (d12 < -20.0D)
			{
				d12 = -20.0D;
			}

			this.rotationYaw = (float) ((double) this.rotationYaw + d12);
			this.setRotation(this.rotationYaw, this.rotationPitch);

			if (!this.worldObj.isRemote)
			{
				List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
				int l;

				if (list != null && !list.isEmpty())
				{
					for (l = 0; l < list.size(); ++l)
					{
						Entity entity = (Entity) list.get(l);

						if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityBoatBase)
						{
							entity.applyEntityCollision(this);
						}
					}
				}

				for (l = 0; l < 4; ++l)
				{
					int i1 = MathHelper.floor_double(this.posX + ((double) (l % 2) - 0.5D) * 0.8D);
					int j1 = MathHelper.floor_double(this.posZ + ((double) (l / 2) - 0.5D) * 0.8D);

					for (int k1 = 0; k1 < 2; ++k1)
					{
						int l1 = MathHelper.floor_double(this.posY) + k1;
						int i2 = this.worldObj.getBlockId(i1, l1, j1);

						if (i2 == Block.snow.blockID)
						{
							this.worldObj.setBlockToAir(i1, l1, j1);
						}
						else if (i2 == Block.waterlily.blockID)
						{
							this.worldObj.destroyBlock(i1, l1, j1, true);
						}
					}
				}

				if (this.riddenByEntity != null && this.riddenByEntity.isDead)
				{
					this.riddenByEntity = null;
				}
			}
		}
	}

	public void updateRiderPosition()
	{
		if (this.riddenByEntity != null)
		{
			double d0 = Math.cos((double) this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			double d1 = Math.sin((double) this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			this.riddenByEntity.setPosition(this.posX + d0, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d1);
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected abstract void writeEntityToNBT(NBTTagCompound par1NBTTagCompound);

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected abstract void readEntityFromNBT(NBTTagCompound par1NBTTagCompound);

	@SideOnly(Side.CLIENT)
	public float getShadowSize()
	{
		return 0.0F;
	}

	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
	 */
	public boolean interact(EntityPlayer par1EntityPlayer)
	{
		// already has something riding? DENIED
		if (this.riddenByEntity != null &&
				// ridden by player.
				this.riddenByEntity instanceof EntityPlayer &&
				// ridden by player thats not this player
				this.riddenByEntity != par1EntityPlayer)
		{
			return true;
		}
		else if (this.isMountableByPlayer())
		{
			if (!this.worldObj.isRemote)
			{
				par1EntityPlayer.mountEntity(this);
			}

			return true;
		}
		
		return true;
	}

	/**
	 * Sets the damage taken from the last hit.
	 */
	public void setDamageTaken(int par1)
	{
		this.dataWatcher.updateObject(19, Integer.valueOf(par1));
	}

	/**
	 * Gets the damage taken from the last hit.
	 */
	public int getDamageTaken()
	{
		return this.dataWatcher.getWatchableObjectInt(19);
	}

	/**
	 * Sets the time to count down from since the last time entity was hit.
	 */
	public void setTimeSinceHit(int par1)
	{
		this.dataWatcher.updateObject(17, Integer.valueOf(par1));
	}

	/**
	 * Gets the time since the last hit.
	 */
	public int getTimeSinceHit()
	{
		return this.dataWatcher.getWatchableObjectInt(17);
	}

	/**
	 * Sets the forward direction of the entity.
	 */
	public void setForwardDirection(int par1)
	{
		this.dataWatcher.updateObject(18, Integer.valueOf(par1));
	}

	/**
	 * Gets the forward direction of the entity.
	 */
	public int getForwardDirection()
	{
		return this.dataWatcher.getWatchableObjectInt(18);
	}
	
	/**
	 * Called when the boat is broken.
	 * Spawn drops here.
	 */
	public abstract void dropItemsOnBreak();
	
	/**
	 * Called when the boat crashes horizontally.
	 * Spawn drops here.
	 */
	public abstract void dropItemsOnCrash();
	
	public abstract boolean isMountableByPlayer();
	
	/**
	 * all things on boat + the boats weight.
	 * @return
	 */
	public abstract int getCurrentWeight();
	
	/**
	 * The weight of the boat empty.
	 * @return
	 */
	public abstract int getBaseWeight();
	
	/**
	 * Vanilla boats are 40
	 * @return
	 */
	public abstract int getMaxDamage();
	
	/**
	 * Vanilla boats are 1.
	 * The ammount of damage to remove each tick.
	 * Players do 10 per hit.
	 * @return
	 */
	public abstract int getRegenRate();
}
