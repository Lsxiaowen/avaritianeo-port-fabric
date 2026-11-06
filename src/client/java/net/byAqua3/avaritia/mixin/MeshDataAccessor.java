package net.byAqua3.avaritia.mixin;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MeshData.class)
public interface MeshDataAccessor {
	@Accessor("indexBuffer")
	void avaritia$setIndexBuffer(ByteBufferBuilder.Result indices);
}
