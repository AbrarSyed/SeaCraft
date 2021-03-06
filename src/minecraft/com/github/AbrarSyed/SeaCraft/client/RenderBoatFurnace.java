package com.github.AbrarSyed.SeaCraft.client;

import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import com.github.AbrarSyed.SeaCraft.SeaCraft;
import com.github.AbrarSyed.SeaCraft.boats.EntityBoatFurnace;

public class RenderBoatFurnace extends Render
{
	private ModelBoat		modelBoat;
	private ModelFurnace	modelFurnace;

	public RenderBoatFurnace()
	{
		shadowSize = 0.5F;
		modelBoat = new ModelBoat();
		modelFurnace = new ModelFurnace();
	}

	/**
	 * The render method used in RenderBoat that renders the boat model.
	 */
	public void render(EntityBoatFurnace entity, double x, double y, double z, float par8, float par9)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);

		// render lines BEFORE ANYTHING
		if (entity.hitched != null)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			Tessellator tess = Tessellator.instance;
			tess.startDrawing(GL11.GL_LINES);
			tess.addVertex(0, 0, 0);
			tess.addVertex(entity.hitched.posX - entity.posX, entity.hitched.posY - entity.posY, entity.hitched.posZ - entity.posZ);
			tess.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}

		// rotate arround y...
		GL11.glRotatef(180 - par8, 0.0F, 1.0F, 0.0F);

		float f2 = entity.getTimeSinceHit() - par9;
		float f3 = entity.getDamageTaken() - par9;

		if (f3 < 0.0F)
		{
			f3 = 0.0F;
		}

		if (f2 > 0.0F)
		{
			// render roll
			GL11.glRotatef(MathHelper.sin(f2) * f2 * f3 / 10.0F, 1.0F, 0.0F, 0.0F);
		}

		float f4 = 0.75F;
		GL11.glScalef(-1.0F, -1.0F, 1.0F);

		loadTexture("/item/boat.png");
		
		modelBoat.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		
		GL11.glTranslatef(1.4f, 0f, 0f);

		modelBoat.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		if (entity.isBurningFuel())
			loadTexture("/mods/" + SeaCraft.MODID + "/textures/skins/furnaceBlock_on.png");
		else
			loadTexture("/mods/" + SeaCraft.MODID + "/textures/skins/furnaceBlock_off.png");
		
		GL11.glRotatef(-90, 0f, 1.0f, 0f);
		GL11.glTranslatef(-.5f, -.9f, -0.5f);

		modelFurnace.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GL11.glPopMatrix();
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	@Override
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		render((EntityBoatFurnace) par1Entity, par2, par4, par6, par8, par9);
	}
}
