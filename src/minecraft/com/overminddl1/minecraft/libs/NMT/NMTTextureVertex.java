package com.overminddl1.minecraft.libs.NMT;

import net.minecraft.util.Vec3;

public class NMTTextureVertex
{
	public NMTVertex	vertex;
	public float		textureU;
	public float		textureV;
	public Vec3			normal;
	public boolean		textured;

	public NMTTextureVertex(float x, float y, float z, float u, float v)
	{
		this(Vec3.createVectorHelper(x, y, z), u, v);
	}

	public NMTTextureVertex(Vec3 vector, float u, float v)
	{
		this(new NMTVertex(vector), u, v);
	}

	public NMTTextureVertex(NMTVertex vert, float u, float v)
	{
		this(vert, u, v, null);
	}

	public NMTTextureVertex(NMTVertex vert, float u, float v, Vec3 n)
	{
		vertex = vert;
		textureU = u;
		textureV = v;
		normal = n;
		textured = true;
	}

	public NMTTextureVertex setUV(float u, float v)
	{
		textureU = u;
		textureV = v;
		return this;
	}
}
